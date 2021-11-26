package com.example.crimeapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_crime_detail.*
import java.util.*

private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"

class CrimeDetailFragment : Fragment() {

    private lateinit var crimeTitle: EditText
    private lateinit var dateButton: Button
    private lateinit var isSolvedCheckBox: CheckBox
    private lateinit var crime: Crime

    private val crimeDetailViewModel : CrimeDetailViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val crimeId:UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID

        crimeDetailViewModel.loadCrime(crimeId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_crime_detail, container, false)

        crimeTitle = view.findViewById(R.id.crime_title_textview)
        dateButton = view.findViewById(R.id.crime_date_textview)
        isSolvedCheckBox = view.findViewById(R.id.crime_is_solved_checkbox)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        crimeDetailViewModel.CrimeLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            crime -> crime?.let {
                this.crime = crime
                updateUI()
            }
        })
    }

    private fun updateUI() {
        crimeTitle.setText(crime.mtitle)
        dateButton.setText(crime.mdate.toString())
        isSolvedCheckBox.apply {
            isSolvedCheckBox.isChecked = crime.misSolved
            jumpDrawablesToCurrentState()
        }
    }

    companion object {
        fun newInstance(crimeId:UUID): CrimeDetailFragment {
            val args = Bundle().apply{
                putSerializable(ARG_CRIME_ID,crimeId)
            }

            return CrimeDetailFragment().apply {
                arguments = args
            }
        }

    }

    override fun onStop() {
        super.onStop()
        crime.mtitle = crimeTitle.text.toString()
        crime.misSolved = crime_is_solved_checkbox.isChecked

        crimeDetailViewModel.saveCrime(crime)
    }
}
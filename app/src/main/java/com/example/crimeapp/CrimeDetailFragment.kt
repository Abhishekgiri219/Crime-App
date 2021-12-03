package com.example.crimeapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_crime_detail.*
import java.text.DateFormat
import java.util.*

private const val TAG = "CrimeFragment"
private const val ARG_CRIME_ID = "crime_id"

class CrimeDetailFragment : Fragment() {

    private lateinit var crimeTitle: EditText
    private lateinit var dateButton: Button
    private lateinit var isSolvedCheckBox: CheckBox
    private lateinit var crime: Crime
    private lateinit var sendReportButton: Button
    private lateinit var suspectButton: Button
    private var REQUEST_CONTACT = 1
    private lateinit var launchForResult : ActivityResultLauncher<Intent>

    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID

        crimeDetailViewModel.loadCrime(crimeId)

        launchForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            run {
                val contactUri: Uri? = result.data?.data
                // Specify which fields you want your query to return values for
                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                // Perform your query - the contactUri is like a "where" clause here
                val cursor = contactUri?.let {
                    requireActivity().contentResolver
                            .query(it, queryFields, null, null, null)
                }
                cursor?.use {
                    // Verify cursor contains at least one result
                    if (it.count == 0) {
                        return@registerForActivityResult
                    }
                    // Pull out the first column of the first row of data -
                    // that is your suspect's name
                    it.moveToFirst()
                    val suspect = it.getString(0)
                    crime.suspect = suspect
                    crimeDetailViewModel.saveCrime(crime)
                    suspectButton.text = suspect
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_crime_detail, container, false)

        crimeTitle = view.findViewById(R.id.crime_title_textview)
        dateButton = view.findViewById(R.id.crime_date_textview)
        isSolvedCheckBox = view.findViewById(R.id.crime_is_solved_checkbox)
        sendReportButton = view.findViewById(R.id.send_report_button)
        suspectButton = view.findViewById(R.id.suspect_button)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        crimeDetailViewModel.CrimeLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { crime ->
            crime?.let {
                this.crime = crime
                updateUI()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        sendReportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }.also { intent ->
                val chooserIntent = Intent.createChooser(intent, getString(R.string.send_report))

                startActivity(chooserIntent)
            }
        }

        suspectButton.apply {
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

            setOnClickListener {
                launchForResult.launch(intent)
            }
        }
    }

    private fun updateUI() {
        crimeTitle.setText(crime.mtitle)
        dateButton.setText(crime.mdate.toString())
        isSolvedCheckBox.apply {
            isSolvedCheckBox.isChecked = crime.misSolved
            jumpDrawablesToCurrentState()
        }
        if (crime.suspect.isNotEmpty()) {
            suspectButton.text = crime.suspect
        }
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.misSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dateString = android.text.format.DateFormat.format("EEE, MM , dd", crime.mdate).toString()

        val suspectString = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else getString(R.string.crime_report_suspect, crime.suspect)

        return getString(R.string.crime_report, crime.mtitle, dateString, solvedString, suspectString)
    }

    companion object {
        fun newInstance(crimeId: UUID): CrimeDetailFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
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
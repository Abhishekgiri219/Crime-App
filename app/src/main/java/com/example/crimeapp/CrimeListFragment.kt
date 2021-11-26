package com.example.crimeapp

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import java.util.*

class CrimeListFragment : Fragment() {

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        return inflater.inflate(R.menu.fragment_crime_list,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.new_crime -> {
                var crime = Crime()
                crimeListViewModel.insertCrime(crime)
                callbacks?.onCrimeSelected(crime.id)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    interface Callbacks{
        fun onCrimeSelected(id: UUID)
    }

    var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    private lateinit var crimeRecyclerView: RecyclerView
    private var crimeListAdapter: CrimeListAdapter? = CrimeListAdapter()

    private val crimeListViewModel: CrimeListViewModel by lazy{
        ViewModelProvider(requireActivity()).get(CrimeListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        crimeRecyclerView = view.findViewById(R.id.fragment_recyler_view)

        //setting a layout manager
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        // setting an adapter
        crimeRecyclerView.adapter = crimeListAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        crimeListViewModel.CrimeListLiveData.observe(viewLifecycleOwner, Observer { 
            crimes -> crimes?.let{
                updateUI(crimes)
            }
        })
    }

    private fun updateUI(crimes: List<Crime>) {
        crimeListAdapter?.submitList(crimes)
        crimeRecyclerView.adapter = crimeListAdapter
    }

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    inner class CrimeListAdapter() : androidx.recyclerview.widget.ListAdapter<Crime, CrimeViewHolder>(DiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_view, parent, false)

            return CrimeViewHolder(view)
        }

        override fun onBindViewHolder(holder: CrimeViewHolder, position: Int) {
            holder.bind(currentList[position])
        }

        override fun getItemCount(): Int {
            return currentList.size
        }

    }

    inner class DiffCallback : DiffUtil.ItemCallback<Crime>(){
        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem == newItem
        }
    }

    // viewHolder class
    inner class CrimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private lateinit var crime: Crime

        init{
            itemView.setOnClickListener(this)
        }

        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title_textview)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date_textview)
        private val isSolvedCheckBox: ImageView = itemView.findViewById(R.id.isSolved_checkBox)

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.mtitle
            dateTextView.text = this.crime.mdate.toString()
            if(!this.crime.misSolved){
                isSolvedCheckBox.visibility = View.INVISIBLE
            }
        }

        override fun onClick(v: View?) {
            callbacks?.onCrimeSelected(crime.id)
        }
    }
}


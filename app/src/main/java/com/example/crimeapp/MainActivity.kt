package com.example.crimeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.crimeapp.R
import com.example.crimeapp.CrimeListFragment
import java.util.*

class MainActivity : AppCompatActivity(), CrimeListFragment.Callbacks {

//    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (fragment == null) {
            fragment = CrimeListFragment.newInstance()

            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit()
        }
    }

    override fun onCrimeSelected(id: UUID) {
        val fragment = CrimeDetailFragment.newInstance(id)

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()

    }
}
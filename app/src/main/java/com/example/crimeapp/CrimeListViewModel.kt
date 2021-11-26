package com.example.crimeapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.collections.ArrayList

class CrimeListViewModel : ViewModel() {
    private val crimeRepository = CrimeRepository.get()
    var CrimeListLiveData = crimeRepository.getCrimes()

    fun insertCrime(crime: Crime){
        crimeRepository.insertCrime(crime)
    }
}
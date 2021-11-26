package com.example.crimeapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class CrimeDetailViewModel: ViewModel() {
    val crimeRepository = CrimeRepository.get()
    val crimeIdLiveData = MutableLiveData<UUID>()

    var CrimeLiveData: LiveData<Crime?> =
            Transformations.switchMap(crimeIdLiveData) { crimeId ->
                crimeRepository.getCrime(crimeId)
            }

    fun loadCrime(crimeId : UUID){
        crimeIdLiveData.value = crimeId
    }

    fun saveCrime(crime: Crime){
        crimeRepository.updateCrime(crime)
    }

}
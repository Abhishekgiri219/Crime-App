package com.example.crimeapp

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.crimeapp.database.CrimeDao
import com.example.crimeapp.database.CrimeDatabase
import java.util.*
import java.util.concurrent.Executors

private val DATABASE_NAME = "crime-database"

class CrimeRepository private constructor(context: Context){

    private val database: CrimeDatabase = Room.databaseBuilder(
            context.applicationContext,
            CrimeDatabase::class.java,
            DATABASE_NAME
    ).build()

    private val crimeDao = database.crimeDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getCrimes(): LiveData< List<Crime> > = crimeDao.getCrimes()
    fun getCrime(id: UUID): LiveData< Crime? > = crimeDao.getCrime(id)

    fun updateCrime(crime:Crime){
        executor.execute{
            crimeDao.updateCrime(crime)
        }
    }

    fun insertCrime(crime: Crime){
        executor.execute{
            crimeDao.insertCrime(crime)
        }
    }

    companion object {
        private var INSTANCE: CrimeRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get():CrimeRepository {
            return INSTANCE ?: throw IllegalStateException(" must be initialized first ")
        }
    }
}
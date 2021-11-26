package com.example.crimeapp

import android.app.Application

class CrimeappApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}
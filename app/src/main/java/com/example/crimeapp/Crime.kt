package com.example.crimeapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Crime(@PrimaryKey val id: UUID = UUID.randomUUID(),
                 var mtitle: String? = "",
                 var mdate: Date?= Date(),
                 var misSolved: Boolean = false){

    override fun toString(): String {
        return super.toString()
    }
}
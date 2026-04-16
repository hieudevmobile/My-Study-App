package com.example.mystudyapp.school_schedule

import com.example.mystudyapp.data_school_schedule.DataSubject

class SortListTimeSubject(private val list: MutableList<DataSubject>) {
    val timeBegin = list.map { it.time_Start }

}
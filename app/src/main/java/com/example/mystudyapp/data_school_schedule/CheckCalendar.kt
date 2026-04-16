package com.example.mystudyapp.data_school_schedule

class CheckCalendar {
    suspend fun checkDuplicateSchedule(
        startDate: String,
        startTime: String,
        endTime: String,
        tenMon: String,
        listWeek: List<Int>
    ): String {
        // Firebase is temporarily disabled.
        return "Không có trùng lặp"
    }
}
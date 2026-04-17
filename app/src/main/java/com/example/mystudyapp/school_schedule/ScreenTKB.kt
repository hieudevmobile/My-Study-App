package com.example.mystudyapp.school_schedule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mystudyapp.R
import com.example.mystudyapp.databinding.ScreenTkbBinding
import com.google.android.material.tabs.TabLayoutMediator

class ScreenTKB : AppCompatActivity() {

    private lateinit var binding: ScreenTkbBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ScreenTkbBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewCalendar.adapter =
            ViewPaperAdapterSchudeles(supportFragmentManager, lifecycle)

        TabLayoutMediator(binding.tabCalendar, binding.viewCalendar) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tkb_tab_calendar)
                1 -> getString(R.string.tkb_tab_create)
                2 -> getString(R.string.tkb_tab_edit)
                else -> ""
            }
        }.attach()
    }
}

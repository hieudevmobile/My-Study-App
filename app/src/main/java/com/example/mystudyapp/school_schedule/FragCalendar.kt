package com.example.mystudyapp.school_schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.mystudyapp.R
import com.example.mystudyapp.data_school_schedule.CustomAdaterDataSubject
import com.example.mystudyapp.data_school_schedule.DataSubject
import com.example.mystudyapp.data_school_schedule.DayoftheWeeks
import com.example.mystudyapp.data_school_schedule.DesignListSubject
import com.example.mystudyapp.databinding.FragmentCalendarBinding
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

class FragCalendar : Fragment(), CalendarAdapter.OnItemListener,
    CustomAdaterDataSubject.OnItemClickListener {
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var monthYearText: TextView
    private lateinit var calendarRecyclerView: RecyclerView
    private var selectedDate: LocalDate = LocalDate.now() // Current selected month
    private var dayPresent = ""
    private lateinit var calendarAdapter: CalendarAdapter
    private val sharedViewModel: SharedViewModel by lazy {
        ViewModelProvider(requireActivity())[SharedViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        binding.datePresent.text = formatVietnameseDate(selectedDate)
        binding.dsSubject.addItemDecoration(DesignListSubject(22))
        dayPresent = binding.datePresent.text.toString()
        initWidgets()
        setMonthView()
        // Set up navigation buttons
        binding.prevButton.setOnClickListener {
            selectedDate = selectedDate.minusMonths(1)
            setMonthView()
        }
        binding.nextButton.setOnClickListener {
            selectedDate = selectedDate.plusMonths(1)
            setMonthView()
        }
        return binding.root
    }

    // Initialize UI widgets
    private fun initWidgets() {
        calendarRecyclerView = binding.calendarRecyclerView
        monthYearText = binding.monthYearTV
    }

    // Set up the calendar view for the selected month
    fun setMonthView() {
        monthYearText.text = monthYearFromDate(selectedDate)
        val daysInMonth = daysInMonthArray(selectedDate)
        // Firebase is temporarily disabled. Show calendar without remote schedule marks.
        updateCalendarAdapter(daysInMonth)
    }

    // Helper method to update RecyclerView with calendar data
    private fun updateCalendarAdapter(daysInMonth: ArrayList<DayoftheWeeks>) {
        calendarAdapter = CalendarAdapter(daysInMonth, this)
        calendarRecyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
        calendarRecyclerView.adapter = calendarAdapter
    }

    // Generate array of days for the selected month
    private fun daysInMonthArray(date: LocalDate): ArrayList<DayoftheWeeks> {
        val daysInMonthArray = ArrayList<DayoftheWeeks>()
        val yearMonth = YearMonth.from(date)
        val daysInMonth = yearMonth.lengthOfMonth()
        val dayNames = arrayListOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
        dayNames.forEach { daysInMonthArray.add(DayoftheWeeks(it)) }

        val firstOfMonth = date.withDayOfMonth(1)
        val leadingBlanks = firstOfMonth.dayOfWeek.value - 1

        for (i in 1..42) {
            if (i <= leadingBlanks || i > daysInMonth + leadingBlanks) {
                daysInMonthArray.add(DayoftheWeeks(""))
            } else {
                daysInMonthArray.add(DayoftheWeeks((i - leadingBlanks).toString()))
            }
        }

        // Remove extra row if the first week is empty
        if (daysInMonthArray[13].dayText.isEmpty() && daysInMonthArray.size > 13) {
            for (i in 7..13) {
                daysInMonthArray.removeAt(7)
            }
        }
        return daysInMonthArray
    }

    // Format month and year for display
    private fun monthYearFromDate(date: LocalDate): String {
        val formatter =
            DateTimeFormatter.ofPattern("LLLL yyyy", Locale.forLanguageTag("vi"))
        return date.format(formatter)
    }

    // Handle click on a calendar day
    override fun onItemClick(position: Int, dayText: String) {
        if (dayText.isNotEmpty() && position > 6) {
            val message = "Selected Date $dayText ${monthYearFromDate(selectedDate)}"
            binding.datePresent.text = "Ngày $dayText tháng ${selectedDate.monthValue}"
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            // Firebase is temporarily disabled. Show empty subject list.
            val adapter = CustomAdaterDataSubject(requireActivity(), emptyList(), this)
            binding.dsSubject.layoutManager = LinearLayoutManager(requireContext())
            binding.dsSubject.adapter = adapter
        }
    }

    fun formatVietnameseDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("'Ngày' d 'tháng' M")
        return date.format(formatter)
    }

    override fun onItemClick(position: Int, item: DataSubject, list: List<DataSubject>) {
        val viewPager = requireActivity().findViewById<ViewPager2>(R.id.viewCalendar)
        if (viewPager != null) {
            viewPager.setCurrentItem(2, true)
            sharedViewModel.setSelectedSubject(item)
        } else {
            Toast.makeText(requireContext(), "ViewPager not found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        setMonthView()
    }

}
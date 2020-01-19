package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.threeten.bp.LocalDate

class MainViewModel : ViewModel() {
    private val _selectedMonth = MutableLiveData<LocalDate>()
    val selectedMonth: LiveData<LocalDate> = _selectedMonth

    fun setSelectedMonth(date: LocalDate) {
        _selectedMonth.value = date
    }

    val months: List<LocalDate>
        get() {
            val now = LocalDate.now().withDayOfMonth(1)

            val muteMonths = mutableListOf<LocalDate>()
            muteMonths.add(LocalDate.ofEpochDay(0))
            muteMonths.addAll((1..12).map { index ->
                now.minusMonths(now.monthValue - index.toLong())
            })
            muteMonths.add(LocalDate.ofEpochDay(0))
            return muteMonths
        }
}

package com.example.myapplication


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.month_item.*
import org.threeten.bp.LocalDate

class MainFragment : Fragment() {
    private lateinit var mainViewModel: MainViewModel

    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        month_list.adapter = MainAdapter(mainViewModel.months, context) { date ->
            mainViewModel.setSelectedMonth(date)
            scrollToMonth(date)
        }
        month_list.layoutManager = layoutManager

        initializeSelectedMonth()

        decrement_month.setOnClickListener {
            mainViewModel.selectedMonth.value?.let { currentDate ->
                val date = currentDate.minusMonths(1)
                if (mainViewModel.months.indexOf(date) >= 0) {
                    mainViewModel.setSelectedMonth(date)
                    scrollToMonth(date)
                }
            }
        }

        increment_month.setOnClickListener {
            mainViewModel.selectedMonth.value?.let { currentDate ->
                val date = currentDate.plusMonths(1)
                if (mainViewModel.months.indexOf(date) >= 0) {
                    mainViewModel.setSelectedMonth(date)
                    scrollToMonth(date)
                }
            }
        }

        val snapHelper = CustomLinearSnapHelper(context)
        snapHelper.attachToRecyclerView(month_list)
        month_list.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                    val offset = (month_list.width / month_item.width - 1) / 2
                    val position = layoutManager.findFirstCompletelyVisibleItemPosition() + offset
                    if (position in 0 until mainViewModel.months.size &&
                        mainViewModel.months[position] != mainViewModel.selectedMonth.value) {
                        when (position) {
                            0 -> {
                                mainViewModel.setSelectedMonth(mainViewModel.months[1])
                                scrollToMonth(mainViewModel.months[1])
                            }
                            mainViewModel.months.size - 1 -> {
                                mainViewModel.setSelectedMonth(mainViewModel.months[position - 1])
                                scrollToMonth(mainViewModel.months[position - 1])
                            }
                            else -> mainViewModel.setSelectedMonth(mainViewModel.months[position])
                        }
                    }
                }
            }
        })
    }

    private fun initializeSelectedMonth() {
        if (mainViewModel.selectedMonth.value == null) {
            val now = mainViewModel.months[LocalDate.now().monthValue]
            mainViewModel.setSelectedMonth(now)
            scrollToMonth(now)
        }
    }

    private fun scrollToMonth(month: LocalDate) {
        var width = month_list.width

        if (width > 0) {
            context?.resources?.getDimensionPixelSize(R.dimen.month_item_width)?.let { monthWidth ->
                layoutManager.scrollToPositionWithOffset(mainViewModel.months.indexOf(month), width / 2 - monthWidth / 2)
            }
        } else {
            val vto = month_list.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    month_list.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    width = month_list.width
                    context?.resources?.getDimensionPixelSize(R.dimen.month_item_width)?.let { monthWidth ->
                        layoutManager.scrollToPositionWithOffset(mainViewModel.months.indexOf(month), width / 2 - monthWidth / 2)
                    }
                }
            })
        }
    }
}

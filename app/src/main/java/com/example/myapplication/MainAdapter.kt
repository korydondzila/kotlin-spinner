package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.month_item.view.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.TextStyle
import java.util.*

class MainAdapter(private val items : List<LocalDate>, private val context: Context?,
                  private val monthClickCallback: ((LocalDate) -> Unit)?)
    : RecyclerView.Adapter<ViewHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.month_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in 1 until (itemCount - 1)) {
            val month = items[position].month.getDisplayName(TextStyle.FULL, Locale.US)
            holder.monthLabel.text = month
            holder.monthLabel.contentDescription = month
            holder.itemView.setOnClickListener {
                monthClickCallback?.invoke(items[position])
            }
        } else {
            holder.monthLabel.text = ""
            holder.monthLabel.contentDescription = ""
            holder.itemView.setOnClickListener {}
        }
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val monthLabel: TextView = view.month_label
}

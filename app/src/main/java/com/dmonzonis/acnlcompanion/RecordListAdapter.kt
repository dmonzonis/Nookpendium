package com.dmonzonis.acnlcompanion

import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import kotlinx.android.synthetic.main.record_row.view.*

class RecordListAdapter(private val records: List<Record>) :
    RecyclerView.Adapter<RecordListAdapter.RecordHolder>() {

    val PREFS_FILENAME = "com.dmonzonis.acnlcompanion.prefs"
    var prefs: SharedPreferences? = null

    class RecordHolder(v: View) : RecyclerView.ViewHolder(v) {
        val checkBox: CheckBox = v.findViewById(R.id.checkbox_captured)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.record_row, parent, false)

        // Initialize shared preferences
        prefs = parent.context.getSharedPreferences(PREFS_FILENAME, 0)

        return RecordHolder(inflatedView)
    }

    override fun getItemCount() = records.size

    override fun onBindViewHolder(holder: RecordHolder, position: Int) {
        val record: Record = records[position]
        holder.itemView.text_name.text = record.name
        holder.itemView.text_price.text = record.price
        holder.itemView.text_season.text = record.season

        // Get captured state from shared preferences if it hasn't been loaded yet
        if (record.captured == null) {
            record.captured = prefs?.getBoolean(record.id, false) ?: false
        }
        holder.itemView.checkbox_captured.isChecked = record.captured!!

        // Set checkbox listener to save captured state to shared preferences
        holder.checkBox.setOnClickListener {
            val state = holder.checkBox.isChecked
            record.captured = state
            val editor: SharedPreferences.Editor? = prefs?.edit()
            editor?.putBoolean(record.id, state)
            editor?.apply()
        }
    }

}
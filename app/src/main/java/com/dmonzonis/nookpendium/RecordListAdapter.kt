package com.dmonzonis.nookpendium

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.record_row.view.*

class RecordListAdapter() :
    RecyclerView.Adapter<RecordListAdapter.RecordHolder>() {

    private lateinit var prefsFilename: String
    private lateinit var context: Context
    private var sharedPrefs: SharedPreferences? = null
    private var records: List<Record> = listOf()

    class RecordHolder(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.record_row, parent, false)

        // Initialize shared preferences
        context = parent.context
        prefsFilename = context.getString(R.string.shared_prefs)
        sharedPrefs = context.getSharedPreferences(prefsFilename, 0)

        return RecordHolder(inflatedView)
    }

    override fun getItemCount() = records.size

    fun setRecords(records: List<Record>) {
        this.records = records
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecordHolder, position: Int) {
        val record: Record = records[position]
        holder.itemView.text_name.text = context.getString(record.name)
        holder.itemView.text_price.text = record.price?.toString() ?: "?"
        holder.itemView.img_picture.setImageResource(record.imageId)
        holder.itemView.text_time.text = record.time
        holder.itemView.text_location.text = context.getString(record.location)

        // Get captured state from shared preferences if it hasn't been loaded yet
        if (record.captured == null) {
            record.captured = sharedPrefs?.getBoolean("captured_" + record.id, false) ?: false
        }
        if (record.donated == null) {
            record.donated = sharedPrefs?.getBoolean("donated_" + record.id, false) ?: false
        }
        holder.itemView.checkbox_captured.isChecked = record.captured!!
        holder.itemView.checkbox_donated.isChecked = record.donated!!

        // Set checkbox listener to save captured state to shared preferences
        holder.itemView.checkbox_captured.setOnClickListener {
            val state = it.checkbox_captured.isChecked
            record.captured = state
            sharedPrefs?.edit()?.putBoolean("captured_" + record.id, state)?.apply()
        }

        holder.itemView.checkbox_donated.setOnClickListener {
            val state = it.checkbox_donated.isChecked
            record.donated = state
            sharedPrefs?.edit()?.putBoolean("donated_" + record.id, state)?.apply()
        }
    }
}
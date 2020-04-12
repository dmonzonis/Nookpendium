package com.dmonzonis.nookpendium

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
    private var sharedPrefs: SharedPreferences? = null
    private var records: List<Record> = listOf()

    class RecordHolder(v: View) : RecyclerView.ViewHolder(v) {
        val checkBox: CheckBox = v.findViewById(R.id.checkbox_captured)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.record_row, parent, false)

        // Initialize shared preferences
        prefsFilename = parent.context.getString(R.string.sharedPrefs)
        sharedPrefs = parent.context.getSharedPreferences(prefsFilename, 0)

        return RecordHolder(inflatedView)
    }

    override fun getItemCount() = records.size

    fun setRecords(records: List<Record>) {
        this.records = records
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecordHolder, position: Int) {
        val record: Record = records[position]
        holder.itemView.text_name.text = record.name
        holder.itemView.text_price.text = record.price?.toString() ?: "?"
        holder.itemView.img_picture.setImageResource(record.imageId)

        // Get captured state from shared preferences if it hasn't been loaded yet
        if (record.captured == null) {
            record.captured = sharedPrefs?.getBoolean(record.id, false) ?: false
        }
        holder.itemView.checkbox_captured.isChecked = record.captured!!

        // Set checkbox listener to save captured state to shared preferences
        holder.checkBox.setOnClickListener {
            val state = holder.checkBox.isChecked
            record.captured = state
            sharedPrefs?.edit()?.putBoolean(record.id, state)?.apply()
        }
    }
}
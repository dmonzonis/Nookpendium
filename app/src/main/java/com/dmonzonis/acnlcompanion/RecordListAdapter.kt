package com.dmonzonis.acnlcompanion

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.record_row.view.*

class RecordListAdapter(private val records: Array<String>) :
    RecyclerView.Adapter<RecordListAdapter.RecordHolder>() {

    class RecordHolder(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.record_row, parent, false)

        return RecordHolder(inflatedView)
    }

    override fun getItemCount() = records.size

    override fun onBindViewHolder(holder: RecordHolder, position: Int) {
        val recordText: String = records[position]
        holder.itemView.text_name.text = recordText
    }

}
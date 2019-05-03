package com.dmonzonis.acnlcompanion

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    private lateinit var viewAdapter: RecordListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputStream: InputStream = assets.open("fish.xml")
        val recordList: List<Record> = RecordXmlParser().parse(inputStream)
        
        viewManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        viewAdapter = RecordListAdapter(recordList)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = viewManager
        recyclerView.adapter = viewAdapter
    }
}

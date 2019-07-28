package com.dmonzonis.acnlcompanion

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_collection.*
import java.io.InputStream

class CollectionActivity : AppCompatActivity() {
    private lateinit var viewAdapter: RecordListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)

        // Get which collection needs to be opened from the value passed by the main menu activity
        val filename = intent.getStringExtra(getString(R.string.filename))

        // Read the corresponding XML with the data and fill the records with it
        val inputStream: InputStream = assets.open(filename)
        val recordList: List<Record> = RecordXmlParser(this).parse(inputStream)

        // Set up the recycler view
        viewManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        viewAdapter = RecordListAdapter(recordList)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = viewManager
        recyclerView.adapter = viewAdapter
    }
}

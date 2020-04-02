package com.dmonzonis.nookpendium

import android.os.Bundle
import android.support.design.widget.TabLayout
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
        // TODO: get the selected tab
        val filename = getString(R.string.fish_file)

        // Read the corresponding XML with the data and fill the records with it
        val inputStream: InputStream = assets.open(filename)
        val parser = RecordXmlParser(this)
        val recordList: List<Record> = parser.parse(inputStream)

        // Set up the recycler view
        viewManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        viewAdapter = RecordListAdapter(recordList)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = viewManager
        recyclerView.adapter = viewAdapter

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                // TODO: Depending on hemisphere (set in settings), get the NH or SH xml file for ACNH
                val filename = when (tab?.text.toString()) {
                    getString(R.string.fish) -> getString(R.string.fish_file)
                    getString(R.string.bugs) -> getString(R.string.bugs_file)
                    else -> getString(R.string.underwater_file)
                }
                val inputStream: InputStream = assets.open(filename)
                val recordList: List<Record> = parser.parse(inputStream)
                viewAdapter = RecordListAdapter(recordList)
                recyclerView.adapter = viewAdapter
            }
        })
    }
}
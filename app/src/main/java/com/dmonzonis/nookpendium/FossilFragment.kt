package com.dmonzonis.nookpendium

import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.InputStream

class FossilFragment(private var selectedGame: Int): Fragment() {

    private lateinit var rootView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecordListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var recordset: Recordset

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_fossils, container, false)
        initRecyclerView()
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewManager = LinearLayoutManager(activity)
        viewAdapter = RecordListAdapter()
    }

    private fun initRecyclerView() {
        // Set up the recycler view
        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = viewManager
        recyclerView.adapter = viewAdapter
        // Load default game assets (Fish) for the last used game (or ACNH if no last used game)
        loadFossils()
    }

    private fun loadFossils() {
        val filePath: String = when (selectedGame) {
            R.string.game_acnh -> getString(R.string.acnh_fossils_path)
            else -> getString(R.string.acnl_fossils_path)
        }
        val inputStream: InputStream = requireContext().assets.open(filePath)
        recordset = RecordXmlParser(requireContext()).parse(inputStream)
        viewAdapter.setRecords(recordset.records)
    }
}
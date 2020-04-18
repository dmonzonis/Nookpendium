package com.dmonzonis.nookpendium

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import java.io.InputStream


class CritterFragment(private var selectedGame: Int): Fragment() {

    private lateinit var rootView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecordListAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var recordset: Recordset

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_critters, container, false)
        tabLayout = rootView.findViewById(R.id.tabLayout)
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
        loadGameAssets(tabLayout.getTabAt(0))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
                loadGameAssets(p0)
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                // TODO: Depending on hemisphere (set in settings), get the NH or SH xml file for ACNH
                loadGameAssets(tab)
                recomputeFilters()
            }
        })
    }

    // Restores the recordset to the original, computes all active filters
    // and the current sorting on it, and then updates the view adapter to use the new
    // recordset
    fun recomputeFilters() {
        recordset.restore()
        val act = activity
        if (act is MainActivity) {
            act.filterManager.applyFilters(recordset)
            act.sortManager.applySort(recordset)
            viewAdapter.setRecords(recordset.records)
        }
    }

    private fun loadGameAssets(tab: TabLayout.Tab?) {
        // ACNH needs 2 tabs while ACNL needs 3
        if (selectedGame == R.string.game_acnh && tabLayout.tabCount > 2) {
            tabLayout.removeTabAt(2)
        } else if (selectedGame == R.string.game_acnl && tabLayout.tabCount < 3) {
            tabLayout.addTab(tabLayout.newTab().setText(R.string.underwater), 2)
        }

        val filePath = if (selectedGame == R.string.game_acnh) {
            when (tab?.text.toString()) {
                getString(R.string.fish) -> getString(R.string.acnh_fish_nh_path)
                else -> getString(R.string.acnh_bugs_nh_path)
            }
        } else {
            when (tab?.text.toString()) {
                getString(R.string.fish) -> getString(R.string.acnl_fish_path)
                getString(R.string.bugs) -> getString(R.string.acnl_bugs_path)
                else -> getString(R.string.acnl_underwater_path)
            }
        }
        val inputStream: InputStream = requireContext().assets.open(filePath)
        recordset = RecordXmlParser(requireContext()).parse(inputStream)
        viewAdapter.setRecords(recordset.records)
        recomputeFilters()
    }
}
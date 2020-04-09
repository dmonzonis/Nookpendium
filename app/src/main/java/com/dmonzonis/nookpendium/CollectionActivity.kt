package com.dmonzonis.nookpendium

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_collection.*
import kotlinx.android.synthetic.main.filter_fab_submenu.*
import java.io.InputStream
import java.util.*

class CollectionActivity : AppCompatActivity(), SortDialogFragment.SortDialogListener {
    private lateinit var viewAdapter: RecordListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    lateinit var drawerToggle: ActionBarDrawerToggle
    private var selectedGame: Int = R.string.game_acnh
    lateinit var recordset: Recordset
    private var isFilterSubmenuOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)

        // Initialize the navigation drawer toggle
        drawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navGamesView.setNavigationItemSelectedListener {
            // FIXME: If another tab other than 0 is selected when changing game, the data
            // for what normally would be tab 0 will be put into whichever tab is active
            when (it.itemId) {
                R.id.miGameAcnh -> changeGame(R.string.game_acnh)
                R.id.miGameAcnl -> changeGame(R.string.game_acnl)
            }
            true
        }

        // Set up the recycler view
        viewManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        viewAdapter = RecordListAdapter()
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = viewManager
        recyclerView.adapter = viewAdapter

        // Load default game assets (Fish, ACNH)
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
            }
        })

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && fabFilters.isShown) {
                    fabFilters.hide()  // Hide on scroll down
                    // Also hide submenu if it was open
                    setFilterButtonsEnabled(false)
                } else if (dy < 0 && !fabFilters.isShown) {
                    fabFilters.show()  // Reappear on scroll up
                }
            }
        })

        fabFilters.setOnClickListener { setFilterButtonsEnabled(!isFilterSubmenuOpen) }
        setFilterButtonsEnabled(false)
        fabFilterThisMonth.setOnClickListener { filterByThisMonth() }
        fabFilterClear.setOnClickListener {
            recordset.restore()
            viewAdapter.setRecords(recordset.records)
        }
        fabSortBy.setOnClickListener {
            val sortByDialog = SortDialogFragment()
            sortByDialog.show(supportFragmentManager, "sort_by")
        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, token: String, descending: Boolean) {
        viewAdapter.setRecords(recordset.applySort(token, descending))
    }


    private fun setFilterButtonsEnabled(enabled: Boolean) {
        isFilterSubmenuOpen = if (enabled) {
            fabFilters.setImageResource(R.drawable.ic_clear_black_24dp)
            fabFilterThisMonth.show()
            fabFilterClear.show()
            fabSortBy.show()
            cardviewFilterThisMonth.visibility = View.VISIBLE
            cardviewFilterClear.visibility = View.VISIBLE
            cardviewSortBy.visibility = View.VISIBLE
            true
        } else {
            cardviewFilterThisMonth.visibility = View.GONE
            cardviewFilterClear.visibility = View.GONE
            cardviewSortBy.visibility = View.GONE
            fabFilterThisMonth.hide()
            fabFilterClear.hide()
            fabSortBy.hide()
            fabFilters.setImageResource(R.drawable.ic_search_black_24dp)
            false
        }
    }

    private fun filterByThisMonth() {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val records = recordset.applyFilterByMonth(currentMonth)
        viewAdapter.setRecords(records)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
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
        val inputStream: InputStream = assets.open(filePath)
        recordset = RecordXmlParser(this).parse(inputStream)
        viewAdapter.setRecords(recordset.records)
    }

    private fun changeGame(game: Int) {
        selectedGame = game
        tabLayout.selectTab(tabLayout.getTabAt(0))
    }
}

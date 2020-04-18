package com.dmonzonis.nookpendium

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_collection.*
import kotlinx.android.synthetic.main.filter_menu.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.InputStream

class CollectionActivity : AppCompatActivity() {
    private lateinit var viewAdapter: RecordListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var selectedGame: Int = R.string.game_acnh
    private lateinit var recordset: Recordset
    private lateinit var sharedPrefs: SharedPreferences
    var filterManager = FilterManager(this)
    var sortManager = SortManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navGamesView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miGameAcnh -> changeGame(R.string.game_acnh)
                R.id.miGameAcnl -> changeGame(R.string.game_acnl)
                R.id.miAbout -> startActivity(Intent(this, AboutActivity::class.java))
            }
            true
        }

        setUpFilterManager()
        setUpSortManager()

        // Set up the recycler view
        viewManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        viewAdapter = RecordListAdapter()
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = viewManager
        recyclerView.adapter = viewAdapter

        // Load default game assets (Fish) for the last used game (or ACNH if no last used game)
        sharedPrefs = getSharedPreferences(getString(R.string.shared_prefs), Context.MODE_PRIVATE)
        selectedGame = sharedPrefs.getInt("selectedGame", R.string.game_acnh)
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

        toolbar.inflateMenu(R.menu.toolbar_menu)
        setSupportActionBar(toolbar)
        supportActionBar!!.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        }
    }

    // Restores the recordset to the original, computes all active filters
    // and the current sorting on it, and then updates the view adapter to use the new
    // recordset
    fun recomputeFilters() {
        recordset.restore()
        // First apply all filters, and then sort the resulting recordset
        filterManager.applyFilters(recordset)
        sortManager.applySort(recordset)
        viewAdapter.setRecords(recordset.records)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        // Set up search action
        var searchMenuItem = menu?.findItem(R.id.miSearch)
        val searchView = searchMenuItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false  // Do nothing, search is done on text change
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterManager.searchQuery = newText ?: ""
                recomputeFilters()
                return false
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // Close all drawers before to avoid having multiple drawers opening on top of each other
        closeAllDrawers()
        when (item?.itemId) {
            android.R.id.home -> drawerLayout.openDrawer(navGamesView)
            R.id.miFilterButton -> drawerLayout.openDrawer(filterMenu)
        }
        return true
    }

    private fun closeAllDrawers() {
        drawerLayout.closeDrawer(navGamesView)
        drawerLayout.closeDrawer(filterMenu)
    }

    private fun addFilterField(id: Int, stringId: Int) {
        // Add filter checkbox view
        var checkboxView = layoutInflater.inflate(R.layout.filter_checkbox, filterMenu, false)
        checkboxView.id = id
        filterContainer.addView(checkboxView)
        var filterCheckbox = filterMenu.findViewById<CheckBox>(id)
        filterCheckbox?.text = getString(stringId)

        // Notify the controller of the new filter
        if (filterCheckbox != null)
            filterManager.addFilterCheckbox(filterCheckbox)
    }

    private fun addSortField(id: Int, stringId: Int) {
        var sortItemLayout = layoutInflater.inflate(R.layout.sort_item, filterMenu, false)
        sortItemLayout.id = id
        sortContainer.addView(sortItemLayout)
        var sortItem = filterMenu.findViewById<LinearLayout>(id)
        var textSortBy = sortItem?.findViewById<TextView>(R.id.textSortBy)
        textSortBy?.text = getString(stringId)

        // Notify the controller of the new sort item
        if (sortItem != null)
            sortManager.addSortField(sortItem)
    }

    private fun setUpFilterManager() {
        addFilterField(R.id.filterNotCaught, R.string.not_caught)
        addFilterField(R.id.filterNotDonated, R.string.not_donated)
        addFilterField(R.id.filterThisMonth, R.string.filter_this_month)
    }

    private fun setUpSortManager() {
        addSortField(R.id.sortById, R.string.id)
        addSortField(R.id.sortByName, R.string.name)
        addSortField(R.id.sortByPrice, R.string.price)
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
        recomputeFilters()
    }

    private fun changeGame(game: Int) {
        selectedGame = game
        tabLayout.selectTab(tabLayout.getTabAt(0))
        // Store selected game in persistent memory so this game is opened
        // the next time the app is launched
        // TODO: Also remember hemisphere, once we are allowed to change it
        sharedPrefs.edit().putInt("selectedGame", game).apply()
    }
}

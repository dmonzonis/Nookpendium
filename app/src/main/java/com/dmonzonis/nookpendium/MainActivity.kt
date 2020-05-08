package com.dmonzonis.nookpendium

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.filter_menu.*
import kotlinx.android.synthetic.main.toolbar.*


class MainActivity : AppCompatActivity() {
    private lateinit var sharedPrefs: SharedPreferences
    lateinit var filterManager: FilterManager
    lateinit var sortManager: SortManager
    private lateinit var currentFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPrefs = getSharedPreferences(getString(R.string.shared_prefs), Context.MODE_PRIVATE)!!

        navGamesView.setNavigationItemSelectedListener(navListener())

        currentFragment = CritterFragment(sharedPrefs.getInt("selectedGame", R.string.game_acnh))
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, currentFragment)
            .commit()

        filterManager = FilterManager(currentFragment as CritterFragment)
        sortManager = SortManager(currentFragment as CritterFragment)
        setUpFilterManager()
        setUpSortManager()

        toolbar.inflateMenu(R.menu.toolbar_menu)
        setSupportActionBar(toolbar)
        supportActionBar!!.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        }
    }

    private fun navListener() : NavigationView.OnNavigationItemSelectedListener {
        return NavigationView.OnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miAcnhCritters -> {
                    currentFragment = CritterFragment(R.string.game_acnh)
                    sharedPrefs.edit().putInt("selectedGame", R.string.game_acnh).apply()
                }
                R.id.miAcnhFossils -> {
                    currentFragment = FossilFragment(R.string.game_acnh)
                    sharedPrefs.edit().putInt("selectedGame", R.string.game_acnh).apply()
                }
                R.id.miAcnlCritters -> {
                    currentFragment = CritterFragment(R.string.game_acnl)
                    sharedPrefs.edit().putInt("selectedGame", R.string.game_acnl).apply()
                }
                R.id.miAcnlFossils -> {
                    currentFragment = FossilFragment(R.string.game_acnl)
                    sharedPrefs.edit().putInt("selectedGame", R.string.game_acnl).apply()
                }
                R.id.miAbout -> {
                    currentFragment = AboutFragment()
                }
            }
            // Necessary for calling onCreateOptionsMenu again in case we change
            // to a fragment that does not need filtering / search
            invalidateOptionsMenu()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, currentFragment).commit()
            if (currentFragment is CritterFragment) {
                filterManager.fragment = currentFragment as CritterFragment
                sortManager.fragment = currentFragment as CritterFragment
            }
            drawerLayout.closeDrawer(navGamesView)
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (currentFragment is CritterFragment) {
            menuInflater.inflate(R.menu.toolbar_menu, menu)
            // Set up search action
            val searchMenuItem = menu?.findItem(R.id.miSearch)
            val searchView = searchMenuItem?.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false  // Do nothing, search is done on text change
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    filterManager.searchQuery = newText ?: ""
                    filterManager.fragment.recomputeFilters()
                    return false
                }

            })
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
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
}

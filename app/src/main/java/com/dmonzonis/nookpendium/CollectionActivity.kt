package com.dmonzonis.nookpendium

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_collection.*
import java.io.InputStream

class CollectionActivity : AppCompatActivity() {
    private lateinit var viewAdapter: RecordListAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    lateinit var drawerToggle: ActionBarDrawerToggle
    private var selectedGame: Int = R.string.game_acnh

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
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = viewManager

        // Load default game assets (Fish, ACNH)
        loadGameAssets(tabLayout.getTabAt(0))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                // TODO: Depending on hemisphere (set in settings), get the NH or SH xml file for ACNH
                loadGameAssets(tab)
            }
        })
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
        val recordList = RecordXmlParser(this).parse(inputStream)
        viewAdapter = RecordListAdapter(recordList)
        recyclerView.adapter = viewAdapter
    }

    private fun changeGame(game: Int) {
        selectedGame = game
        tabLayout.selectTab(tabLayout.getTabAt(0))
    }
}

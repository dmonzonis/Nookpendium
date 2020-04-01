package com.dmonzonis.nookpendium

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dmonzonis.nookpendium.CollectionActivity
import kotlinx.android.synthetic.main.activity_collection_select.*

class CollectionSelectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_select)

        button_fish.setOnClickListener {
            openCollectionInstance(getString(R.string.fish_file))
        }

        button_insects.setOnClickListener {
            openCollectionInstance(getString(R.string.insects_file))
        }

        button_underwater.setOnClickListener {
            openCollectionInstance(getString(R.string.underwater_file))
        }
    }

    private fun openCollectionInstance(filename: String) {
        val intent = Intent(this, CollectionActivity::class.java)

        // Pass the filename string to the new activity
        intent.putExtra(getString(R.string.filename), filename)

        startActivity(intent)
    }
}
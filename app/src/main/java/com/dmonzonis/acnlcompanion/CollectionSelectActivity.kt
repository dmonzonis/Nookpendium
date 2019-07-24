package com.dmonzonis.acnlcompanion

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_collection_select.*

class CollectionSelectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_select)

        button_fish.setOnClickListener {
            openCollectionInstance("fish.xml")
        }

        button_insects.setOnClickListener {
            openCollectionInstance("insects.xml")
        }

        button_underwater.setOnClickListener {
            openCollectionInstance("underwater.xml")
        }
    }

    private fun openCollectionInstance(filename: String) {
        val intent = Intent(this, CollectionActivity::class.java)

        // Pass the filename string to the new activity
        intent.putExtra("filename", filename)

        startActivity(intent)
    }
}
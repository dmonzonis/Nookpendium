package com.dmonzonis.nookpendium

import android.widget.ImageView
import android.widget.LinearLayout

class SortManager(internal var fragment: CritterFragment) {
    private var currentSort: Int? = null
    private var descending: Boolean = false
    private var imageViewMap = mutableMapOf<Int, ImageView>()

    fun addSortField(sortItem: LinearLayout) {
        val id = sortItem.id
        var imgSortOrder = sortItem.findViewById<ImageView>(R.id.imgSortOrder)
        if (imgSortOrder != null)
            imageViewMap[id] = imgSortOrder

        // Initially, sort by the first sorting item introduced
        if (currentSort == null) {
            currentSort = id
            imgSortOrder?.setImageResource(sortOrderImageResource())
        }

        sortItem.setOnClickListener { _ ->
            if (id == currentSort) {
                // Toggle sorting order
                descending = !descending
                setImageOnId(id, sortOrderImageResource())
            } else {
                // Set sorting by the selected id in the same order we had
                setImageOnId(currentSort!!, 0)
                currentSort = id
                setImageOnId(id, sortOrderImageResource())
            }

            fragment.recomputeFilters()
        }
    }

    fun applySort(recordset: Recordset) {
        if (currentSort != null)
            recordset.applySort(currentSort!!, descending)
    }

    private fun sortOrderImageResource(): Int {
        return when (descending) {
            true -> R.drawable.ic_arrow_downward_24px
            false -> R.drawable.ic_arrow_upward_24px
        }
    }

    // Sets the image of the given sort item to the specified resource
    private fun setImageOnId(id: Int, resource: Int) {
        imageViewMap[id]?.setImageResource(resource)
    }
}
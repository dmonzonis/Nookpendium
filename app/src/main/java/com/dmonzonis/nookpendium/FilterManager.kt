package com.dmonzonis.nookpendium

import android.widget.CheckBox
import java.util.*

class FilterManager(private val activity: CollectionActivity) {
    // Array of currently active filters, which correspond to the IDs of the associated views
    private var activeFilters = mutableSetOf<Int>()
    var searchQuery: String = ""

    fun addFilterCheckbox(filterCheckbox: CheckBox) {
        val id = filterCheckbox.id
        filterCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                activeFilters.add(id)
            } else {
                activeFilters.remove(id)
            }

            activity.recomputeFilters()
        }
    }

    fun applyFilters(recordset: Recordset) {
        // Apply search filter separately
        // TODO: Search by price if the searchQuery is a number
        recordset.applyFilter {
            val nameString = activity.getString(it.name)
            nameString.contains(searchQuery, ignoreCase = true)
        }

        // Apply the currently active filters to the current state of the recordset
        for (filter in activeFilters) {
            // Associate each filter ID with the corresponding filter lambda
            val filterFun = when (filter) {
                R.id.filterNotCaught -> { r: Record -> r.captured?.not() ?: true }
                R.id.filterNotDonated -> { r: Record -> r.donated?.not() ?: true }
                R.id.filterThisMonth -> { r: Record ->
                    availableOnMonth(r, Calendar.getInstance().get(Calendar.MONTH))
                }
                else -> { _: Record -> true }  // Filter not implemented, do nothing
            }
            recordset.applyFilter(filterFun)
        }
    }

    private fun availableOnMonth(record: Record, month: Int): Boolean {
        return month < record.availability.size && record.availability[month] == 1
    }
}
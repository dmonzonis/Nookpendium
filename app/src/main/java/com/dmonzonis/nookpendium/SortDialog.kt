package com.dmonzonis.nookpendium

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.sort_by_dialog.view.*

class SortDialogFragment : DialogFragment() {
    // Instance of the interface that will deliver action events
    private lateinit var listener: SortDialogListener

    // The activity calling this dialog must implement this interface to be able to
    // receive event callbacks
    interface SortDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, token: String, descending: Boolean)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity!!.let {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.sort_by_dialog, null)
            val builder = AlertDialog.Builder(it)
                .setView(dialogView)
                .setPositiveButton(R.string.sort, DialogInterface.OnClickListener { dialog, id ->
                    val token = when (dialogView.rgSortBy.checkedRadioButtonId) {
                        dialogView.rbSortByName.id -> "name"
                        else -> "price"
                    }
                    val descending =
                        dialogView.rgSortOrder.checkedRadioButtonId == dialogView.rbOrderDescending.id
                    listener.onDialogPositiveClick(this, token, descending)
                })
            builder.create()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as SortDialogListener
        } catch (e: ClassCastException) {
            // The activity opening this dialog must implement the listener interface
            throw ClassCastException((context.toString() + " must implement SortDialogListener"))
        }
    }
}

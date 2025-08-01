package ph.edu.dlsu.ccs.mobicom.remedication

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import ph.edu.dlsu.ccs.mobicom.remedication.NewMedicineActivity
import ph.edu.dlsu.ccs.mobicom.remedication.NewMedicineActivity.Companion.NEW_DOSAGE_KEY
import ph.edu.dlsu.ccs.mobicom.remedication.NewMedicineActivity.Companion.NEW_END_KEY
import ph.edu.dlsu.ccs.mobicom.remedication.NewMedicineActivity.Companion.NEW_FREQUENCY_KEY
import ph.edu.dlsu.ccs.mobicom.remedication.NewMedicineActivity.Companion.NEW_ID_KEY
import ph.edu.dlsu.ccs.mobicom.remedication.NewMedicineActivity.Companion.NEW_IMAGE_KEY
import ph.edu.dlsu.ccs.mobicom.remedication.NewMedicineActivity.Companion.NEW_NAME_KEY
import ph.edu.dlsu.ccs.mobicom.remedication.NewMedicineActivity.Companion.NEW_REMAINING_KEY
import ph.edu.dlsu.ccs.mobicom.remedication.NewMedicineActivity.Companion.NEW_START_KEY
import ph.edu.dlsu.ccs.mobicom.remedication.NewMedicineActivity.Companion.NEW_TIMEOFDAY_KEY
import ph.edu.dlsu.ccs.mobicom.remedication.NewMedicineActivity.Companion.NEW_UNIT_KEY
import ph.edu.dlsu.ccs.mobicom.remedication.databinding.ActivityNewMedicineBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.Executors
import kotlin.math.log

class ChecklistViewHolder(itemView: View): ViewHolder(itemView) {
    // In our item layout, we need two references -- an ImageView and a TextView. Please note that
    // the itemView is the RecyclerView -- which has context that we can use to find View instances.
    private val executorService = Executors.newSingleThreadExecutor()
    private lateinit var myDbHelper: LogDbHelper

    private val iv: ImageView = itemView.findViewById(R.id.medicineImageIv)
    private val mtv: TextView = itemView.findViewById(R.id.medicineNameTv)
    private val dtv: TextView = itemView.findViewById(R.id.dosageTv)
    private val cb: CheckBox = itemView.findViewById(R.id.checklistCb)
    private val sharedPreferences: SharedPreferences = itemView.context.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)

    // This is our own method that accepts a Character object and sets our views' info accordingly.
    fun bindData(checklist: Checklist, position: Int, adapter: ChecklistAdapter) {
        myDbHelper = LogDbHelper.getInstance(itemView.context.applicationContext)!!

        Glide.with(iv).load(checklist.image).centerCrop().into(iv)

        mtv.text = checklist.medicineName
        dtv.text = checklist.dosage

        val savedIsChecked = sharedPreferences.getBoolean("checklist_${checklist.medicineName}_${checklist.timeOfDay}_checked", false)
        checklist.isChecked = savedIsChecked
        cb.isChecked = checklist.isChecked

        val bgColor = when {
            checklist.isChecked && !checklist.isOverdue-> 0xFFB5F2B5.toInt()
            checklist.isChecked && checklist.isOverdue-> 0xFFFDFD96.toInt()
            checklist.isOverdue -> 0xFFFDBEC7.toInt()
            else            -> 0xFFFFFFFF.toInt()
        }
        val rounded = ContextCompat
            .getDrawable(itemView.context, R.drawable.rounded_corners)!!
            .mutate() as GradientDrawable

        rounded.setColor(bgColor)
        itemView.background = rounded

        val alpha = if (checklist.isChecked) 0.5f else 1.0f
        iv.alpha  = alpha
        mtv.alpha = alpha
        dtv.alpha = alpha
        cb.alpha  = alpha

        cb.setOnClickListener {
            val newCheckedState = !checklist.isChecked

            if (newCheckedState) {
                handleCheckboxChange(checklist, position, adapter, newCheckedState)
            } else {
                showUncheckConfirmationDialog(checklist, position, adapter)
            }
        }
    }

    fun saveNewLog(itemId: Long, logId: Long, isCreated: Boolean, context: Context) {
        val sharedPref = context.getSharedPreferences("LogPreferences", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putLong("logId_$itemId", logId)
        editor.putBoolean("isLogCreated_$itemId", isCreated)
        editor.apply()
    }

    fun deleteLog(itemId: Long, context: Context) {
        val sharedPref = context.getSharedPreferences("LogPreferences", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.remove("logId_$itemId")
        editor.remove("isLogCreated_$itemId")
        editor.apply()
    }

    fun getIsLogCreated(itemId: Long, context: Context): Boolean {
        val sharedPref = context.getSharedPreferences("LogPreferences", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("isLogCreated_$itemId", false) == true   // Default is false if not found
    }

    fun getLogId(itemId: Long, context: Context): Long {
        val sharedPref = context.getSharedPreferences("LogPreferences", Context.MODE_PRIVATE)
        return sharedPref.getLong("logId_$itemId", -1L)
    }

    private fun handleCheckboxChange(checklist: Checklist, position: Int, adapter: ChecklistAdapter, isChecked: Boolean) {
        checklist.isChecked = isChecked

        val editor = sharedPreferences.edit()
        editor.putBoolean("checklist_${checklist.medicineName}_${checklist.timeOfDay}_checked", checklist.isChecked)
        editor.apply()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())

        val isLogCreated = getIsLogCreated(checklist.id, itemView.context)

        if (checklist.isChecked && !isLogCreated && !checklist.isOverdue) {
            executorService.execute {
                val currentDate = Date()
                val formattedDate = dateFormat.format(currentDate)
                val formattedTime = timeFormat.format(currentDate)
                val log = Log(
                    formattedDate,
                    formattedTime,
                    mtv.text.toString(),
                    dtv.text.toString(),
                    false
                )
                val newId = myDbHelper.insertLog(log)
                saveNewLog(checklist.id, newId, true, itemView.context)
                android.util.Log.d("ChecklistViewHolder", "new Log Taken: $newId")

                // Update UI on main thread
                itemView.post {
                    adapter.notifyItemChanged(position)
                }
            }
        } else if (!checklist.isChecked && isLogCreated && !checklist.isOverdue) {
            executorService.execute {
                val logId = getLogId(checklist.id, itemView.context)
                if (logId != -1L) {
                    android.util.Log.d("ChecklistViewHolder", "Deleting Log with ID: $logId")
                    myDbHelper.deleteLog(logId)
                    deleteLog(checklist.id, itemView.context)
                    android.util.Log.d("ChecklistViewHolder", "Log Deleted: $logId")

                    // Update UI on main thread
                    itemView.post {
                        adapter.notifyItemChanged(position)
                    }
                }
            }
        } else {
            adapter.notifyItemChanged(position)
        }
    }

    private fun showUncheckConfirmationDialog(checklist: Checklist, position: Int, adapter: ChecklistAdapter) {
        AlertDialog.Builder(itemView.context)
            .setTitle("Confirm Uncheck")
            .setMessage("Are you sure you want to uncheck this item?")
            .setPositiveButton("Yes") { dialog, _ ->
                handleCheckboxChange(checklist, position, adapter, false)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                // Keep the checkbox checked
                cb.isChecked = true
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
}
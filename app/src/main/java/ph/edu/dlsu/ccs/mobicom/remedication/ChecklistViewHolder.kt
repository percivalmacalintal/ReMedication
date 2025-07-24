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
            checklist.isChecked -> 0xFFB5F2B5.toInt()
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

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()) // Date format (year-month-day)
        val timeFormat = SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()) // Time format (hours:minutes:seconds)

        cb.setOnClickListener {
            checklist.isChecked = !checklist.isChecked

            val editor = sharedPreferences.edit()
            editor.putBoolean("checklist_${checklist.medicineName}_${checklist.timeOfDay}_checked", checklist.isChecked)
            editor.apply()

            if (checklist.isChecked && !checklist.isLogCreated && !checklist.isOverdue){   //idk if cb is disabled when overdue
                executorService.execute {   //  add new log when checked
                    val currentDate = Date()
                    val formattedDate = dateFormat.format(currentDate)
                    val formattedTime = timeFormat.format(currentDate)
                    val log = Log(
                        formattedDate,   //now
                        formattedTime,   //this time
                        mtv.text.toString(),
                        dtv.text.toString(),
                        false
                    )
                    val newId = myDbHelper.insertLog(log)
                    checklist.isLogCreated = true
                    checklist.logID = newId
                    android.util.Log.d("ChecklistViewHolder", "new Log Taken: $newId")
                }
            } else if (!checklist.isChecked && checklist.isLogCreated && !checklist.isOverdue){
                executorService.execute {   //  delete log if unchecked
                    if (checklist.logID != -1L) {
                        android.util.Log.d("ChecklistViewHolder", "Deleting Log with ID: ${checklist.logID}")
                        myDbHelper.deleteLog(checklist.logID)
                        checklist.isLogCreated = false
                        checklist.logID = -1L
                        android.util.Log.d("ChecklistViewHolder", "Log Deleted: ${checklist.logID}")
                    }
                }
            }
            adapter.notifyItemChanged(position)
        }
    }
}
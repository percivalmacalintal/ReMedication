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
import kotlin.math.log

class ChecklistViewHolder(itemView: View): ViewHolder(itemView) {
    private val executorService = Executors.newSingleThreadExecutor()
    private lateinit var myDbHelper: LogDbHelper

    private val iv: ImageView = itemView.findViewById(R.id.medicineImageIv)
    private val mtv: TextView = itemView.findViewById(R.id.medicineNameTv)
    private val dtv: TextView = itemView.findViewById(R.id.dosageTv)
    private val cb: CheckBox = itemView.findViewById(R.id.checklistCb)
    private val sharedPreferences: SharedPreferences = itemView.context.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)

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

            val isLogCreated = getIsLogCreated(checklist.id, itemView.context)
            if (checklist.isChecked){
                val logStatus = if (checklist.isOverdue) LogStatus.LATE else LogStatus.ONTIME
                executorService.execute {
                    val currentDate = Date()
                    val formattedDate = dateFormat.format(currentDate)
                    val formattedTime = timeFormat.format(currentDate)

                    if(isLogCreated) {  // late
                        val logId = getLogId(checklist.id, itemView.context)
                        myDbHelper.updateLog(logId, formattedTime, logStatus)
                        android.util.Log.d("ChecklistViewHolder", "Updated to Late Log: $logId")
                    } else {    // on time
                        val log = Log(
                            formattedDate,   //now
                            formattedTime,   //this time
                            mtv.text.toString(),
                            dtv.text.toString(),
                            logStatus
                        )
                        val newId = myDbHelper.insertLog(log)
                        insertLog(checklist.id, newId, true, itemView.context)
                        android.util.Log.d("ChecklistViewHolder", "Inserted Log: $newId")
                    }
                }
            } else {
                if (isLogCreated) {
                    val currentDate = Date()
                    val formattedTime = timeFormat.format(currentDate)
                    val logId = getLogId(checklist.id, itemView.context)
                    if (checklist.isOverdue){
                        val logId = getLogId(checklist.id, itemView.context)
                        myDbHelper.updateLog(logId, formattedTime, LogStatus.MISSED)
                        android.util.Log.d("ChecklistViewHolder", "Updated to Missed Log: $logId")
                    } else {
                        executorService.execute {   //  delete
                            if (logId != -1L) {
                                myDbHelper.deleteLog(logId)
                                deleteLog(checklist.id, itemView.context)
                                android.util.Log.d("ChecklistViewHolder", "Deleted Log: $logId")
                            }
                        }
                    }
                } else {
                    android.util.Log.d("ChecklistViewHolder", "Unchecked item: ${checklist.id}, with no log ")
                }
            }
            adapter.notifyItemChanged(position)
        }
    }

    fun insertLog(itemId: Long, logId: Long, isCreated: Boolean, context: Context) {
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
        return sharedPref.getBoolean("isLogCreated_$itemId", false) == true
    }

    fun getLogId(itemId: Long, context: Context): Long {
        val sharedPref = context.getSharedPreferences("LogPreferences", Context.MODE_PRIVATE)
        return sharedPref.getLong("logId_$itemId", -1L)
    }
}
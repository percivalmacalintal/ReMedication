package ph.edu.dlsu.ccs.mobicom.remedication

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import ph.edu.dlsu.ccs.mobicom.remedication.databinding.ActivityNewMedicineBinding
import ph.edu.dlsu.ccs.mobicom.remedication.databinding.DialogTimeofdaySelectionBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

class NewMedicineActivity : ComponentActivity(){
    companion object {
        const val NEW_IMAGE_KEY = "NEW_IMAGE_KEY"
        const val NEW_NAME_KEY = "NEW_NAME_KEY"
        const val NEW_DOSAGE_KEY = "NEW_DOSAGE_KEY"
        const val NEW_UNIT_KEY = "NEW_UNIT_KEY"
        const val NEW_FREQUENCY_KEY = "NEW_FREQUENCY_KEY"
        const val NEW_TIMEOFDAY_KEY = "NEW_TIMEOFDAY_KEY"
        const val NEW_REMAINING_KEY = "NEW_REMAINING_KEY"
        const val NEW_START_KEY = "NEW_START_KEY"
        const val NEW_END_KEY = "NEW_END_KEY"
    }

    private lateinit var viewBinding: ActivityNewMedicineBinding

    private var selectedTimeOfDay = mutableListOf<Int>()
    private var confirmedTimeOfDay = mutableListOf<Int>()
    private var confirmedFrequency = ""

    private val units = arrayOf("mg", "ml")
    private val frequencies = arrayOf("Select frequency...", "Once a day", "Twice a day", "Thrice a day")
    private val resId = R.drawable.medicine

    private var previousFreqPosition = 0
    private var restoringSpinner = false

    private var imageAdded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityNewMedicineBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.startvalEt.setOnClickListener {
            showDatePickerDialog(viewBinding.startvalEt)
        }

        viewBinding.endvalEt.setOnClickListener {
            showDatePickerDialog(viewBinding.endvalEt)
        }

        val unitAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, units)
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewBinding.unitvalSp.adapter = unitAdapter

        val freqAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, frequencies)
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewBinding.freqvalSp.adapter = freqAdapter

        viewBinding.closeBtn.setOnClickListener {
            finish()
        }

        viewBinding.addMedBtn.setOnClickListener {
            val image = resId
            val name = viewBinding.namevalEt.text.toString().trim()
            val dosage = viewBinding.dosvalEt.text.toString().trim()
            val remaining = viewBinding.remvalEt.text.toString().trim()
            val startDate = viewBinding.startvalEt.text.toString().trim()
            val endDate = viewBinding.endvalEt.text.toString().trim()

            val unit = viewBinding.unitvalSp.selectedItem.toString()
            val frequency = viewBinding.freqvalSp.selectedItem.toString()

            if (name.isEmpty() || dosage.isEmpty() || remaining.isEmpty() ||
                startDate.isEmpty() || endDate.isEmpty()
                || frequency == "Select frequency..." || !imageAdded) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (confirmedTimeOfDay.isEmpty()) {
                Toast.makeText(this, "Please choose time(s) of day", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val returnIntent = Intent()
            returnIntent.putExtra(NEW_IMAGE_KEY, image)
            returnIntent.putExtra(NEW_NAME_KEY, name)
            returnIntent.putExtra(NEW_DOSAGE_KEY, dosage.toInt())
            returnIntent.putExtra(NEW_UNIT_KEY, unit)
            returnIntent.putExtra(NEW_FREQUENCY_KEY, frequency)
            returnIntent.putIntegerArrayListExtra(NEW_TIMEOFDAY_KEY, ArrayList(confirmedTimeOfDay))
            returnIntent.putExtra(NEW_REMAINING_KEY, remaining.toInt())
            returnIntent.putExtra(NEW_START_KEY, startDate)
            returnIntent.putExtra(NEW_END_KEY, endDate)
            setResult(RESULT_OK, returnIntent)
            finish()
        }

        viewBinding.freqvalSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (restoringSpinner) { restoringSpinner = false; return }

                val newFrequency = parent.getItemAtPosition(position).toString()
                if (position == 0) {
                    confirmedFrequency = ""
                    confirmedTimeOfDay.clear()
                    viewBinding.freqvalTv.visibility = View.GONE
                    return
                }

                selectedTimeOfDay.clear()
                showTimeOfDaySelectionDialog(
                    newFrequency,
                    onConfirm = {
                        confirmedFrequency = newFrequency
                        confirmedTimeOfDay = selectedTimeOfDay.toMutableList()
                        viewBinding.freqvalTv.text = formatTimeOfDayOnly(confirmedTimeOfDay)
                        viewBinding.freqvalTv.visibility = View.VISIBLE
                        previousFreqPosition = position
                    },
                    onCancel  = {
                        restoringSpinner = true
                        viewBinding.freqvalSp.setSelection(previousFreqPosition)
                        selectedTimeOfDay = confirmedTimeOfDay.toMutableList()
                    }
                )
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        viewBinding.freqvalSp.setSelection(0)

        viewBinding.medicineIv.setImageResource(android.R.color.transparent)
        viewBinding.addImgBtn.setOnClickListener {
            viewBinding.medicineIv.setImageResource(resId)
            viewBinding.addImgBtn.visibility = View.GONE
            imageAdded = true
        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(
                    GregorianCalendar(selectedYear, selectedMonth, selectedDay).time
                )
                editText.setText(formattedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun showTimeOfDaySelectionDialog(
        frequencyLabel: String,
        onConfirm: () -> Unit,
        onCancel: () -> Unit
    ) {
        val dialogBinding = DialogTimeofdaySelectionBinding.inflate(layoutInflater)

        val afterMidnightCb = dialogBinding.afterMidnightCb
        val morningCb = dialogBinding.morningCb
        val afternoonCb = dialogBinding.afternoonCb
        val nightCb = dialogBinding.nightCb
        val freqTv = dialogBinding.freqTv

        freqTv.text = getString(R.string.freqTvText, frequencyLabel)

        val maxSelection = when (frequencyLabel) {
            "Once a day" -> 1
            "Twice a day" -> 2
            "Thrice a day" -> 3
            else -> Int.MAX_VALUE
        }

        val limitSelections = { checkedCount: Int ->
            afterMidnightCb.isEnabled = checkedCount < maxSelection || afterMidnightCb.isChecked
            morningCb.isEnabled = checkedCount < maxSelection || morningCb.isChecked
            afternoonCb.isEnabled = checkedCount < maxSelection || afternoonCb.isChecked
            nightCb.isEnabled = checkedCount < maxSelection || nightCb.isChecked
        }

        val checkBoxListener = CompoundButton.OnCheckedChangeListener { _, _ ->
            val checkedCount = listOf(afterMidnightCb, morningCb, afternoonCb, nightCb).count { it.isChecked }
            limitSelections(checkedCount)
        }

        afterMidnightCb.setOnCheckedChangeListener(checkBoxListener)
        morningCb.setOnCheckedChangeListener(checkBoxListener)
        afternoonCb.setOnCheckedChangeListener(checkBoxListener)
        nightCb.setOnCheckedChangeListener(checkBoxListener)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Time of Day")
        builder.setView(dialogBinding.root)

        builder.setPositiveButton("OK") { _, _ ->
            selectedTimeOfDay.clear()
            if (afterMidnightCb.isChecked) selectedTimeOfDay.add(0)
            if (morningCb.isChecked) selectedTimeOfDay.add(1)
            if (afternoonCb.isChecked) selectedTimeOfDay.add(2)
            if (nightCb.isChecked) selectedTimeOfDay.add(3)
            confirmedFrequency = frequencyLabel
            confirmedTimeOfDay = selectedTimeOfDay.toMutableList()
            viewBinding.freqvalTv.text = formatTimeOfDayOnly(selectedTimeOfDay)
            viewBinding.freqvalTv.visibility = View.VISIBLE
            onConfirm()
        }

        builder.setNegativeButton("Cancel") { _, _ ->
            onCancel()
        }

        builder.show()
    }

    private fun formatTimeOfDayOnly(timeOfDay: List<Int>): String {
        val timeLabels = timeOfDay.mapNotNull {
            when (it) {
                0 -> "Early Morning"
                1 -> "Morning"
                2 -> "Afternoon"
                3 -> "Night"
                else -> null
            }
        }
        return timeLabels.joinToString(" & ")
    }
}
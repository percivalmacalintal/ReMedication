package ph.edu.dlsu.ccs.mobicom.remedication

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.widget.addTextChangedListener
import ph.edu.dlsu.ccs.mobicom.remedication.databinding.ActivityInfoBinding
import ph.edu.dlsu.ccs.mobicom.remedication.databinding.DialogTimeofdaySelectionBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale


class InfoActivity : ComponentActivity() {
    companion object {
        const val NAME_KEY = "NAME_KEY"
        const val DOSAGE_KEY = "DOSAGE_KEY"
        const val UNIT_KEY = "UNIT_KEY"
        const val FREQUENCY_KEY = "FREQUENCY_KEY"
        const val TIMEOFDAY_KEY = "TIMEOFDAY_KEY"
        const val REMAINING_KEY = "REMAINING_KEY"
        const val START_KEY = "START_KEY"
        const val END_KEY = "END_KEY"
        const val POSITION_KEY = "POSITION_KEY"
    }

    private var isEditing = false
    private var initialName: String = ""
    private var initialDosage: String = ""
    private var initialUnit: String = ""
    private var initialFrequency: String = ""
    private var initialTimeOfDay: List<Int> = listOf()
    private var initialRemaining: String = ""
    private var initialStartDate: String = ""
    private var initialEndDate: String = ""
    private var initialPosition: Int = -1

    private var defaultEditTextBackground: Drawable? = null
    private var selectedTimeOfDay = mutableListOf<Int>()

    private var isSpinnerInitialized = false
    private var previousFreqPosition = 0
    private var restoringSpinner = false
    private var confirmedTimeOfDay = mutableListOf<Int>()
    private var confirmedFrequency = ""

    private val units = arrayOf("mg", "ml")
    private val frequencies = arrayOf("Once a day", "Twice a day", "Three times a day")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBinding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initialName = this.intent.getStringExtra(NAME_KEY) ?: ""
        initialDosage = this.intent.getIntExtra(DOSAGE_KEY, 0).toString()
        initialUnit = this.intent.getStringExtra(UNIT_KEY) ?: ""
        initialFrequency = this.intent.getStringExtra(FREQUENCY_KEY) ?: ""
        initialTimeOfDay = this.intent.getIntegerArrayListExtra(TIMEOFDAY_KEY) ?: listOf()
        initialRemaining = this.intent.getIntExtra(REMAINING_KEY, 0).toString()
        initialStartDate = this.intent.getStringExtra(START_KEY) ?: ""
        initialEndDate = this.intent.getStringExtra(END_KEY) ?: ""
        initialPosition = this.intent.getIntExtra(POSITION_KEY, -1)

        confirmedFrequency  = initialFrequency
        confirmedTimeOfDay = initialTimeOfDay.toMutableList()

        viewBinding.namevalEt.setText(initialName)
        viewBinding.dosvalEt.setText(initialDosage)
        viewBinding.remvalEt.setText(initialRemaining)
        viewBinding.startvalEt.setText(initialStartDate)
        viewBinding.endvalEt.setText(initialEndDate)

        defaultEditTextBackground = viewBinding.namevalEt.background

        addTextChangedListeners(viewBinding)

        viewBinding.startvalEt.setOnClickListener {
            if (isEditing) {
                showDatePickerDialog(viewBinding.startvalEt, initialStartDate)
            }
        }

        viewBinding.endvalEt.setOnClickListener {
            if (isEditing) {
                showDatePickerDialog(viewBinding.endvalEt, initialEndDate)
            }
        }

        disableEditing(viewBinding)

        viewBinding.edtBtn.setOnClickListener {
            isEditing = !isEditing

            if (isEditing) {
                viewBinding.namevalEt.isFocusable = true
                viewBinding.namevalEt.isFocusableInTouchMode = true
                viewBinding.dosvalEt.isFocusable = true
                viewBinding.dosvalEt.isFocusableInTouchMode = true
                viewBinding.remvalEt.isFocusable = true
                viewBinding.remvalEt.isFocusableInTouchMode = true

                viewBinding.freqvalSp.visibility = View.VISIBLE
                viewBinding.unitvalSp.visibility = View.VISIBLE
                viewBinding.dosvalEt.visibility = View.VISIBLE

                viewBinding.freqvalTv.visibility = View.GONE
                viewBinding.dosvalTv.visibility = View.GONE

                viewBinding.namevalEt.background = defaultEditTextBackground
                viewBinding.dosvalEt.background = defaultEditTextBackground
                viewBinding.remvalEt.background = defaultEditTextBackground
                viewBinding.startvalEt.background = defaultEditTextBackground
                viewBinding.endvalEt.background = defaultEditTextBackground

                enableSaveButtonIfChanges(viewBinding)

                viewBinding.saveBtn.visibility = View.VISIBLE
                viewBinding.delBtn.visibility = View.VISIBLE
            } else {
                disableEditing(viewBinding)
            }
        }

        viewBinding.saveBtn.setOnClickListener {
            val updatedName = viewBinding.namevalEt.text.toString().trim()
            val updatedDosage = viewBinding.dosvalEt.text.toString().trim()
            val updatedRemaining = viewBinding.remvalEt.text.toString().trim()
            val updatedStartDate = viewBinding.startvalEt.text.toString().trim()
            val updatedEndDate = viewBinding.endvalEt.text.toString().trim()

            if (updatedName.isEmpty() || updatedDosage.isEmpty() || updatedRemaining.isEmpty() ||
                updatedStartDate.isEmpty() || updatedEndDate.isEmpty()) {

                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedUnit = viewBinding.unitvalSp.selectedItem.toString()
            val updatedFrequency = viewBinding.freqvalSp.selectedItem.toString()

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirm Changes")
            builder.setMessage("Are you sure you want to save these changes?")

            builder.setPositiveButton("Yes") { _, _ ->
                val returnIntent = Intent()
                returnIntent.putExtra(NAME_KEY, updatedName)
                returnIntent.putExtra(DOSAGE_KEY, updatedDosage.toInt())
                returnIntent.putExtra(UNIT_KEY, updatedUnit)
                returnIntent.putExtra(FREQUENCY_KEY, updatedFrequency)
                returnIntent.putIntegerArrayListExtra(TIMEOFDAY_KEY, ArrayList(confirmedTimeOfDay))
                returnIntent.putExtra(REMAINING_KEY, updatedRemaining.toInt())
                returnIntent.putExtra(START_KEY, updatedStartDate)
                returnIntent.putExtra(END_KEY, updatedEndDate)
                returnIntent.putExtra(POSITION_KEY, initialPosition)
                setResult(RESULT_OK, returnIntent)
                finish()
            }

            builder.setNegativeButton("No") { dialog, _ ->
                enableSaveButtonIfChanges(viewBinding)
                dialog.cancel()
            }

            builder.show()
        }

        viewBinding.delBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirm Deletion")
            builder.setMessage("Are you sure you want to delete this medicine? This action cannot be undone.")

            builder.setPositiveButton("Yes") { _, _ ->
                val returnIntent = Intent()
                returnIntent.putExtra(POSITION_KEY, this.intent.getIntExtra(POSITION_KEY, 0))
                setResult(RESULT_OK, returnIntent)
                finish()
            }

            builder.setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }

            builder.show()
        }

        viewBinding.closeBtn.setOnClickListener {
            finish()
        }

        val unitAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, units)
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewBinding.unitvalSp.adapter = unitAdapter

        val freqAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, frequencies)
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewBinding.freqvalSp.adapter = freqAdapter
        viewBinding.freqvalSp.setOnTouchListener { view, _ ->
            view.performClick()
            isSpinnerInitialized = true
            false
        }

        viewBinding.unitvalSp.setSelection(units.indexOf(initialUnit))
        viewBinding.freqvalSp.setSelection(frequencies.indexOf(initialFrequency))
        previousFreqPosition = frequencies.indexOf(initialFrequency)

        viewBinding.unitvalSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                enableSaveButtonIfChanges(viewBinding)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        viewBinding.freqvalSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (restoringSpinner) {
                    restoringSpinner = false
                    return
                }

                if (!isSpinnerInitialized) return
                isSpinnerInitialized = false

                val newFrequency = parent.getItemAtPosition(position).toString()

                // ✅ New selection: Once/Twice
                if (newFrequency == "Once a day" || newFrequency == "Twice a day") {
                    selectedTimeOfDay.clear()
                    showTimeOfDaySelectionDialog(
                        newFrequency,
                        onConfirm = {
                            previousFreqPosition = position
                            enableSaveButtonIfChanges(viewBinding)
                        },
                        onCancel = {
                            restoringSpinner = true
                            viewBinding.freqvalSp.setSelection(previousFreqPosition)
                            selectedTimeOfDay  = confirmedTimeOfDay.toMutableList()
                            enableSaveButtonIfChanges(viewBinding)
                        }
                    )
                    return
                }

                // ✅ Handle "Three times a day"
                confirmedFrequency = newFrequency
                previousFreqPosition = position
                selectedTimeOfDay = mutableListOf(0, 1, 2)
                confirmedTimeOfDay = selectedTimeOfDay.toMutableList()
                enableSaveButtonIfChanges(viewBinding)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun showTimeOfDaySelectionDialog(
        frequencyLabel: String,
        onConfirm: () -> Unit,
        onCancel: () -> Unit
    ) {
        val dialogBinding = DialogTimeofdaySelectionBinding.inflate(layoutInflater)

        val morningCb = dialogBinding.morningCb
        val afternoonCb = dialogBinding.afternoonCb
        val nightCb = dialogBinding.nightCb
        val freqTv = dialogBinding.freqTv

        freqTv.text = getString(R.string.freqTvText, frequencyLabel)

        val maxSelection = when (frequencyLabel) {
            "Once a day" -> 1
            "Twice a day" -> 2
            else -> Int.MAX_VALUE
        }

        val limitSelections = { checkedCount: Int ->
            morningCb.isEnabled = checkedCount < maxSelection || morningCb.isChecked
            afternoonCb.isEnabled = checkedCount < maxSelection || afternoonCb.isChecked
            nightCb.isEnabled = checkedCount < maxSelection || nightCb.isChecked
        }

        val checkBoxListener = CompoundButton.OnCheckedChangeListener { _, _ ->
            val checkedCount = listOf(morningCb, afternoonCb, nightCb).count { it.isChecked }
            limitSelections(checkedCount)
        }

        morningCb.setOnCheckedChangeListener(checkBoxListener)
        afternoonCb.setOnCheckedChangeListener(checkBoxListener)
        nightCb.setOnCheckedChangeListener(checkBoxListener)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Time of Day")
        builder.setView(dialogBinding.root)

        builder.setPositiveButton("OK") { _, _ ->
            selectedTimeOfDay.clear()
            if (morningCb.isChecked) selectedTimeOfDay.add(0)
            if (afternoonCb.isChecked) selectedTimeOfDay.add(1)
            if (nightCb.isChecked) selectedTimeOfDay.add(2)
            confirmedFrequency  = frequencyLabel
            confirmedTimeOfDay  = selectedTimeOfDay.toMutableList()
            onConfirm()
        }

        builder.setNegativeButton("Cancel") { _, _ ->
            onCancel()
        }

        builder.show()
    }

    private fun disableEditing(viewBinding: ActivityInfoBinding) {
        viewBinding.namevalEt.setText(initialName)
        viewBinding.dosvalEt.setText(initialDosage)
        viewBinding.remvalEt.setText(initialRemaining)
        viewBinding.startvalEt.setText(initialStartDate)
        viewBinding.endvalEt.setText(initialEndDate)
        viewBinding.unitvalSp.setSelection(units.indexOf(initialUnit))
        viewBinding.freqvalSp.setSelection(frequencies.indexOf(initialFrequency))

        viewBinding.namevalEt.isFocusable = false
        viewBinding.namevalEt.isFocusableInTouchMode = false
        viewBinding.dosvalEt.isFocusable = false
        viewBinding.dosvalEt.isFocusableInTouchMode = false
        viewBinding.remvalEt.isFocusable = false
        viewBinding.remvalEt.isFocusableInTouchMode = false

        viewBinding.namevalEt.setBackgroundResource(android.R.color.transparent)
        viewBinding.dosvalEt.setBackgroundResource(android.R.color.transparent)
        viewBinding.remvalEt.setBackgroundResource(android.R.color.transparent)
        viewBinding.startvalEt.setBackgroundResource(android.R.color.transparent)
        viewBinding.endvalEt.setBackgroundResource(android.R.color.transparent)

        viewBinding.freqvalSp.visibility = View.GONE
        viewBinding.unitvalSp.visibility = View.GONE
        viewBinding.dosvalEt.visibility = View.GONE

        viewBinding.dosvalTv.visibility = View.VISIBLE
        viewBinding.dosvalTv.text = getString(R.string.dosvalTvText, initialDosage, initialUnit)

        viewBinding.freqvalTv.visibility = View.VISIBLE
        viewBinding.freqvalTv.text = formatFrequencyLabel(initialFrequency, initialTimeOfDay)

        viewBinding.saveBtn.visibility = View.GONE
        viewBinding.delBtn.visibility = View.GONE
    }

    private fun formatFrequencyLabel(freq: String, timeOfDay: List<Int>): String {
        if (freq == "Three times a day") return freq

        val timeLabels = timeOfDay.mapNotNull {
            when (it) {
                0 -> "Morning"
                1 -> "Afternoon"
                2 -> "Night"
                else -> null
            }
        }

        return if (timeLabels.isNotEmpty()) "$freq: ${timeLabels.joinToString(" & ")}" else freq
    }

    private fun showDatePickerDialog(editText: EditText, dateString: String) {
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = dateFormat.parse(dateString) ?: calendar.time
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

    private fun addTextChangedListeners(viewBinding: ActivityInfoBinding) {
        viewBinding.namevalEt.addTextChangedListener {
            enableSaveButtonIfChanges(viewBinding)
        }

        viewBinding.dosvalEt.addTextChangedListener {
            enableSaveButtonIfChanges(viewBinding)
        }

        viewBinding.remvalEt.addTextChangedListener {
            enableSaveButtonIfChanges(viewBinding)
        }

        viewBinding.startvalEt.addTextChangedListener {
            enableSaveButtonIfChanges(viewBinding)
        }

        viewBinding.endvalEt.addTextChangedListener {
            enableSaveButtonIfChanges(viewBinding)
        }
    }

    private fun enableSaveButtonIfChanges(viewBinding: ActivityInfoBinding) {
        val saveButton = viewBinding.saveBtn
        val isEnabled = hasAnyChanges(viewBinding)

        saveButton.setTextColor(
            if (isEnabled) android.graphics.Color.WHITE
            else viewBinding.root.context.getColor(android.R.color.darker_gray)
        )

        saveButton.isEnabled = isEnabled
    }

    private fun hasAnyChanges(viewBinding: ActivityInfoBinding): Boolean {
        return viewBinding.namevalEt.text.toString() != initialName ||
                viewBinding.dosvalEt.text.toString() != initialDosage ||
                viewBinding.remvalEt.text.toString() != initialRemaining ||
                viewBinding.startvalEt.text.toString() != initialStartDate ||
                viewBinding.endvalEt.text.toString() != initialEndDate ||
                viewBinding.unitvalSp.selectedItem?.toString() != initialUnit ||
                viewBinding.freqvalSp.selectedItem?.toString() != initialFrequency ||
                selectedTimeOfDay != initialTimeOfDay
    }
}

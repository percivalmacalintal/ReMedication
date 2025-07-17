package ph.edu.dlsu.ccs.mobicom.remedication

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale
import java.util.concurrent.Executors


class InfoActivity : ComponentActivity() {
    companion object {
        const val ID_KEY = "ID_KEY"
        const val IMAGE_KEY = "IMAGE_KEY"
        const val NAME_KEY = "NAME_KEY"
        const val DOSAGE_KEY = "DOSAGE_KEY"
        const val UNIT_KEY = "UNIT_KEY"
        const val FREQUENCY_KEY = "FREQUENCY_KEY"
        const val TIMEOFDAY_KEY = "TIMEOFDAY_KEY"
        const val REMAINING_KEY = "REMAINING_KEY"
        const val START_KEY = "START_KEY"
        const val END_KEY = "END_KEY"
        const val POSITION_KEY = "POSITION_KEY"

        //Results
        const val RESULT_EDIT = 200
        const val RESULT_DELETE = 300
    }

    private val executorService = Executors.newSingleThreadExecutor()
    private lateinit var viewBinding: ActivityInfoBinding
    private lateinit var myDbHelper: MedicineDbHelper

    private var medicineId: Long = -1
    private var initialImage: String = ""
    private var initialName: String = ""
    private var initialDosage: String = ""
    private var initialUnit: String = ""
    private var initialFrequency: String = ""
    private var initialTimeOfDay: List<Int> = listOf()
    private var initialRemaining: String = ""
    private var initialStartDate: String = ""
    private var initialEndDate: String = ""
//    private var initialPosition: Int = -1

    private var isEditing = false
    private var defaultEditTextBackground: Drawable? = null
    private var selectedTimeOfDay = mutableListOf<Int>()

    private var isSpinnerInitialized = false
    private var previousFreqPosition = 0
    private var restoringSpinner = false
    private var confirmedTimeOfDay = mutableListOf<Int>()
    private var confirmedFrequency = ""

    private val units = arrayOf("mg", "ml")
    private val frequencies = arrayOf("Once a day", "Twice a day", "Thrice a day")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        myDbHelper = MedicineDbHelper.getInstance(this@InfoActivity)!!

        medicineId = this.intent.getLongExtra(ID_KEY, -1)
        initialImage = this.intent.getStringExtra(IMAGE_KEY) ?: ""
        initialName = this.intent.getStringExtra(NAME_KEY) ?: ""
        initialName = this.intent.getStringExtra(NAME_KEY) ?: ""
        initialDosage = this.intent.getIntExtra(DOSAGE_KEY, 0).toString()
        initialUnit = this.intent.getStringExtra(UNIT_KEY) ?: ""
        initialFrequency = this.intent.getStringExtra(FREQUENCY_KEY) ?: ""
        initialTimeOfDay = this.intent.getIntegerArrayListExtra(TIMEOFDAY_KEY) ?: listOf()
        initialRemaining = this.intent.getIntExtra(REMAINING_KEY, 0).toString()
        initialStartDate = this.intent.getStringExtra(START_KEY) ?: ""
        initialEndDate = this.intent.getStringExtra(END_KEY) ?: ""
//        initialPosition = this.intent.getIntExtra(POSITION_KEY, -1)

        confirmedFrequency  = initialFrequency
        confirmedTimeOfDay = initialTimeOfDay.toMutableList()

        val imageUri = Uri.fromFile(File(initialImage))
        viewBinding.medicineIv.setImageURI(imageUri)  // Set the image using the Uri

        viewBinding.namevalEt.setText(initialName)
        viewBinding.dosvalEt.setText(initialDosage)
        viewBinding.remvalEt.setText(initialRemaining)
        viewBinding.startvalEt.setText(initialStartDate)
        viewBinding.endvalEt.setText(initialEndDate)

        defaultEditTextBackground = viewBinding.namevalEt.background

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

        disableEditing()

        viewBinding.edtBtn.setOnClickListener {
            isEditing = !isEditing

            if (isEditing) {
                viewBinding.namevalEt.isFocusable = true
                viewBinding.namevalEt.isFocusableInTouchMode = true
                viewBinding.dosvalEt.isFocusable = true
                viewBinding.dosvalEt.isFocusableInTouchMode = true
                viewBinding.remvalEt.isFocusable = true
                viewBinding.remvalEt.isFocusableInTouchMode = true

                viewBinding.namevalEt.background = defaultEditTextBackground
                viewBinding.dosvalEt.background = defaultEditTextBackground
                viewBinding.remvalEt.background = defaultEditTextBackground
                viewBinding.startvalEt.background = defaultEditTextBackground
                viewBinding.endvalEt.background = defaultEditTextBackground

                viewBinding.freqvalSp.visibility = View.VISIBLE
                viewBinding.unitvalSp.visibility = View.VISIBLE
                viewBinding.dosvalEt.visibility = View.VISIBLE

                viewBinding.dosvalTv.visibility = View.GONE

                if (confirmedFrequency == "Three times a day") {
                    viewBinding.freqvalTv.visibility = View.GONE
                } else {
                    viewBinding.freqvalTv.text = formatTimeOfDayOnly(confirmedTimeOfDay)
                    viewBinding.freqvalTv.gravity = android.view.Gravity.CENTER
                    viewBinding.freqvalTv.visibility = View.VISIBLE
                }

                viewBinding.saveBtn.visibility = View.VISIBLE
                viewBinding.delBtn.visibility = View.VISIBLE
                viewBinding.edtImgBtn.visibility = View.VISIBLE

                enableSaveButtonIfChanges(viewBinding)
            } else {
                disableEditing()
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
                executorService.execute {
                    val updatedImage = this.intent.getStringExtra(IMAGE_KEY) ?: ""   //change when editable
                    val medicine = Medicine(
                        updatedImage,
                        updatedName,
                        updatedDosage.toInt(),
                        updatedUnit,
                        updatedFrequency,
                        ArrayList(confirmedTimeOfDay),
                        updatedRemaining.toInt(),
                        updatedStartDate,
                        updatedEndDate
                    )
                    myDbHelper.updateMedicine(medicine, medicineId)

                    val returnIntent = Intent()
                    returnIntent.putExtra(ID_KEY, medicineId)
                    returnIntent.putExtra(IMAGE_KEY, updatedImage)
                    returnIntent.putExtra(NAME_KEY, updatedName)
                    returnIntent.putExtra(DOSAGE_KEY, updatedDosage.toInt())
                    returnIntent.putExtra(UNIT_KEY, updatedUnit)
                    returnIntent.putExtra(FREQUENCY_KEY, updatedFrequency)
                    returnIntent.putIntegerArrayListExtra(TIMEOFDAY_KEY, ArrayList(confirmedTimeOfDay))
                    returnIntent.putExtra(REMAINING_KEY, updatedRemaining.toInt())
                    returnIntent.putExtra(START_KEY, updatedStartDate)
                    returnIntent.putExtra(END_KEY, updatedEndDate)
//                    returnIntent.putExtra(POSITION_KEY, initialPosition)
                    setResult(RESULT_EDIT, returnIntent)
                    finish()
                }
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
                val rowsDeleted = this.myDbHelper.deleteMedicine(medicineId)
                if (rowsDeleted > 0) {
                    val returnIntent = Intent()
                    returnIntent.putExtra(POSITION_KEY, this.intent.getIntExtra(POSITION_KEY, 0))
                    setResult(RESULT_DELETE, returnIntent)
                    finish()
                } else {
                    Log.e("InfoActivity", "Delete failed for contact with id $medicineId")
                }
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
                if (restoringSpinner) { restoringSpinner = false; return }
                if (!isSpinnerInitialized) return
                isSpinnerInitialized = false

                val newFrequency = parent.getItemAtPosition(position).toString()

                selectedTimeOfDay.clear()
                showTimeOfDaySelectionDialog(
                    newFrequency,
                    onConfirm = {
                        confirmedFrequency = newFrequency
                        previousFreqPosition = position
                        enableSaveButtonIfChanges(viewBinding)
                    },
                    onCancel = {
                        restoringSpinner = true
                        viewBinding.freqvalSp.setSelection(previousFreqPosition)
                        selectedTimeOfDay = confirmedTimeOfDay.toMutableList()
                        enableSaveButtonIfChanges(viewBinding)
                    }
                )
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

        val afterMidnightCb = dialogBinding.afterMidnightCb
        val morningCb = dialogBinding.morningCb
        val afternoonCb = dialogBinding.afternoonCb
        val nightCb = dialogBinding.nightCb
        val freqTv = dialogBinding.freqTv

        freqTv.text = getString(R.string.freqTvText, frequencyLabel)

        val maxSelection = when (frequencyLabel) {
            "Once a day" -> 1
            "Twice a day" -> 2
            "Thrice a day" -> 2
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
            confirmedFrequency  = frequencyLabel
            confirmedTimeOfDay  = selectedTimeOfDay.toMutableList()
            if (isEditing) {
                viewBinding.freqvalTv.text = formatTimeOfDayOnly(selectedTimeOfDay)
                viewBinding.freqvalTv.gravity = android.view.Gravity.CENTER
                viewBinding.freqvalTv.visibility = View.VISIBLE
            }
            onConfirm()
        }

        builder.setNegativeButton("Cancel") { _, _ ->
            onCancel()
        }

        builder.show()
    }

    private fun disableEditing() {
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
        viewBinding.freqvalTv.gravity = android.view.Gravity.START

        viewBinding.saveBtn.visibility = View.GONE
        viewBinding.delBtn.visibility = View.GONE

        viewBinding.edtImgBtn.visibility = View.GONE
    }

    private fun formatFrequencyLabel(freq: String, timeOfDay: List<Int>): String {
        val timeLabels = timeOfDay.mapNotNull {
            when (it) {
                0 -> "Early Morning"
                1 -> "Morning"
                2 -> "Afternoon"
                3 -> "Night"
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

    private fun enableSaveButtonIfChanges(viewBinding: ActivityInfoBinding) {
        val saveButton = viewBinding.saveBtn
        val isEnabled = viewBinding.namevalEt.text.toString() != initialName ||
                viewBinding.dosvalEt.text.toString() != initialDosage ||
                viewBinding.remvalEt.text.toString() != initialRemaining ||
                viewBinding.startvalEt.text.toString() != initialStartDate ||
                viewBinding.endvalEt.text.toString() != initialEndDate ||
                viewBinding.unitvalSp.selectedItem?.toString() != initialUnit ||
                viewBinding.freqvalSp.selectedItem?.toString() != initialFrequency ||
                selectedTimeOfDay != initialTimeOfDay

        saveButton.setTextColor(
            if (isEnabled) android.graphics.Color.WHITE
            else viewBinding.root.context.getColor(android.R.color.darker_gray)
        )

        saveButton.isEnabled = isEnabled
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

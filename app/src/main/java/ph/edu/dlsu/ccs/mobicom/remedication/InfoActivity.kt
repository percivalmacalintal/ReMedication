package ph.edu.dlsu.ccs.mobicom.remedication

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.core.widget.addTextChangedListener
import ph.edu.dlsu.ccs.mobicom.remedication.databinding.ActivityInfoBinding
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
    private var initialRemaining: String = ""
    private var initialStartDate: String = ""
    private var initialEndDate: String = ""
    private var initialPosition: Int = -1

    private var defaultEditTextBackground: Drawable? = null
    private var defaultSpinnerBackground: Drawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBinding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initialName = this.intent.getStringExtra(NAME_KEY) ?: ""
        initialDosage = this.intent.getIntExtra(DOSAGE_KEY, 0).toString()
        initialUnit = this.intent.getStringExtra(UNIT_KEY) ?: ""
        initialFrequency = this.intent.getStringExtra(FREQUENCY_KEY) ?: ""
        initialRemaining = this.intent.getIntExtra(REMAINING_KEY, 0).toString()
        initialStartDate = this.intent.getStringExtra(START_KEY) ?: ""
        initialEndDate = this.intent.getStringExtra(END_KEY) ?: ""
        initialPosition = this.intent.getIntExtra(POSITION_KEY, -1)

        viewBinding.namevalEt.setText(initialName)
        viewBinding.dosvalEt.setText(initialDosage)
        viewBinding.remvalEt.setText(initialRemaining)
        viewBinding.startvalEt.setText(initialStartDate)
        viewBinding.endvalEt.setText(initialEndDate)

        defaultEditTextBackground = viewBinding.namevalEt.background
        defaultSpinnerBackground = viewBinding.freqvalSpin.background

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

                viewBinding.unitvalSpin.isEnabled = true
                viewBinding.freqvalSpin.isEnabled = true

                viewBinding.namevalEt.background = defaultEditTextBackground
                viewBinding.dosvalEt.background = defaultEditTextBackground
                viewBinding.remvalEt.background = defaultEditTextBackground
                viewBinding.startvalEt.background = defaultEditTextBackground
                viewBinding.endvalEt.background = defaultEditTextBackground

                viewBinding.unitvalSpin.background = defaultSpinnerBackground
                viewBinding.freqvalSpin.background = defaultSpinnerBackground

                viewBinding.saveBtn.visibility = Button.VISIBLE
            } else {
                disableEditing(viewBinding)
            }
        }

        viewBinding.saveBtn.setOnClickListener {
            val updatedName = viewBinding.namevalEt.text.toString()
            val updatedStartDate = viewBinding.startvalEt.text.toString()
            val updatedEndDate = viewBinding.endvalEt.text.toString()

            val updatedDosage = viewBinding.dosvalEt.text.toString().toInt()
            val updatedRemaining = viewBinding.remvalEt.text.toString().toInt()

            val updatedUnit = viewBinding.unitvalSpin.selectedItem.toString()
            val updatedFrequency = viewBinding.freqvalSpin.selectedItem.toString()

            val returnIntent = Intent()
            returnIntent.putExtra(NAME_KEY, updatedName)
            returnIntent.putExtra(DOSAGE_KEY, updatedDosage)
            returnIntent.putExtra(UNIT_KEY, updatedUnit)
            returnIntent.putExtra(FREQUENCY_KEY, updatedFrequency)
            returnIntent.putExtra(REMAINING_KEY, updatedRemaining)
            returnIntent.putExtra(START_KEY, updatedStartDate)
            returnIntent.putExtra(END_KEY, updatedEndDate)
            returnIntent.putExtra(POSITION_KEY, initialPosition)
            setResult(RESULT_OK, returnIntent)
            finish()
        }

        viewBinding.delBtn.setOnClickListener {
            val returnIntent = Intent()
            returnIntent.putExtra(POSITION_KEY, this.intent.getIntExtra(POSITION_KEY, 0))
            setResult(RESULT_OK, returnIntent)
            finish()
        }

        viewBinding.closeBtn.setOnClickListener {
            finish()
        }

        val units = arrayOf("mg", "ml")
        val frequencies = arrayOf("Once a day", "Twice a day", "Three times a day", "Every other day")

        val unitAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, units)
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewBinding.unitvalSpin.adapter = unitAdapter

        val freqAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, frequencies)
        freqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewBinding.freqvalSpin.adapter = freqAdapter

        viewBinding.unitvalSpin.setSelection(units.indexOf(initialUnit))
        viewBinding.freqvalSpin.setSelection(frequencies.indexOf(initialFrequency))

        viewBinding.unitvalSpin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                initialUnit = parent.getItemAtPosition(position).toString()
                enableSaveButtonIfChanges(viewBinding)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        viewBinding.freqvalSpin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                initialFrequency = parent.getItemAtPosition(position).toString()
                enableSaveButtonIfChanges(viewBinding)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun disableEditing(viewBinding: ActivityInfoBinding) {
        viewBinding.namevalEt.setText(initialName)
        viewBinding.dosvalEt.setText(initialDosage)
        viewBinding.remvalEt.setText(initialRemaining)
        viewBinding.startvalEt.setText(initialStartDate)
        viewBinding.endvalEt.setText(initialEndDate)

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

        viewBinding.saveBtn.visibility = Button.GONE
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
        viewBinding.saveBtn.isEnabled = hasAnyChanges(viewBinding)
    }

    private fun hasAnyChanges(viewBinding: ActivityInfoBinding): Boolean {
        return viewBinding.namevalEt.text.toString() != initialName ||
                viewBinding.dosvalEt.text.toString() != initialDosage ||
                viewBinding.remvalEt.text.toString() != initialRemaining ||
                viewBinding.startvalEt.text.toString() != initialStartDate ||
                viewBinding.endvalEt.text.toString() != initialEndDate ||
                viewBinding.unitvalSpin.selectedItem?.toString() != initialUnit ||
                viewBinding.freqvalSpin.selectedItem?.toString() != initialFrequency
    }
}

package com.example.taskmanagement.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.taskmanagement.R
import com.example.taskmanagement.databinding.FragmentAddTaskManagementPopUpBinding
import com.example.taskmanagement.utils.TaskManagementData
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTaskManagementPopUpFragment : DialogFragment() {

    private lateinit var binding: FragmentAddTaskManagementPopUpBinding
    private lateinit var listener: DialogNextBtnClickListener
    private var taskManagementData: TaskManagementData? = null
    private var selectedDateTime: String? = null


    fun setListener(listener: DialogNextBtnClickListener) {
        this.listener = listener
    }

    companion object {
        const val TAG = "AddTaskManagementPopUpFragment"

        @JvmStatic
        fun newInstance(
            taskId: String,
            task: String,
            endDateTime: String,
            isCompleted: Boolean,
            isMissed: Boolean
        ) = AddTaskManagementPopUpFragment().apply {
            arguments = Bundle().apply {
                putString("taskId", taskId)
                putString("task", task)
                putString("endDateTime", endDateTime)
                putBoolean("isCompleted", isCompleted)
                putBoolean("isMissed", isMissed)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddTaskManagementPopUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // If task data is passed, populate the fields
        if (arguments != null) {
            taskManagementData = TaskManagementData(
                taskId = arguments?.getString("taskId").toString(),
                task = arguments?.getString("task").toString(),
                endDateTime = arguments?.getString("endDateTime").toString(),
                isCompleted = arguments?.getBoolean("isCompleted") ?: false,
                isMissed = arguments?.getBoolean("isMissed") ?: false
            )
            binding.taskManagementEt.setText(taskManagementData?.task)
           // binding.dateTimeTextView.Text = taskManagementData?.endDateTime
            binding.dateTimeTextView.text = taskManagementData?.endDateTime
        }

        registerEvents()
    }

    private fun registerEvents() {
        binding.taskManagementNextBtn.setOnClickListener {
            val taskManagementTask = binding.taskManagementEt.text.toString()

            if (taskManagementTask.isNotEmpty() && !selectedDateTime.isNullOrEmpty()) {
                if (taskManagementData == null) {
                    // New Task
                    listener.onSaveTask(taskManagementTask, selectedDateTime!!, binding.taskManagementEt)
                } else {
                    // Update Task
                    taskManagementData?.task = taskManagementTask
                   // taskManagementData?.endDateTime = selectedDateTime ?: taskManagementData?.endDateTime
                    taskManagementData?.endDateTime = (selectedDateTime ?: taskManagementData?.endDateTime).toString()
                    listener.onUpdateTask(taskManagementData!!,binding.taskManagementEt)

                }
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.TaskManagementClose.setOnClickListener {
            dismiss()
        }

        // Date & Time Picker
        binding.dateTimePickerBtn.setOnClickListener {
            showDateTimePicker()
        }
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()

        // Date Picker
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Time Picker
                val timePicker = TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        selectedDateTime = dateFormat.format(calendar.time)
                        binding.dateTimeTextView.text = selectedDateTime
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                )
                timePicker.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    interface DialogNextBtnClickListener {
        fun onSaveTask(task: String, endDateTime: String, taskManagementEt: TextInputEditText)
        fun onUpdateTask(taskManagementData: TaskManagementData, taskManagementEt: TextInputEditText)
    }
}

package com.example.taskmanagement.utils

import java.text.SimpleDateFormat
import java.util.Locale

data class TaskManagementData(
    val taskId: String = "",
    var task: String = "",
    var endDateTime: String = "",
    var isCompleted: Boolean = false,
    var isMissed: Boolean = false
) {
    // Function to get the task status
    fun getTaskStatus(): String {
        val currentDateTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val taskEndDateTime = try {
            dateFormat.parse(endDateTime)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }

        return when {
            !isMissed && isCompleted -> "Completed"
            !isCompleted && taskEndDateTime < currentDateTime -> "Missed"
            else -> "Pending"
        }
    }
}



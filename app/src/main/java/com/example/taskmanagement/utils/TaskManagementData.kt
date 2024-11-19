package com.example.taskmanagement.utils

import java.text.SimpleDateFormat
import java.util.Locale

data class TaskManagementData(
    val taskId: String = "",
    var task: String = "",
    var endDateTime: String = "",
    var isCompleted: Boolean = false,
    var isMissed: Boolean = false,
    var taskStatus: String = "" // Add taskStatus field
)



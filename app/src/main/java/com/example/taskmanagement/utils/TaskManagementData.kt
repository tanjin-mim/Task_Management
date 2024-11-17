package com.example.taskmanagement.utils

data class TaskManagementData(
    val taskId: String = "",
    var task: String = "",
    var endDateTime: String = "",
    var isCompleted: Boolean = false,
    var isMissed: Boolean = false
)


package com.example.taskmanagement.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters


class NotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val taskId = inputData.getString("taskId") ?: return Result.failure()
        val taskTitle = inputData.getString("taskTitle") ?: "Task Reminder"
        val endDateTime = inputData.getString("endDateTime") ?: return Result.failure()

        val notificationHelper = NotificationHelper(context)
        notificationHelper.showNotification(
            taskId,
            "Task Reminder: $taskTitle",
            "Your task is due at $endDateTime."
        )
        return Result.success()
    }
}



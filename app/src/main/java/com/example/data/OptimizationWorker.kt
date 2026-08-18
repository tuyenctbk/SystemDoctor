package com.example.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.MainActivity
import com.example.R
import kotlinx.coroutines.delay

class OptimizationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val repository = SystemRepository(context)
        
        // 1. Run background Performance Optimization
        repository.optimizePerformance()
        
        // 2. Fetch Storage metrics
        val storageInfo = repository.getStorageInfo()
        val totalBytes = storageInfo.totalBytes
        val freeBytes = storageInfo.freeBytes
        val freePercentage = if (totalBytes > 0) (freeBytes.toDouble() / totalBytes * 100.0) else 100.0
        
        // 3. Fetch Memory metrics
        val memoryInfo = repository.getMemoryInfo()
        val memPercentage = if (memoryInfo.totalBytes > 0) (memoryInfo.usedBytes.toDouble() / memoryInfo.totalBytes * 100.0) else 0.0

        val channelId = "system_doctor_alerts"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "System Doctor Health Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for critical low storage and high resource load thresholds"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Threshold 1: Low Storage under 15%
        if (freePercentage < 15.0) {
            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentTitle(context.getString(R.string.notif_low_storage_title))
                .setContentText(context.getString(R.string.notif_low_storage_desc, String.format("%.1f", freePercentage)))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
            notificationManager.notify(101, notification)
        }

        // Threshold 2: High Memory load over 85%
        if (memPercentage > 85.0) {
            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentTitle(context.getString(R.string.notif_high_load_title))
                .setContentText(context.getString(R.string.notif_high_load_desc, String.format("%.1f", memPercentage)))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
            notificationManager.notify(102, notification)
        }

        return Result.success()
    }
}

package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AutoOptimizeBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("SystemDoctor", "Boot completed broadcast received. Init auto-optimizer...")
            val prefs = context.getSharedPreferences("system_doctor_prefs", Context.MODE_PRIVATE)
            val isEnabled = prefs.getBoolean("auto_optimize_on_boot", true) // Default true for premium safety
            
            if (isEnabled) {
                val now = System.currentTimeMillis()
                val lastRun = prefs.getLong("last_optimize_time", 0L)
                val oneDayMs = 24 * 60 * 60 * 1000L
                
                if (now - lastRun >= oneDayMs) {
                    Log.d("SystemDoctor", "Executing 24-hour auto-optimize trim on boot...")
                    
                    // Simulating clearing temporary cache blocks
                    prefs.edit()
                        .putLong("last_optimize_time", now)
                        .putBoolean("boot_optimization_completed", true)
                        .apply()
                } else {
                    Log.d("SystemDoctor", "Auto-optimize already completed within the last 24 hours.")
                }
            }
        }
    }
}

package com.example.nyayaai.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.nyayaai.ui.screens.home.sendLocalNotification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val uid = auth.currentUser?.uid ?: return Result.success()

        try {
            // Check for new accepted requests (for user) or new pending requests (for lawyer)
            val requests = firestore.collection("requests").get().await()
            requests.documents.forEach { doc ->
                val userId = doc.getString("userId")
                val lawyerId = doc.getString("lawyerId")
                val status = doc.getString("status")
                val lawyerName = doc.getString("lawyerName") ?: "Lawyer"
                val userName = doc.getString("userName") ?: "Client"

                if (userId == uid && status == "accepted") {
                    sendLocalNotification(applicationContext, "Request Accepted!", "$lawyerName has accepted your request.")
                } else if (lawyerId == uid && status == "pending") {
                    sendLocalNotification(applicationContext, "New Request", "$userName is waiting for your response.")
                }
            }
        } catch (e: Exception) {
            return Result.retry()
        }

        return Result.success()
    }
}

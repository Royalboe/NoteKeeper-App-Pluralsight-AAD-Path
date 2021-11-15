package com.example.notekeeperapp

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat


object ReminderNotification {
    private const val NOTIFICATION_TAG = "Reminder"

    private const val REMINDER_CHANNEL = "Reminder"
    const val KEY_TEXT_REPLY = "KeyTextReply"

    fun notify (context: Context, note: NoteInfo, notePosition: Int) {
        val res = context.resources

        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(NOTE_POSITION, notePosition)

        val remoteInput = RemoteInput.Builder(KEY_TEXT_REPLY)
            .setLabel("Add Note")
            .build()

        val replyIntent = Intent(context, NotificationBroadcastReceiver::class.java)
        replyIntent.putExtra(NOTE_POSITION, notePosition)

        val replyPendingIntent = PendingIntent.getBroadcast(context,
            100,
            replyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        val replyAction = NotificationCompat.Action.Builder(
            R.drawable.ic_reply_black,
            "Add Note", replyPendingIntent)
            .addRemoteInput(remoteInput)
            .build()


        val pendingIntent = TaskStackBuilder.create(context)
            .addNextIntentWithParentStack(intent)
            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        val message1 = NotificationCompat.MessagingStyle.Message(
            note.comments[0].comment,
            note.comments[0].timestamp,
            note.comments[0].name
        )

        val message2 = NotificationCompat.MessagingStyle.Message(
            note.comments[1].comment,
            note.comments[1].timestamp,
            note.comments[1].name
        )

        val message3 = NotificationCompat.MessagingStyle.Message(
            note.comments[2].comment,
            note.comments[2].timestamp,
            note.comments[2].name
        )

        val builder = NotificationCompat.Builder(context, REMINDER_CHANNEL)
            //set appropriate defaults for the notification light, sound and vibration
            .setDefaults(Notification.DEFAULT_ALL)
            // Set required fields, including the small icon, the notification title and the text
            .setSmallIcon(R.drawable.ic_stat_reminder)
            .setContentTitle("Comment from ${note.comments[0].name}")
            .setContentText(note.comments[0].comment)
            //All fields below this line are optional
            // Use a default property recognized on devices running android 4.1 and later
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentTitle("Comments about ${note.title}")
            // Set ticker text (preview) information for this notification.
            .setTicker("Comments about ${note.title}")
            // Set the pending intent to be initiated when the user touches
            // the notification.
            .setContentIntent(pendingIntent)
            // Automatically dismiss the notification when it is touched.
            .setAutoCancel(true)
            .setLargeIcon(BitmapFactory
                .decodeResource(res, R.drawable.logo))
            .setStyle(NotificationCompat.MessagingStyle("You")
                .setConversationTitle(note.title)
                .addMessage(message3)
                .addMessage(message2)
                .addMessage(message1)
            )
            .addAction(replyAction)
            .setColorized(true)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setOnlyAlertOnce(true)

        notify(context, builder.build())
    }

    private fun notify(context: Context, notification: Notification) {
        val nm = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification)
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification)
        }
    }

    /**
     * Cancels any notifications of this type previously shown using
     * [.notify].
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    fun cancel(context: Context) {
        val nm = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0)
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode())
        }
    }
}

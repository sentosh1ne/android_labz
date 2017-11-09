package com.labz.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.labz.R
import com.labz.main.MainActivity
import com.labz.todo_list.models.ToDo
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

/**
 * Created by Stanislav Vylegzhanin on 18.10.17.
 */
class TodoReceiver : BroadcastReceiver(), AnkoLogger {
    companion object {
        val NOTIFICATION_TODO_EXTRA = "notification_todo_extra"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val redirectIntent = Intent(context, MainActivity::class.java)
        val todo = intent?.getParcelableExtra<ToDo>(NOTIFICATION_TODO_EXTRA)

        redirectIntent.putExtra(NOTIFICATION_TODO_EXTRA, todo)

        val contentIntent = PendingIntent.getActivity(context, 0,
                redirectIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        var builder = NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Your todo time is up")
                .setContentText(todo?.note)
                .setContentIntent(contentIntent)
                .setDefaults(Notification.DEFAULT_SOUND)

        val manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = 12312
        manager.notify(notificationID, builder.build())
    }
}
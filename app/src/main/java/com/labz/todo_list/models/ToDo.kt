package com.labz.todo_list.models

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import com.labz.R
import com.labz.notification.TodoReceiver
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Created by Stanislav Vylegzhanin on 11.10.17.
 */
@PaperParcel
data class ToDo(val id: Int?, var note: String?, var picture: String?, var date: Long?, var importance: Importance?) : PaperParcelable, AnkoLogger {
    companion object {
        @JvmField
        val CREATOR = PaperParcelToDo.CREATOR
    }
}

fun ToDo.scheduleAlarm(context: Context?) {
    val notificationIntent = Intent(context, TodoReceiver::class.java)
    notificationIntent.putExtra(TodoReceiver.NOTIFICATION_TODO_EXTRA, this)
    info(this)
    val pendingIntent = PendingIntent.getBroadcast(context, 0,
            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.set(AlarmManager.RTC_WAKEUP, date!!, pendingIntent)
}

enum class Importance(val importance: Int) {
    LOW(1),
    MEDIUM(2),
    HIGH(3);

    companion object {
        fun fromInt(value: Int): Importance {
            return when (value) {
                1 -> LOW
                2 -> MEDIUM
                3 -> HIGH
                else -> LOW
            }
        }
    }
}

@ColorRes
fun Importance?.importanceColor(): Int {
    return when (this) {
        Importance.LOW -> R.color.blueHighlight
        Importance.MEDIUM -> R.color.accent_material_light
        Importance.HIGH -> R.color.redHighlight
        else -> R.color.blueHighlight
    }
}

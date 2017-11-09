package com.labz.db

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.labz.base.fragment.BaseFragment
import com.labz.db.ToDoTable.DATE
import com.labz.db.ToDoTable.IMPORTANCE
import com.labz.db.ToDoTable.NOTE
import com.labz.db.ToDoTable.PICTURE
import com.labz.db.ToDoTable.TODO_TABLE
import com.labz.db.ToDoTable._ID
import com.labz.todo_list.models.Importance
import com.labz.todo_list.models.ToDo
import org.jetbrains.anko.db.*

/**
 * Created by Stanislav Vylegzhanin on 11.10.17.
 */

class DatabaseHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, DB_NAME, null, 1) {
    companion object {
        val DB_NAME = "ToDoDb"

        private var instance: DatabaseHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): DatabaseHelper {
            if (instance == null) {
                instance = DatabaseHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Here you create tables
        createTodoTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
        db.dropTable(TODO_TABLE, true)
    }
}

val Context.database: DatabaseHelper
    get() = DatabaseHelper.getInstance(applicationContext)

val createTodoTable = fun(db: SQLiteDatabase) {
    db.createTable(TODO_TABLE, true,
            _ID to INTEGER + PRIMARY_KEY + UNIQUE,
            NOTE to TEXT,
            IMPORTANCE to INTEGER,
            PICTURE to TEXT,
            DATE to INTEGER)
}

object ToDoTable {
    val TODO_TABLE = "ToDoTable"
    val _ID = "id"
    val NOTE = "note"
    val IMPORTANCE = "importance"
    val PICTURE = "picture"
    val DATE = "date"

    fun Cursor.buildToDoList(): ArrayList<ToDo>? {
        val result = ArrayList<ToDo>()
        while (this.moveToNext()) {
            try {
                val idIndex = this.getColumnIndexOrThrow(_ID)
                val noteIndex = this.getColumnIndexOrThrow(NOTE)
                val importanceIndex = this.getColumnIndex(IMPORTANCE)
                val pictureIndex = this.getColumnIndex(PICTURE)
                val dateIndex = this.getColumnIndex(DATE)

                val id = this.getInt(idIndex)
                val note = this.getString(noteIndex)
                val importance = Importance.fromInt(this.getInt(importanceIndex))
                val picture = this.getString(pictureIndex)
                val date = this.getLong(dateIndex)

                result.add(ToDo(id = id, note = note, importance = importance, picture = picture, date = date))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return result
    }
}

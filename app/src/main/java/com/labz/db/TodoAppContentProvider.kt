package com.labz.db

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import com.labz.BuildConfig


/**
 * Created by Sentosh1ne on 15.10.2017.
 */
class TodoAppContentProvider : ContentProvider() {

    companion object {
        val AUTHORITY = BuildConfig.APPLICATION_ID + ".ToDoAppContentProvider"
        val CONTENT_URI = Uri.parse("content://$AUTHORITY/todo")

        private val TODO_TABLE_ID = 1
        private var URI_MATCHER: UriMatcher? = null

        init {
            URI_MATCHER = UriMatcher(UriMatcher.NO_MATCH)
            URI_MATCHER?.addURI(AUTHORITY, "todo/" + ToDoTable.TODO_TABLE, TODO_TABLE_ID)
        }
    }

    private var dbHelper: DatabaseHelper? = null

    override fun onCreate(): Boolean {
        dbHelper = DatabaseHelper(context)
        return true
    }

    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?,
                       selectionArgs: Array<out String>?, sortOrder: String?): Cursor {
        val qb = SQLiteQueryBuilder()
        qb.tables = ToDoTable.TODO_TABLE
        return qb.query(dbHelper?.writableDatabase, projection, selection, selectionArgs, null, null, null)
    }

    override fun insert(p0: Uri?, p1: ContentValues?): Uri? {
        return null
    }

    override fun update(p0: Uri?, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        return 0
    }

    override fun delete(p0: Uri?, p1: String?, p2: Array<out String>?): Int {
        return 0
    }

    override fun getType(uri: Uri?): String {
        return ""
    }
}
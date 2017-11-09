package com.labz.todo_list.ui.todo_list

import android.content.Context
import com.labz.db.ToDoTable
import com.labz.db.ToDoTable.buildToDoList
import com.labz.db.database
import com.labz.todo_list.models.Importance
import com.labz.todo_list.models.ToDo
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.db.select

/**
 * Created by Stanislav Vylegzhanin on 19.10.17.
 */
interface TodoInteractor {
    fun fetchToDoList()
    fun fetchToDoList(pattern: String, importance: Importance?)
}

interface TodoPresenter {
    val context: Context
    fun onDataFetched(list: ArrayList<ToDo>?)
    fun fetchTodoList()
    fun fetchTodoList(pattern: String, importance: Importance?)
}

interface TodoView {
    fun onDataFetched(list: ArrayList<ToDo>?)
}


class TodoInteractorImpl(private val presenter: TodoPresenter) : TodoInteractor, AnkoLogger {

    override fun fetchToDoList(pattern: String, importance: Importance?) {
        var whereClause = "${ToDoTable.NOTE} LIKE {note} "

        if (importance != null) {
            whereClause += "AND ${ToDoTable.IMPORTANCE} = ${importance.importance}"
        }

        async(UI) {
            val list = bg {
                presenter.context.database.use {
                    select(ToDoTable.TODO_TABLE)
                            .whereArgs(whereClause, "note" to ("%$pattern%"))
                            .exec {
                                this.buildToDoList()
                            }
                }
            }

            presenter.onDataFetched(list.await())
        }
    }


    override fun fetchToDoList() {
        async(UI) {
            val list = bg {
                presenter.context.database.use {
                    select(ToDoTable.TODO_TABLE).exec {
                        this.buildToDoList()
                    }
                }
            }
            presenter.onDataFetched(list.await())
        }
    }
}


class TodoPresenterImpl(override val context: Context, private val view: TodoView) : TodoPresenter {
    private val interactor = TodoInteractorImpl(this)

    override fun fetchTodoList(pattern: String, importance: Importance?) {
        interactor.fetchToDoList(pattern, importance)
    }

    override fun fetchTodoList() {
        interactor.fetchToDoList()
    }

    override fun onDataFetched(list: ArrayList<ToDo>?) {
        view.onDataFetched(list)
    }

}




package com.labz.todo_list.ui.update_todo

import android.content.Context
import com.labz.db.ToDoTable
import com.labz.db.database
import com.labz.todo_list.models.ToDo
import com.labz.todo_list.models.scheduleAlarm
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.update

/**
 * Created by Stanislav Vylegzhanin on 24.10.17.
 */
interface EditTodoView {
    fun onActionCompleted()
}

interface EditTodoPresenter {
    var context: Context

    fun onActionCompleted()

    fun addToDo(todo: ToDo?)

    fun updateToDo(todo: ToDo?)

    fun deleteTodo(id: Int?)
}

interface EditTodoInteractor {
    fun addToDo(todo: ToDo?)

    fun updateToDo(todo: ToDo?)

    fun deleteTodo(id: Int?)
}

class EditTodoInteractorImpl(private val presenter: EditTodoPresenter) : EditTodoInteractor, AnkoLogger {
    override fun deleteTodo(id: Int?) {
        presenter.context.database.use {
            delete(ToDoTable.TODO_TABLE,
                    "${ToDoTable._ID} = ?",
                    arrayOf(id.toString()))
            presenter.onActionCompleted()
        }
    }

    override fun addToDo(todo: ToDo?) {
        presenter.context.database.use {
            insert(ToDoTable.TODO_TABLE,
                    ToDoTable.NOTE to todo?.note,
                    ToDoTable.IMPORTANCE to todo?.importance?.importance,
                    ToDoTable.PICTURE to todo?.picture,
                    ToDoTable.DATE to todo?.date)
        }

        todo?.scheduleAlarm(presenter.context)
        presenter.onActionCompleted()
    }

    override fun updateToDo(todo: ToDo?) {
        presenter.context.database.use {
            update(ToDoTable.TODO_TABLE,
                    ToDoTable.NOTE to todo?.note,
                    ToDoTable.IMPORTANCE to todo?.importance?.importance,
                    ToDoTable.PICTURE to todo?.picture,
                    ToDoTable.DATE to todo?.date)
                    .whereSimple("${ToDoTable._ID} = ?", todo?.id.toString())
                    .exec()
        }

        todo?.scheduleAlarm(presenter.context)
        presenter.onActionCompleted()
    }

}

class EditTodoPresenterImpl(override var context: Context, val view: EditTodoView) : EditTodoPresenter {
    private var interactor = EditTodoInteractorImpl(this)

    override fun deleteTodo(id: Int?) {
        interactor.deleteTodo(id)
    }

    override fun onActionCompleted() {
        view.onActionCompleted()
    }

    override fun addToDo(todo: ToDo?) {
        interactor.addToDo(todo)
    }

    override fun updateToDo(todo: ToDo?) {
        interactor.updateToDo(todo)
    }

}
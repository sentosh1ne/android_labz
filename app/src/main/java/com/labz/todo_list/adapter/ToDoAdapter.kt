package com.labz.todo_list.adapter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.labz.R
import com.labz.base.activity.BaseActivity
import com.labz.base.adapter.BaseRecyclerViewAdapter
import com.labz.db.ToDoTable
import com.labz.db.ToDoTable.buildToDoList
import com.labz.db.database
import com.labz.todo_list.models.ToDo
import com.labz.todo_list.models.importanceColor
import com.labz.todo_list.ui.update_todo.UpdateToDoFragment
import com.labz.utils.formattedDate
import kotlinx.android.synthetic.main.row_todo_note.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.db.select
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.imageURI

/**
 * Created by Stanislav Vylegzhanin on 11.10.17.
 */
class ToDoAdapter(context: Context?, objects: MutableList<ToDo>?) :
        BaseRecyclerViewAdapter<ToDo, ToDoAdapter.ViewHolder>(context, objects), AnkoLogger,
        UpdateToDoFragment.OnToDoAction {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val todo = getItem(position)
        todo?.let {
            holder?.bind(it, {
                val activity = context as BaseActivity
                val fragment = UpdateToDoFragment.newInstance(todo, true)
                fragment.actionCallback = this
                fragment.show(activity.supportFragmentManager, "updateDialog")
            })
        }
    }

    override fun onAction() {
        val list = context.database.use {
            select(ToDoTable.TODO_TABLE).exec {
                this.buildToDoList()
            }
        }
        items = list
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val converterView = LayoutInflater.from(parent?.context).inflate(R.layout.row_todo_note, parent, false)
        return ViewHolder(converterView)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(todo: ToDo, clickAction: () -> Unit) {
            with(todo) {
                itemView.txtTodo.text = todo.note
                itemView.imgImportance.imageResource = todo.importance.importanceColor()
                itemView.imgNote.imageURI = Uri.parse(todo.picture)
                itemView.txtDate.text = todo.date?.formattedDate()
                itemView.cardTodo.setOnClickListener({ clickAction() })
            }
        }
    }
}




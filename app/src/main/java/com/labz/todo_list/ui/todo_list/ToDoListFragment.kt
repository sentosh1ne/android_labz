package com.labz.todo_list.ui.todo_list

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.labz.R
import com.labz.base.fragment.BaseFragment
import com.labz.notification.TodoReceiver
import com.labz.todo_list.adapter.ToDoAdapter
import com.labz.todo_list.models.Importance
import com.labz.todo_list.models.ToDo
import com.labz.todo_list.ui.update_todo.UpdateToDoFragment
import com.labz.utils.afterTextChanged
import kotlinx.android.synthetic.main.fragment_todo_list.*
import kotlinx.android.synthetic.main.layout_filter_todo.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by Stanislav Vylegzhanin on 11.10.17.
 */
class ToDoListFragment : BaseFragment(), UpdateToDoFragment.OnToDoAction, TodoView, AnkoLogger {

    private lateinit var adapter: ToDoAdapter
    private lateinit var presenter: TodoPresenter
    private var importanceFilter: Importance? = null
    private var searchQuery: String = ""

    companion object {
        fun newInstance(): Fragment {
            val args = Bundle()
            val fragment = ToDoListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_todo_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initFab()
        presenter = TodoPresenterImpl(context, this)
        presenter.fetchTodoList()

        txtSearchNote.afterTextChanged {
            searchQuery = it
            presenter.fetchTodoList(searchQuery, importanceFilter)
        }

        rgImportance.onCheckedChange { _, checkedId ->
            importanceFilter = when (checkedId) {
                R.id.lowPriority -> Importance.LOW
                R.id.mediumPriority -> Importance.MEDIUM
                R.id.highPriority -> Importance.HIGH
                else -> null
            }
            presenter.fetchTodoList(searchQuery, importanceFilter)
        }

        btnClear.onClick {
            rgImportance.clearCheck()
            txtSearchNote.setText("")
        }
    }

    override fun handleArguments(args: Bundle?) {
        super.handleArguments(args)
        if (activity.intent.extras != null) {
            val todo = activity.intent.extras.getParcelable<ToDo>(TodoReceiver.NOTIFICATION_TODO_EXTRA)
            val fragment = UpdateToDoFragment.newInstance(todo, true)
            fragment.actionCallback = this
            fragment.show(activity.supportFragmentManager, "updateDialog")
        }

    }

    private fun initFab() {
        rvTodolist.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fbAddNote.show()
                }
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && fbAddNote.isShown) {
                    fbAddNote.hide()
                }
            }
        })

        fbAddNote.onClick {
            val fragment = UpdateToDoFragment.newInstance(null, false)
            fragment.actionCallback = this@ToDoListFragment
            fragment.show(activity.supportFragmentManager, "update_dialog")
        }

    }

    override fun onAction() {
        presenter.fetchTodoList()
    }

    private var handler: Handler? = null

    override fun onDataFetched(list: ArrayList<ToDo>?) {
        pbList.visibility = View.VISIBLE
        handler = Handler()
        handler?.postDelayed({
            adapter.items = list
            pbList.visibility = View.GONE
        }, 1000)
    }

    private fun initList() {
        adapter = ToDoAdapter(context, ArrayList())
        rvTodolist.adapter = adapter
        rvTodolist.layoutManager = LinearLayoutManager(context)
    }

    override fun onDetach() {
        super.onDetach()
    }
}


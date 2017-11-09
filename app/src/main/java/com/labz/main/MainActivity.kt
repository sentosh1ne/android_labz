package com.labz.main

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.view.Menu
import android.view.MenuItem
import com.labz.R
import com.labz.base.activity.BaseActivity
import com.labz.calc.CalculatorFragment
import com.labz.color_picker.ColorPickerFragment
import com.labz.notification.TodoReceiver
import com.labz.player.ui.PlayerFragment
import com.labz.preferences.SettingsActivity
import com.labz.todo_list.models.ToDo
import com.labz.todo_list.ui.todo_list.ToDoListFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity

class MainActivity : BaseActivity(), AnkoLogger {

    private var todo: ToDo? = null

    private val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                replaceActivityFragment(CalculatorFragment.newInstance(), true, true)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_color_picker -> {
                replaceActivityFragment(ColorPickerFragment.newInstance(), true, true)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                replaceActivityFragment(ToDoListFragment.newInstance(), true, true)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_player -> {
                replaceActivityFragment(PlayerFragment.newInstance(), true, true)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigation.selectedItemId =  R.id.navigation_home
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        setSupportActionBar(my_toolbar)
        if (todo != null) {
            replaceActivityFragment(ToDoListFragment.newInstance(), false, true)
        } else {
            replaceActivityFragment(ColorPickerFragment.newInstance(), false, true)
        }
    }

    override fun handleExtras(extras: Bundle?) {
        super.handleExtras(extras)
        if (extras != null) {
            todo = extras.getParcelable(TodoReceiver.NOTIFICATION_TODO_EXTRA)
            info(todo)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.actionbar_settings -> {
                startActivity<SettingsActivity>()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun getToolbarNavigationIcon(): Int {
        return R.drawable.ic_back
    }
}

package com.labz.todo_list.ui.update_todo

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.*
import com.labz.R
import com.labz.todo_list.models.Importance
import com.labz.todo_list.models.ToDo
import com.labz.utils.formattedDate
import com.labz.utils.screenHeight
import com.labz.utils.screenWidth
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.fragment_update_todo.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.imageURI
import org.jetbrains.anko.info
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onTouch
import org.jetbrains.anko.support.v4.toast
import java.io.FileNotFoundException
import java.util.*


/**
 * Created by Stanislav Vylegzhanin on 11.10.17.
 */
class UpdateToDoFragment : DialogFragment(), DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener, AnkoLogger, EditTodoView {

    var actionCallback: OnToDoAction? = null

    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private var hour: Int = 0
    private var minute: Int = 0

    private var todo: ToDo? = null
    private var toUpdate: Boolean = false

    private lateinit var presenter: EditTodoPresenter

    interface OnToDoAction {
        fun onAction()
    }

    companion object {
        val SELECT_PICTURE = 12345
        val TODO_ARG = "todo_arg"
        val TOUPDATE_ARG = "to_update_arg"

        fun newInstance(todo: ToDo?, toUpdate: Boolean): UpdateToDoFragment {
            val args = Bundle()
            args.putParcelable(TODO_ARG, todo)
            args.putBoolean(TOUPDATE_ARG, toUpdate)
            val fragment = UpdateToDoFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private fun handleArguments() {
        todo = arguments.getParcelable(TODO_ARG)
        if (todo == null) {
            todo = ToDo(id = 0, note = "", picture = "", date = 0, importance = Importance.MEDIUM)
        }
        toUpdate = arguments.getBoolean(TOUPDATE_ARG)
    }

    override fun onDateSet(view: com.wdullaer.materialdatetimepicker.date.DatePickerDialog?,
                           chosenYear: Int, monthOfYear: Int, dayOfMonth: Int) {
        this.year = chosenYear
        this.month = monthOfYear
        this.day = dayOfMonth
        showTimePicker()
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minuteOfHour: Int, second: Int) {
        hour = hourOfDay
        minute = minuteOfHour
        changeDate()
    }

    private fun changeDate() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        val time = calendar.time.time
        val formattedDate = time.formattedDate()
        txtDate.setText(formattedDate)
        todo?.date = time
    }

    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog.newInstance(this, 0, 0, true)
        timePickerDialog.show(fragmentManager, "timepicker")
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog.newInstance(this)
        datePickerDialog.showYearPickerFirst(false)
        datePickerDialog.show(fragmentManager, "datepicker")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_update_todo, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleArguments()
        initFields()

        presenter = EditTodoPresenterImpl(context, this)

        if (toUpdate) {
            btnDelete.visibility = View.VISIBLE
            btnDelete.onClick {
                presenter.deleteTodo(todo?.id)
            }
        }

        imgAddNote.onClick {
            browseGallery()
        }

        txtDate.onTouch { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                showDatePicker()
            }
        }

        rgImportance.onCheckedChange { _, checkedId ->
            when (checkedId) {
                lowPriority.id -> todo?.importance = Importance.LOW
                mediumPriority.id -> todo?.importance = Importance.MEDIUM
                highPriority.id -> todo?.importance = Importance.HIGH
            }
        }

        btnSave.onClick {
            todo?.note = txtNote.text.toString()

            if (toUpdate) {
                presenter.updateToDo(todo)
            } else {
                presenter.addToDo(todo)
            }
        }

    }

    override fun onActionCompleted() {
        dismiss()
        if (actionCallback != null) {
            actionCallback?.onAction()
        }
    }

    private fun initFields() {
        if (todo != null) {
            
            if (todo?.date == 0L) {
                todo?.date = System.currentTimeMillis()
            }

            txtDate.setText(todo?.date?.formattedDate())
            txtNote.setText(todo?.note)

            if (todo?.picture.isNullOrEmpty()) {
                imgAddNote.imageResource = R.mipmap.ic_launcher
            } else {
                imgAddNote.imageURI = Uri.parse(todo?.picture)
            }

            when (todo?.importance) {
                Importance.LOW -> lowPriority.isChecked = true
                Importance.MEDIUM -> mediumPriority.isChecked = true
                Importance.HIGH -> highPriority.isChecked = true
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(activity)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_update_todo)
        initDialogLayout(dialog)
        return dialog
    }

    private fun initDialogLayout(dialog: Dialog) {
        val window = dialog.window
        if (window != null) {
            with(window) {
                var screenWidth = activity.screenWidth()
                var screenHeight = activity.screenHeight()
                val edgeOffset = 64

                info(resources.configuration.orientation)
                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    setLayout(screenHeight - edgeOffset * 2, (screenWidth / 1.85).toInt())
                } else {
                    setLayout(screenWidth - edgeOffset * 2, (screenHeight / 1.5).toInt())
                }
                addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val lp = window.attributes
                lp.gravity = Gravity.CENTER
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        info("Result")
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                initImagePath(data)
            }
        }
    }

    private fun browseGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(activity.packageManager) != null) {
            startActivityForResult(intent, UpdateToDoFragment.SELECT_PICTURE)
        }
    }


    private fun initImagePath(data: Intent?) {
        try {
            val imageUri = data?.data
            val imageStream = activity.contentResolver.openInputStream(imageUri)
            val selectedImage = BitmapFactory.decodeStream(imageStream)
            info(imageUri)
            todo?.picture = imageUri.toString()
            imgAddNote.setImageBitmap(selectedImage)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            toast("Picture was not found")
        }
    }
}




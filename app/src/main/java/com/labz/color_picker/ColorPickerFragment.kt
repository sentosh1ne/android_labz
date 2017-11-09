package com.labz.color_picker

import android.graphics.Color
import android.os.Bundle
import android.support.annotation.ArrayRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.labz.R
import com.labz.base.fragment.BaseFragment
import com.marcinmoskala.arcseekbar.ArcSeekBar
import com.marcinmoskala.arcseekbar.ProgressListener
import kotlinx.android.synthetic.main.fragment_color_picker.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.backgroundColor

/**
 * Created by Stanislav Vylegzhanin on 09.10.17.
 */
class ColorPickerFragment : BaseFragment(), AnkoLogger {

    private var color = 0
    private val progressListener = ProgressListener {
        color = Color.rgb(redSeekBar.progress, greenSeekBar.progress, blueSeekBar.progress)
        palette.backgroundColor = color
        txtRed.text = redSeekBar.progress.toString()
        txtGreen.text = greenSeekBar.progress.toString()
        txtBlue.text = blueSeekBar.progress.toString()
    }

    companion object {
        fun newInstance(): Fragment {
            val args = Bundle()
            val fragment = ColorPickerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        redSeekBar.onProgressChangedListener = progressListener
        redSeekBar.applyGradient(R.array.redGradient)

        blueSeekBar.onProgressChangedListener = progressListener
        blueSeekBar.applyGradient(R.array.blueGradient)

        greenSeekBar.onProgressChangedListener = progressListener
        greenSeekBar.applyGradient(R.array.greenGradient)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_color_picker, container, false)
    }
}

fun ArcSeekBar.applyGradient(@ArrayRes array: Int) {
    val gradient = resources.getIntArray(array)
    this.setProgressGradient(*gradient)
}

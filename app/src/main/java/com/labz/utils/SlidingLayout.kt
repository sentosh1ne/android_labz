package com.labz.utils

import android.view.View
import com.sothree.slidinguppanel.SlidingUpPanelLayout

/**
 * Created by Stanislav Vylegzhanin on 19.10.17.
 */
fun SlidingUpPanelLayout.onStateChange(onChange: (state: SlidingUpPanelLayout.PanelState?) -> Unit) {
    this.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
        override fun onPanelSlide(panel: View?, slideOffset: Float) {

        }

        override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
            onChange(newState)
        }
    })
}
package com.example.myapplication

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.absoluteValue
import kotlin.math.sign

class CustomLinearSnapHelper(private val context: Context?) : LinearSnapHelper() {
    private val millisecondsPerInch = 100
    private val maxScrollOnFlingDuration = 250 // ms
    private val maxVelocity = 5000

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager?,
        velocityX: Int,
        velocityY: Int
    ): Int {
        val velocity = if (velocityX.absoluteValue > maxVelocity) maxVelocity * velocityX.sign else velocityX
        return super.findTargetSnapPosition(layoutManager, velocity, velocityY)
    }

    override fun createScroller(layoutManager: RecyclerView.LayoutManager?): RecyclerView.SmoothScroller? {
        if (layoutManager !is RecyclerView.SmoothScroller.ScrollVectorProvider) return null
        return object : LinearSmoothScroller(context) {
            override fun onTargetFound(targetView: View, state: RecyclerView.State, action: Action) {
                val snapDistances = calculateDistanceToFinalSnap(layoutManager, targetView) ?: return
                val dx = snapDistances[0]
                val dy = snapDistances[1]
                val time = calculateTimeForDeceleration(Math.max(Math.abs(dx), Math.abs(dy)))
                if (time > 0) {
                    action.update(dx, dy, time, mDecelerateInterpolator)
                }
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                return displayMetrics?.densityDpi?.let { density ->
                    millisecondsPerInch.toFloat() / density
                } ?: super.calculateSpeedPerPixel(displayMetrics)
            }

            override fun calculateTimeForScrolling(dx: Int): Int =
                Math.min(maxScrollOnFlingDuration, super.calculateTimeForScrolling(dx))

            override fun setTargetPosition(targetPosition: Int) {
                super.setTargetPosition(when (targetPosition) {
                    0 -> 1
                    layoutManager.itemCount - 1 -> targetPosition - 1
                    else -> targetPosition
                })
            }
        }
    }
}

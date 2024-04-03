package cn.ybs.video.slide

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout

class SlideBehavior(context: Context, attrs: AttributeSet) : CoordinatorLayout.Behavior<View>(context, attrs) {

    private var isAnimatingOut = false

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency is androidx.core.widget.NestedScrollView
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return true
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        if (dy > 0 && !isAnimatingOut && child.visibility == View.VISIBLE) {
            animateOut(child)
        } else if (dy < 0 && child.visibility != View.VISIBLE) {
            animateIn(child)
        }

        // Moving MatchingView along with the scroll
        val params = coordinatorLayout.getChildAt(1).layoutParams as CoordinatorLayout.LayoutParams
        val newTopMargin = params.topMargin - dy
        params.topMargin = newTopMargin.coerceAtLeast(0) // Ensures view doesn't move above top
        coordinatorLayout.getChildAt(1).layoutParams = params
    }

    private fun animateOut(view: View) {
        view.animate()
            .translationY(-view.height.toFloat())
            .setInterpolator(AccelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.INVISIBLE
                    isAnimatingOut = false
                }
            })
            .start()
        isAnimatingOut = true
    }

    private fun animateIn(view: View) {
        view.visibility = View.VISIBLE
        view.animate()
            .translationY(0f)
            .setInterpolator(DecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    isAnimatingOut = false
                }
            })
            .start()
    }
}


package yamin.elmakis.bot.util

import android.content.res.Resources
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class KeyboardVisibilityHandler(
    private val view: View,
    private val onKeyboardVisibilityChanged: (Boolean) -> (Unit)
) : ViewTreeObserver.OnDrawListener,
    LifecycleObserver {

    companion object {
        /**
         * Approximate height of keyboard (150 dp)
         */
        private val KEYBOARD_THRESHOLD = 150 * Resources.getSystem().displayMetrics.density
    }

    private var isKeyboardShown = false
    private var lastKeyboardChange = false
    private val visibleFrame = Rect()
    private var keyboardHeight: Int = 0
    private var visibilityChanged: Boolean = false

    override fun onDraw() {
        view.rootView.getWindowVisibleDisplayFrame(visibleFrame)
        keyboardHeight = view.rootView.measuredHeight - (visibleFrame.bottom - visibleFrame.top)
        isKeyboardShown = keyboardHeight > KEYBOARD_THRESHOLD
        visibilityChanged = lastKeyboardChange xor isKeyboardShown
        if (!visibilityChanged) {
            return
        }
        this.lastKeyboardChange = isKeyboardShown
        onKeyboardVisibilityChanged.invoke(isKeyboardShown)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() {
        view.viewTreeObserver.addOnDrawListener(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun onPause() {
        view.viewTreeObserver.removeOnDrawListener(this)
    }
}
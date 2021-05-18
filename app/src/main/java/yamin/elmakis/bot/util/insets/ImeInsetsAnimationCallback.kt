package yamin.elmakis.bot.util.insets

import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsAnimation

class ImeInsetsAnimationCallback(
    dispatchMode: Int,
    private val view: View,
    private val onKeyboardVisibilityChanged: (Boolean) -> (Unit)
) : WindowInsetsAnimation.Callback(dispatchMode) {

    override fun onProgress(
        insets: WindowInsets,
        runningAnimations: List<WindowInsetsAnimation>
    ): WindowInsets {
        // no-op and return the insets
        return insets
    }

    override fun onEnd(animation: WindowInsetsAnimation) {
        if (animation.typeMask and WindowInsets.Type.ime() != 0) {
            val imeVisible = view.rootWindowInsets.isVisible(WindowInsets.Type.ime())
            onKeyboardVisibilityChanged.invoke(imeVisible)
        }
    }
}

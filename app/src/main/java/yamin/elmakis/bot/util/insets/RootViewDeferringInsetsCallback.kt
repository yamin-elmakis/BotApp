package yamin.elmakis.bot.util.insets

import android.os.Build
import android.view.View
import android.view.WindowInsets
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.Q)
class RootViewDeferringInsetsCallback(
    val persistentInsetTypes: Int,
    val deferredInsetTypes: Int
) : View.OnApplyWindowInsetsListener {
    init {
        require(persistentInsetTypes and deferredInsetTypes == 0) {
            "persistentInsetTypes and deferredInsetTypes can not contain any of " +
                    " same WindowInsets.Type values"
        }
    }

    private var view: View? = null
    private var lastWindowInsets: WindowInsets? = null

    private var deferredInsets = false

    override fun onApplyWindowInsets(v: View, windowInsets: WindowInsets): WindowInsets {
        // Store the view and insets for us in onEnd() below
        view = v
        lastWindowInsets = windowInsets

        val types = when {
            // When the deferred flag is enabled, we only use the systemBars() insets
            deferredInsets -> persistentInsetTypes
            // Otherwise we handle the combination of the the systemBars() and ime() insets
            else -> persistentInsetTypes or deferredInsetTypes
        }

        // Finally we apply the resolved insets by setting them as padding
        val typeInsets = windowInsets.getInsets(types)
        v.setPadding(
            typeInsets.left,
            typeInsets.top + getStatusBarHeight(),
            typeInsets.right,
            typeInsets.bottom
        )

        // We return the new WindowInsets.CONSUMED to stop the insets being dispatched any
        // further into the view hierarchy. This replaces the deprecated
        // WindowInsets.consumeSystemWindowInsets() and related functions.
        return WindowInsets.CONSUMED
    }

    private fun getStatusBarHeight(): Int {
        view?.resources?.let {
            val resourceId = it.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                return it.getDimensionPixelSize(resourceId)
            }
        }
        return 0
    }

}

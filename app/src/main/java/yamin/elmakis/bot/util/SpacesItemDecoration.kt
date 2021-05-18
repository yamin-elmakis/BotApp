package yamin.elmakis.bot.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacesItemDecoration( // the sizes in PX
    private val left: Int, private val top: Int, private val right: Int, private val bottom: Int
) : RecyclerView.ItemDecoration() {
    private var firstTop: Int = 0
    private var firstStart: Int = 0
    private var lastBottom: Int = 0
    private var lastEnd: Int = 0

    constructor(space: Int) : this(space, space, space, space)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        var rtl = false
        if (view.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            rtl = true
        }

        val position = parent.getChildAdapterPosition(view)
        if (firstTop > 0 && position == 0) {
            outRect.top = top + firstTop
        } else
            outRect.top = top

        if (lastBottom > 0 && position == (parent.adapter?.itemCount ?: 0) - 1) {
            outRect.bottom = bottom + lastBottom
        } else
            outRect.bottom = bottom

        outRect.left = left
        outRect.right = right
        if (firstStart > 0 && position == 0) {
            if (rtl)
                outRect.right += firstStart
            else
                outRect.left += firstStart
        }

        if (lastEnd > 0 && position == (parent.adapter?.itemCount ?: 0) - 1) {
            if (rtl)
                outRect.left += lastEnd
            else
                outRect.right += lastEnd
        }
    }

    fun setFirstTop(firstTop: Int) {
        this.firstTop = firstTop
    }

    fun setFirstStart(firstStart: Int) {
        this.firstStart = firstStart
    }

    fun setLastBottom(lastBottom: Int) {
        this.lastBottom = lastBottom
    }

    fun setLastEnd(lastEnd: Int) {
        this.lastEnd = lastEnd
    }
}
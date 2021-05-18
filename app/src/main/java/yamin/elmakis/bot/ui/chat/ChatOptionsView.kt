package yamin.elmakis.bot.ui.chat

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.view.setMargins
import yamin.elmakis.bot.R
import yamin.elmakis.bot.util.setOnDelayClickListener
import yamin.elmakis.domain.entity.SelectionOption

class ChatOptionsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val buttonsMargin = resources.getDimension(R.dimen.chat_options_margin).toInt()

    init {
        orientation = HORIZONTAL
    }

    fun setOptions(options: List<SelectionOption>?, onClickListener: (SelectionOption) -> (Unit)) {
        if (options.isNullOrEmpty()) {
            return
        }
        weightSum = options.size.toFloat()
        removeAllViews()
        options.forEach {
            addOptionsButton(it, onClickListener)
        }
    }

    private fun addOptionsButton(
        option: SelectionOption,
        onClickListener: (SelectionOption) -> (Unit)
    ) {
        val button = Button(context)
        button.text = option.name
        button.setBackgroundResource(R.drawable.select_button_ripple)
        button.setOnDelayClickListener {
            onClickListener.invoke(option)
        }
        val param = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.MATCH_PARENT,
            1.0f
        )
        param.setMargins(buttonsMargin)
        addView(button, param)
    }
}
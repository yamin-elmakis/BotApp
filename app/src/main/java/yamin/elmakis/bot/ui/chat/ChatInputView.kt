package yamin.elmakis.bot.ui.chat

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.text.Spanned
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View.OnFocusChangeListener
import androidx.constraintlayout.widget.ConstraintLayout
import yamin.elmakis.bot.databinding.ChatInputViewBinding
import yamin.elmakis.bot.util.setOnDelayClickListener

class ChatInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = ChatInputViewBinding.inflate(LayoutInflater.from(context), this)

    companion object {
        private val phoneKeyListener = DigitsKeyListener.getInstance("0123456789")
        private val textInputFilter =
            InputFilter { charSequence: CharSequence, start: Int, end: Int, dest: Spanned, dStart: Int, dEnd: Int ->
                return@InputFilter charSequence.removeSuffix(".").replace(Regex("[^a-z]"), "")
            }
        private val inputFilterList = arrayOf(textInputFilter)
    }

    fun setChatInputOnClickListener(onClickListener: (String) -> (Unit)) {
        binding.chatInputSend.setOnDelayClickListener {
            onClickListener.invoke(binding.chatInputEditText.text.toString())
            binding.chatInputEditText.setText("")
        }
    }

    fun setKeyboardType(type: KeyboardType) {
        val keyboardType = when (type) {
            KeyboardType.TEXT -> InputType.TYPE_CLASS_TEXT
            KeyboardType.PHONE -> InputType.TYPE_CLASS_PHONE
        }
        if (binding.chatInputEditText.inputType == keyboardType) {
            return
        }
        binding.chatInputEditText.inputType = keyboardType
        when (type) {
            KeyboardType.TEXT -> {
                binding.chatInputEditText.filters = inputFilterList
            }
            KeyboardType.PHONE -> {
                binding.chatInputEditText.keyListener = phoneKeyListener
                binding.chatInputEditText.filters = arrayOf<InputFilter>()
            }
        }
    }

    enum class KeyboardType {
        TEXT, PHONE
    }
}
package yamin.elmakis.bot.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import yamin.elmakis.bot.databinding.ChatBotItemBinding
import yamin.elmakis.bot.databinding.ChatLineBinding
import yamin.elmakis.bot.databinding.ChatMyItemBinding
import yamin.elmakis.domain.entity.ChatMessage
import yamin.elmakis.domain.entity.Sender


class ChatAdapter : ListAdapter<ChatMessage, ChatAdapter.BubbleVH>(DIFF_CALLBACK) {

    private var lastAddedPosition = -1

    companion object {
        const val BUBBLE_ANIMATION_DURATION: Long = 300
        const val LINE_ANIMATION_DURATION: Long = 500

        const val TYPE_MY = 1
        const val TYPE_BOT = 2
        const val TYPE_LINE = 3

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ChatMessage>() {
            override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
                return newItem == oldItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val curItem = getItem(position)) {
            is ChatMessage.TextChatMessage -> {
                when (curItem.sender) {
                    Sender.ME -> TYPE_MY
                    Sender.BOT -> TYPE_BOT
                }
            }
            is ChatMessage.SeparatorLine -> TYPE_LINE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BubbleVH {
        return when (viewType) {
            TYPE_MY -> {
                val myBinding =
                    ChatMyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MyBubbleVH(myBinding)
            }
            TYPE_BOT -> {
                val botBinding =
                    ChatBotItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                BotBubbleVH(botBinding)
            }
            TYPE_LINE -> {
                val lineBinding =
                    ChatLineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LineVH(lineBinding)
            }
            else -> throw IllegalArgumentException("unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: BubbleVH, position: Int) {
        holder.bind(getItem(position))
        holder.setAnimation(position)
    }

    abstract inner class BubbleVH(private var binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        abstract fun bind(textChatMessage: ChatMessage)
        abstract fun getAnimation(): Animation
        abstract fun getAnimationDuration(): Long

        fun setAnimation(position: Int) {
            if (position > lastAddedPosition) {
                val anim = getAnimation()
                anim.duration = getAnimationDuration()
                binding.root.startAnimation(anim)

                lastAddedPosition = position
            }
        }
    }

    inner class MyBubbleVH(private var binding: ChatMyItemBinding) : BubbleVH(binding) {
        override fun bind(textChatMessage: ChatMessage) {
            binding.chatMyItemText.text = (textChatMessage as? ChatMessage.TextChatMessage)?.text
        }

        override fun getAnimation() = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, -0.5f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        )

        override fun getAnimationDuration(): Long = BUBBLE_ANIMATION_DURATION
    }

    inner class BotBubbleVH(private var binding: ChatBotItemBinding) : BubbleVH(binding) {
        override fun bind(textChatMessage: ChatMessage) {
            binding.chatBotItemText.text = (textChatMessage as? ChatMessage.TextChatMessage)?.text
        }

        override fun getAnimation() = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f
        )

        override fun getAnimationDuration(): Long = BUBBLE_ANIMATION_DURATION
    }

    inner class LineVH(binding: ChatLineBinding) : BubbleVH(binding) {
        override fun bind(textChatMessage: ChatMessage) {}

        override fun getAnimation(): Animation = AlphaAnimation(0.0f, 1.0f)
        override fun getAnimationDuration(): Long = LINE_ANIMATION_DURATION
    }
}
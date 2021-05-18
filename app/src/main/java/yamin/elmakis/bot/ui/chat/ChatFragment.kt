package yamin.elmakis.bot.ui.chat

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.LinearLayoutManager
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import yamin.elmakis.bot.R
import yamin.elmakis.bot.base.BaseFragment
import yamin.elmakis.bot.databinding.ChatFragmentBinding
import yamin.elmakis.bot.di.fragmentDependencies
import yamin.elmakis.bot.util.KeyboardVisibilityHandler
import yamin.elmakis.bot.util.SpacesItemDecoration
import yamin.elmakis.bot.util.hideSoftKeyboard
import yamin.elmakis.bot.util.insets.ImeInsetsAnimationCallback
import yamin.elmakis.bot.util.insets.RootViewDeferringInsetsCallback
import yamin.elmakis.domain.entity.ChatMessage
import yamin.elmakis.domain.entity.InputType
import yamin.elmakis.domain.entity.SelectionOption

class ChatFragment : BaseFragment() {

    private lateinit var binding: ChatFragmentBinding
    private lateinit var chatAdapter: ChatAdapter
    private val viewModel: ChatContract.ChatViewModel by instance()
    private var admittedState: ChatContract.State? = null

    override fun getDependenciesModule(): Kodein.Module {
        return fragmentDependencies(this@ChatFragment)
    }

    companion object {
        fun newInstance() = ChatFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChatFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        setUpChatInputView()
        setUpKeyboardHandler()

        viewModel.getStateLiveData().observe(viewLifecycleOwner) {
            if (it.messages != admittedState?.messages && it.messages.isNotEmpty()) {
                handleMessages(it.messages)
            }
            if (it.inputType != admittedState?.inputType) {
                handleInputType(it.inputType)
            }
            admittedState = it
        }

        viewModel.getEventsLiveData().observe(viewLifecycleOwner, ::handleEvents)
    }

    private fun setUpKeyboardHandler() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            val deferringInsetsListener = RootViewDeferringInsetsCallback(
                persistentInsetTypes = WindowInsets.Type.systemBars(),
                deferredInsetTypes = WindowInsets.Type.ime()
            )
            binding.root.setOnApplyWindowInsetsListener(deferringInsetsListener)

            val callback = ImeInsetsAnimationCallback(
                WindowInsetsAnimation.Callback.DISPATCH_MODE_STOP,
                binding.root, ::onKeyboardVisibilityChanged
            )
            binding.main.setWindowInsetsAnimationCallback(callback)
        } else {
            viewLifecycleOwner.lifecycle.addObserver(
                KeyboardVisibilityHandler(
                    binding.root, ::onKeyboardVisibilityChanged
                )
            )
        }
    }

    private fun onKeyboardVisibilityChanged(isVisible: Boolean) {
        if (isVisible) {
            smoothScrollToLast()
        }
    }

    private fun handleEvents(event: ChatContract.Event) {
        when (event) {
            ChatContract.Event.HIDE_KEYBOARD -> binding.main.hideSoftKeyboard()
            ChatContract.Event.ERROR -> Toast.makeText(
                requireContext(),
                R.string.error,
                Toast.LENGTH_SHORT
            ).show()
            ChatContract.Event.EMPTY_TEXT -> Toast.makeText(
                requireContext(),
                R.string.empty_user_input,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleMessages(messages: List<ChatMessage>) {
        chatAdapter.submitList(messages) {
            smoothScrollToLast()
        }
    }

    private fun smoothScrollToLast() {
        binding.chatMessages.doOnLayout {
            binding.chatMessages.adapter?.itemCount?.minus(1)?.coerceAtLeast(0)?.let {
                binding.chatMessages.smoothScrollToPosition(it)
            }
        }
    }

    private fun handleInputType(inputType: InputType) {
        when (inputType) {
            InputType.BotTyping -> {
                binding.main.transitionToState(R.id.chat_with_loading)
            }
            InputType.Number -> {
                binding.main.transitionToState(R.id.chat_with_input)
                binding.chatInputView.setKeyboardType(ChatInputView.KeyboardType.PHONE)
            }
            is InputType.Selection -> {
                binding.main.transitionToState(R.id.chat_with_selection)
                binding.chatOptionsView.setOptions(inputType.options, ::onOptionSelected)
            }
            InputType.Text -> {
                binding.main.transitionToState(R.id.chat_with_input)
                binding.chatInputView.setKeyboardType(ChatInputView.KeyboardType.TEXT)
            }
            InputType.None -> {
                binding.main.transitionToState(R.id.only_chat)
            }
        }
    }

    private fun onOptionSelected(option: SelectionOption) {
        viewModel.sendOption(option)
    }

    private fun setUpChatInputView() {
        binding.chatInputView.setChatInputOnClickListener {
            viewModel.sendText(it)
        }
    }

    private fun setUpRecyclerView() {
        chatAdapter = ChatAdapter()
        binding.chatMessages.adapter = chatAdapter
        binding.chatMessages.layoutManager = LinearLayoutManager(requireContext())
        val spacing = resources.getDimension(R.dimen.chat_bubble_spacing).toInt()
        val firstSpacing = resources.getDimension(R.dimen.chat_bubble_first_spacing).toInt()
        val spacesItemDecoration = SpacesItemDecoration(spacing)
        spacesItemDecoration.setFirstTop(firstSpacing)
        binding.chatMessages.addItemDecoration(spacesItemDecoration)
    }

}
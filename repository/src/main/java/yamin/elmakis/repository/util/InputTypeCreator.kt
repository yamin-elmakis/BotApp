package yamin.elmakis.repository.util

import android.util.Log
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import yamin.elmakis.domain.entity.InputType
import yamin.elmakis.domain.entity.SelectionOption
import java.lang.reflect.Type

class InputTypeCreator : JsonSerializer<InputType>, JsonDeserializer<InputType> {

    companion object {
        const val ID_KEY = "key"
        const val OPTIONS_KEY = "options"
        const val BOT_TYPING = 1
        const val TEXT = 2
        const val NUMBER = 3
        const val SELECTION = 4
        const val NONE = 5
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): InputType {
        json as JsonObject
        return when (json.get(ID_KEY).asInt) {
            BOT_TYPING -> InputType.BotTyping
            TEXT -> InputType.Text
            NUMBER -> InputType.Number
            NONE -> InputType.None
            SELECTION -> {
                val selectionOptionsStr = json.get(OPTIONS_KEY)
                val selectionOptions: ArrayList<SelectionOption> = arrayListOf()
                try {
                    context?.let {
                        val itemType = object : TypeToken<List<String>>() {}.type
                        val res: List<String> = it.deserialize(selectionOptionsStr, itemType)
                        selectionOptions.addAll(res.map { optionStr ->
                            SelectionOption.valueOf(optionStr)
                        })
                    }
                } catch (e: JsonParseException) {
                    Log.e("YAMIN", "InputTypeCreator:deserialize: can't parse $selectionOptionsStr")
                }
                InputType.Selection(selectionOptions)
            }
            else -> InputType.BotTyping
        }
    }

    override fun serialize(
        src: InputType?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val jsonObject = JsonObject()
        try {
            val id = when (src) {
                InputType.BotTyping -> BOT_TYPING
                InputType.Number -> NUMBER
                InputType.None -> NONE
                is InputType.Selection -> {
                    val options = context?.serialize(src.options)
                    jsonObject.add(OPTIONS_KEY, options)
                    SELECTION
                }
                InputType.Text -> TEXT
                null -> -1
            }
            jsonObject.addProperty(ID_KEY, id)
        } catch (e: Exception) {
            Log.e("YAMIN", "InputTypeCreator:serialize:${e.message}", e)
        }
        return jsonObject
    }
}
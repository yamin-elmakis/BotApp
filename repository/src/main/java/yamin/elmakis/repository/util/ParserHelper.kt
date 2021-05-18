package yamin.elmakis.repository.util

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.lang.reflect.Type

interface ParserHelper {
    fun toJson(obj: Any): String

    @Throws(JsonSyntaxException::class)
    fun <T> fromJson(json: String, typeOfT: Type): T?
}

class ParserHelperImpl(private val gson: Gson) : ParserHelper {

    override fun toJson(obj: Any): String {
        return gson.toJson(obj)
    }

    override fun <T> fromJson(json: String, typeOfT: Type): T? {
        return gson.fromJson(json, typeOfT)
    }
}
package yamin.elmakis.repository.data.response

import yamin.elmakis.domain.entity.InputType

data class BotResponse(val messages: List<String>, val input: InputType?)


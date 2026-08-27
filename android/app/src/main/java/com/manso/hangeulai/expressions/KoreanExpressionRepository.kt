package com.manso.hangeulai.expressions

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class LocalizedExpressionText(val ko: String, val en: String, val ja: String) {
    fun forLanguage(language: String): String = when (language) {
        "en" -> en
        "ja" -> ja
        else -> ko
    }
}

data class ExpressionUsage(val friend: String, val coworker: String, val boss: String)

data class KoreanExpression(
    val id: String,
    val expression: String,
    val meaning: LocalizedExpressionText,
    val nuance: LocalizedExpressionText,
    val usage: ExpressionUsage,
    val example: LocalizedExpressionText,
    val category: String,
    val difficulty: Int
)

object KoreanExpressionRepository {
    fun load(context: Context): List<KoreanExpression> = runCatching {
        val json = context.assets.open("korean_expressions.json").bufferedReader().use { it.readText() }
        val array = JSONArray(json)
        List(array.length()) { index -> array.getJSONObject(index).toExpression() }
    }.getOrDefault(emptyList())

    private fun JSONObject.localized(key: String): LocalizedExpressionText {
        val value = getJSONObject(key)
        return LocalizedExpressionText(value.getString("ko"), value.getString("en"), value.getString("ja"))
    }

    private fun JSONObject.toExpression(): KoreanExpression {
        val usage = getJSONObject("usage")
        return KoreanExpression(
            id = getString("id"),
            expression = getString("expression"),
            meaning = localized("meaning"),
            nuance = localized("nuance"),
            usage = ExpressionUsage(usage.getString("friend"), usage.getString("coworker"), usage.getString("boss")),
            example = localized("example"),
            category = getString("category"),
            difficulty = getInt("difficulty")
        )
    }
}

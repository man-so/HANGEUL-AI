package com.manso.hangeulai.expressions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val WarmBackground = Color(0xFFF7F5F0)
private val Accent = Color(0xFFE84C3D)
private val Muted = Color(0xFF6E6B65)

@Composable
fun KoreanExpressionScreen(expressions: List<KoreanExpression>, language: String = "ko") {
    var index by remember { mutableStateOf(0) }
    var detail by remember { mutableStateOf(false) }
    if (expressions.isEmpty()) return
    val item = expressions[index % expressions.size]

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("오늘의 진짜 한국어", color = Accent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(Modifier.height(10.dp))
        Text(item.expression, fontSize = 38.sp, lineHeight = 46.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(14.dp))
        Text(item.meaning.forLanguage(language), color = Muted, fontSize = 17.sp, lineHeight = 25.sp)
        Spacer(Modifier.height(18.dp))
        UsageRow(item.usage)
        Spacer(Modifier.height(18.dp))
        Button(onClick = { detail = !detail }, modifier = Modifier.fillMaxWidth()) {
            Text(if (detail) "간단히 보기" else "자세히 보기")
        }
        if (detail) {
            Spacer(Modifier.height(18.dp))
            DetailCard("실제 뉘앙스", item.nuance.forLanguage(language))
            Spacer(Modifier.height(10.dp))
            DetailCard("자연스러운 예문", item.example.forLanguage(language))
            Spacer(Modifier.height(10.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
                Row(Modifier.fillMaxWidth().padding(18.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("카테고리  ${item.category}", fontWeight = FontWeight.Bold)
                    Text("난이도  ${"★".repeat(item.difficulty)}${"☆".repeat(5 - item.difficulty)}")
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = { index = (index + 1) % expressions.size; detail = false }, modifier = Modifier.fillMaxWidth()) {
            Text("다른 표현 보기 ↻")
        }
    }
}

@Composable
private fun UsageRow(usage: ExpressionUsage) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        UsageBadge("친구", usage.friend, Modifier.weight(1f))
        UsageBadge("직장동료", usage.coworker, Modifier.weight(1f))
        UsageBadge("상사", usage.boss, Modifier.weight(1f))
    }
}

@Composable
private fun UsageBadge(label: String, value: String, modifier: Modifier) {
    val icon = when (value) { "recommended" -> "🟢"; "caution" -> "🟡"; else -> "🔴" }
    Box(modifier.background(Color.White, RoundedCornerShape(14.dp)).padding(horizontal = 8.dp, vertical = 12.dp)) {
        Text("$label $icon", fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DetailCard(title: String, body: String) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) {
            Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
            Spacer(Modifier.height(7.dp))
            Text(body, color = Muted, lineHeight = 24.sp)
        }
    }
}

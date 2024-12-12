package com.joaofranco.basil.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.joaofranco.basil.data.Message


@Composable
fun ChatBubble(message: Message) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (message.isUser) 20.dp else 4.dp,
                        bottomEnd = if (message.isUser) 4.dp else 20.dp
                    )
                )
                .background(
                    if (message.isUser) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondaryContainer
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            if (message.isLoading) {
                LoadingAnimation()
            } else {
                Text(
                    text = formatMarkdown(message.content),
                    color = if (message.isUser) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

private fun formatMarkdown(text: String): AnnotatedString {
    return buildAnnotatedString {
        var startIndex = 0
        while (true) {
            val boldStartIndex = text.indexOf("**", startIndex)
            val italicStartIndex = text.indexOf("*", startIndex)
            val strikeStartIndex = text.indexOf("~~", startIndex)
            val nextIndex = listOf(boldStartIndex, italicStartIndex, strikeStartIndex).filter { it != -1 }.minOrNull() ?: -1

            if (nextIndex == -1) {
                append(text.substring(startIndex))
                break
            }

            append(text.substring(startIndex, nextIndex))

            when (nextIndex) {
                boldStartIndex -> {
                    val boldEndIndex = text.indexOf("**", boldStartIndex + 2)
                    if (boldEndIndex == -1) {
                        append(text.substring(boldStartIndex))
                        break
                    }
                    val boldText = text.substring(boldStartIndex + 2, boldEndIndex)
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append(boldText)
                    pop()
                    startIndex = boldEndIndex + 2
                }
                italicStartIndex -> {
                    val italicEndIndex = text.indexOf("*", italicStartIndex + 1)
                    if (italicEndIndex == -1) {
                        append(text.substring(italicStartIndex))
                        break
                    }
                    val italicText = text.substring(italicStartIndex + 1, italicEndIndex)
                    pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                    append(italicText)
                    pop()
                    startIndex = italicEndIndex + 1
                }
                strikeStartIndex -> {
                    val strikeEndIndex = text.indexOf("~~", strikeStartIndex + 2)
                    if (strikeEndIndex == -1) {
                        append(text.substring(strikeStartIndex))
                        break
                    }
                    val strikeText = text.substring(strikeStartIndex + 2, strikeEndIndex)
                    pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                    append(strikeText)
                    pop()
                    startIndex = strikeEndIndex + 2
                }
            }
        }
    }
}

@Composable
fun LoadingAnimation() {
    val dots = 3
    val transition = rememberInfiniteTransition(label = "loading")
    val alphas = List(dots) { index ->
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 1000
                    0f at 0 with LinearEasing
                    1f at (1000 / dots) * index with LinearEasing
                    0f at (1000 / dots) * (index + 1) with LinearEasing
                    0f at 1000 with LinearEasing
                },
                repeatMode = RepeatMode.Restart
            ), label = "dot$index"
        )
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        alphas.forEach { alpha ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.onSecondaryContainer
                            .copy(alpha = alpha.value)
                    )
            )
        }
    }
}

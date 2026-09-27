package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LazaynovaPrimary
import com.example.ui.theme.LazaynovaTextPrimary

sealed interface MarkdownElement {
    data class TextBlock(val content: String) : MarkdownElement
    data class CodeBlock(val language: String, val code: String) : MarkdownElement
    data class HeaderBlock(val level: Int, val title: String) : MarkdownElement
    data class BulletItem(val text: String) : MarkdownElement
}

/**
 * Parses a raw markdown string into a list of parsed structured blocks.
 */
fun parseMarkdown(raw: String): List<MarkdownElement> {
    val elements = mutableListOf<MarkdownElement>()
    val lines = raw.lines()
    var i = 0

    while (i < lines.size) {
        val line = lines[i]

        // Check for fenced code block ```lang
        if (line.trimStart().startsWith("```")) {
            val language = line.trimStart().removePrefix("```").trim()
            val codeBuilder = StringBuilder()
            i++
            while (i < lines.size && !lines[i].trimStart().startsWith("```")) {
                codeBuilder.append(lines[i]).append("\n")
                i++
            }
            // Skip the closing ```
            if (i < lines.size && lines[i].trimStart().startsWith("```")) {
                i++
            }
            elements.add(MarkdownElement.CodeBlock(language.ifEmpty { "code" }, codeBuilder.toString().trimEnd()))
            continue
        }

        // Headers
        if (line.startsWith("# ")) {
            elements.add(MarkdownElement.HeaderBlock(1, line.removePrefix("# ").trim()))
            i++
            continue
        } else if (line.startsWith("## ")) {
            elements.add(MarkdownElement.HeaderBlock(2, line.removePrefix("## ").trim()))
            i++
            continue
        } else if (line.startsWith("### ")) {
            elements.add(MarkdownElement.HeaderBlock(3, line.removePrefix("### ").trim()))
            i++
            continue
        }

        // Bullet lists
        if (line.trimStart().startsWith("- ") || line.trimStart().startsWith("* ")) {
            val bulletContent = line.trimStart().drop(2).trim()
            elements.add(MarkdownElement.BulletItem(bulletContent))
            i++
            continue
        }

        // Regular paragraph / text block
        val paragraphBuilder = StringBuilder()
        while (i < lines.size &&
            !lines[i].trimStart().startsWith("```") &&
            !lines[i].startsWith("# ") &&
            !lines[i].startsWith("## ") &&
            !lines[i].startsWith("### ") &&
            !lines[i].trimStart().startsWith("- ") &&
            !lines[i].trimStart().startsWith("* ")
        ) {
            paragraphBuilder.append(lines[i]).append("\n")
            i++
        }

        val text = paragraphBuilder.toString().trimEnd()
        if (text.isNotEmpty()) {
            elements.add(MarkdownElement.TextBlock(text))
        }
    }

    return elements
}

/**
 * Parses bold (**text**), italic (*text*), and inline code (`code`) into an AnnotatedString.
 */
fun buildFormattedAnnotatedString(text: String, defaultColor: Color): AnnotatedString {
    return buildAnnotatedString {
        var currentIndex = 0
        val pattern = Regex("(\\*\\*.*?\\*\\*|\\*.*?\\*|`.*?`)")
        val matches = pattern.findAll(text)

        for (match in matches) {
            val range = match.range
            if (range.first > currentIndex) {
                withStyle(SpanStyle(color = defaultColor)) {
                    append(text.substring(currentIndex, range.first))
                }
            }

            val matchedText = match.value
            when {
                matchedText.startsWith("**") && matchedText.endsWith("**") -> {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = defaultColor)) {
                        append(matchedText.removeSurrounding("**"))
                    }
                }
                matchedText.startsWith("*") && matchedText.endsWith("*") -> {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = defaultColor)) {
                        append(matchedText.removeSurrounding("*"))
                    }
                }
                matchedText.startsWith("`") && matchedText.endsWith("`") -> {
                    withStyle(
                        SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            color = LazaynovaPrimary,
                            background = Color(0xFFF1F5F9)
                        )
                    ) {
                        append(" ${matchedText.removeSurrounding("`")} ")
                    }
                }
                else -> {
                    withStyle(SpanStyle(color = defaultColor)) {
                        append(matchedText)
                    }
                }
            }
            currentIndex = range.last + 1
        }

        if (currentIndex < text.length) {
            withStyle(SpanStyle(color = defaultColor)) {
                append(text.substring(currentIndex))
            }
        }
    }
}

/**
 * Markdown rendering composable with syntax-styled code blocks and copy action.
 */
@Composable
fun MarkdownRenderer(
    content: String,
    textColor: Color = LazaynovaTextPrimary,
    onOpenCanvas: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val elements = parseMarkdown(content)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        elements.forEach { element ->
            when (element) {
                is MarkdownElement.TextBlock -> {
                    val annotated = buildFormattedAnnotatedString(element.content, textColor)
                    Text(
                        text = annotated,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                is MarkdownElement.HeaderBlock -> {
                    val (fontSize, fontWeight) = when (element.level) {
                        1 -> 18.sp to FontWeight.Bold
                        2 -> 16.sp to FontWeight.Bold
                        else -> 15.sp to FontWeight.SemiBold
                    }
                    Text(
                        text = element.title,
                        fontSize = fontSize,
                        fontWeight = fontWeight,
                        color = textColor,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }

                is MarkdownElement.BulletItem -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 1.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "•",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = LazaynovaPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                        val annotated = buildFormattedAnnotatedString(element.text, textColor)
                        Text(
                            text = annotated,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                is MarkdownElement.CodeBlock -> {
                    // Styled Code Block Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0F172A),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("code_block_card")
                    ) {
                        Column {
                            // Header bar: Language + Actions
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF1E293B))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Code,
                                        contentDescription = null,
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = element.language.uppercase(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF94A3B8),
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (onOpenCanvas != null) {
                                        TextButton(
                                            onClick = { onOpenCanvas(element.code) },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text(
                                                text = "Canvas",
                                                fontSize = 11.sp,
                                                color = Color(0xFF38BDF8),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }

                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Code", element.code)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "تم نسخ الكود بنجاح ✨", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "نسخ الكود",
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            // Code Content with horizontal scroll
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = element.code,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFFE2E8F0)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

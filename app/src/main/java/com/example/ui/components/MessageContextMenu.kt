package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.MessageEntity
import com.example.ui.theme.LazaynovaGlowCyan
import com.example.ui.theme.LazaynovaPrimary
import com.example.ui.theme.LazaynovaPrimaryLight
import com.example.ui.theme.LazaynovaTextPrimary
import com.example.ui.theme.LazaynovaTextSecondary

val AVAILABLE_REACTION_EMOJIS = listOf(
    "❤️", "👍", "🔥", "🚀", "💡", "👏", "😂", "✨"
)

/**
 * Context menu displayed on long-pressing a message, featuring quick emoji reactions and actions.
 */
@Composable
fun MessageContextMenuDialog(
    message: MessageEntity,
    onDismiss: () -> Unit,
    onSelectEmoji: (String) -> Unit,
    displayContent: String? = null,
    onOpenCanvas: ((String) -> Unit)? = null,
    onDeleteMessage: (() -> Unit)? = null,
    onCopyMessage: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val currentReactions = remember(message.reactions) { message.getReactionList() }
    val textToCopy = displayContent?.ifEmpty { null } ?: message.content

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
                .testTag("message_context_menu_dialog"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Floating Horizontal Emoji Reaction Bar
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 12.dp,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF6366F1).copy(alpha = 0.4f),
                            Color(0xFF06B6D4).copy(alpha = 0.4f),
                            Color(0xFFA855F7).copy(alpha = 0.4f)
                        )
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag("emoji_reaction_bar")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AVAILABLE_REACTION_EMOJIS.forEach { emoji ->
                        val isSelected = currentReactions.contains(emoji)
                        EmojiReactionButton(
                            emoji = emoji,
                            isSelected = isSelected,
                            onClick = {
                                onSelectEmoji(emoji)
                                onDismiss()
                            }
                        )
                    }
                }
            }

            // 2. Actions List Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    // Copy to Clipboard option
                    ContextMenuItem(
                        icon = Icons.Default.ContentCopy,
                        title = "نسخ إلى الحافظة (Copy to Clipboard)",
                        subtitle = "نسخ محتوى الرسالة بالكامل",
                        testTag = "copy_to_clipboard_option",
                        onClick = {
                            if (onCopyMessage != null) {
                                onCopyMessage()
                            } else {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Message Content", textToCopy)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "تم نسخ محتوى الرسالة إلى الحافظة 📋✨", Toast.LENGTH_SHORT).show()
                            }
                            onDismiss()
                        }
                    )

                    // Share Message
                    ContextMenuItem(
                        icon = Icons.Default.Share,
                        title = "مشاركة",
                        testTag = "context_menu_share",
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, message.content)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "مشاركة الرسالة"))
                            onDismiss()
                        }
                    )

                    // Open Code in Canvas (if present)
                    if (!message.codeSnippet.isNullOrEmpty() && onOpenCanvas != null) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = Color(0xFFF1F5F9)
                        )
                        ContextMenuItem(
                            icon = Icons.Default.Code,
                            title = "فتح في بيئة Canvas",
                            tint = LazaynovaPrimary,
                            testTag = "context_menu_canvas",
                            onClick = {
                                onOpenCanvas(message.codeSnippet)
                                onDismiss()
                            }
                        )
                    }

                    // Delete Message Option
                    if (onDeleteMessage != null) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = Color(0xFFF1F5F9)
                        )
                        ContextMenuItem(
                            icon = Icons.Default.Delete,
                            title = "حذف الرسالة",
                            tint = Color(0xFFEF4444),
                            testTag = "context_menu_delete",
                            onClick = {
                                onDeleteMessage()
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual animated emoji button inside the reaction bar.
 */
@Composable
private fun EmojiReactionButton(
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.35f else if (isSelected) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "emoji_scale"
    )

    Box(
        modifier = Modifier
            .size(38.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                if (isSelected) Color(0xFFEEF2FF) else Color.Transparent
            )
            .border(
                width = if (isSelected) 1.5.dp else 0.dp,
                color = if (isSelected) LazaynovaPrimary else Color.Transparent,
                shape = CircleShape
            )
            .clickable {
                isPressed = true
                onClick()
            }
            .testTag("reaction_emoji_$emoji"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 20.sp
        )
    }
}

/**
 * Reusable action row item inside the context menu.
 */
@Composable
private fun ContextMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    tint: Color = LazaynovaTextPrimary,
    testTag: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = tint.copy(alpha = 0.08f),
            modifier = Modifier.size(34.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = tint,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = tint
            )
            if (!subtitle.isNullOrEmpty()) {
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = LazaynovaTextSecondary,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

/**
 * Displays the list of reaction badges right under a message bubble.
 */
@Composable
fun MessageReactionsRow(
    reactions: List<String>,
    onToggleReaction: (String) -> Unit,
    onAddMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (reactions.isEmpty()) return

    // Group duplicate emojis to show count, or list distinct
    val emojiCounts = remember(reactions) {
        reactions.groupingBy { it }.eachCount()
    }

    Row(
        modifier = modifier
            .padding(top = 4.dp)
            .testTag("message_reactions_row"),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        emojiCounts.forEach { (emoji, count) ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shadowElevation = 1.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onToggleReaction(emoji) }
                    .testTag("reaction_badge_$emoji")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = emoji, fontSize = 13.sp)
                    if (count > 1) {
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = count.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LazaynovaPrimary
                        )
                    }
                }
            }
        }

        // Small '+' button to quickly add reaction
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFFF8FAFC))
                .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                .clickable { onAddMore() }
                .testTag("add_reaction_badge_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "إضافة تفاعل",
                tint = LazaynovaTextSecondary,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

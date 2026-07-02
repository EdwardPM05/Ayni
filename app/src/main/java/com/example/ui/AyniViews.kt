package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import coil.compose.SubcomposeAsyncImage
import androidx.compose.ui.layout.ContentScale

/**
 * High-fidelity Inca-inspired vector Ayni.pe logo Composable
 * Uses clean Paths to draw the mutual help figure.
 */
@Composable
fun AyniLogo(
    modifier: Modifier = Modifier,
    showSlogan: Boolean = true,
    sizeScale: Float = 1.0f
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size((36 * sizeScale).dp)
                    .clip(RoundedCornerShape((10 * sizeScale).dp))
                    .background(Terracotta)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = (20 * sizeScale).sp,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.width((8 * sizeScale).dp))
            
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Ayni",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = (28 * sizeScale).sp,
                        color = DarkBrown,
                        letterSpacing = (-0.5).sp
                    )
                )
            }
        }

        if (showSlogan) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "ENCUENTRA • CONECTA • RESUELVE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = (9 * sizeScale).sp,
                    color = Gold,
                    letterSpacing = 1.5.sp
                )
            )
        }
    }
}

/**
 * Reusable Modern Gradient Button with Smooth Hover / Scaling animations
 */
@Composable
fun AyniButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(Terracotta, Terracotta),
    icon: ImageVector? = null,
    textColor: Color = Color.White
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.03f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isHovered) 4.dp else 1.dp
    )

    Surface(
        modifier = modifier
            .hoverable(interactionSource)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        shadowElevation = shadowElevation,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(Brush.horizontalGradient(colors))
                .padding(horizontal = 20.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}

/**
 * Secondary outlined modern button
 */
@Composable
fun AyniOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color = Terracotta,
    textColor: Color = Terracotta,
    icon: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.03f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .hoverable(interactionSource)
            .graphicsLayer(scaleX = scale, scaleY = scale),
        border = BorderStroke(1.5.dp, borderColor),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = textColor,
            containerColor = if (isHovered) borderColor.copy(alpha = 0.05f) else Color.Transparent
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            )
        }
    }
}

/**
 * Visual Canvas Art rendering of Princess theme birthday decoration
 */
@Composable
fun DrawPrincessTheme(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Pastel Pink magical background
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFCE4EC), Color(0xFFF8BBD0))
            )
        )

        // Draw Princess Castle Towers in background
        val castleBrush = Brush.verticalGradient(listOf(Color(0xFFE1BEE7), Color(0xFFD1C4E9)))
        // Left tower
        drawRect(castleBrush, Offset(w * 0.15f, h * 0.35f), Size(w * 0.12f, h * 0.65f))
        // Center main palace wall
        drawRect(castleBrush, Offset(w * 0.35f, h * 0.45f), Size(w * 0.3f, h * 0.55f))
        // Right tower
        drawRect(castleBrush, Offset(w * 0.73f, h * 0.35f), Size(w * 0.12f, h * 0.65f))

        // Castle roofs (triangles)
        val roofPath = Path().apply {
            moveTo(w * 0.15f, h * 0.35f)
            lineTo(w * 0.21f, h * 0.15f)
            lineTo(w * 0.27f, h * 0.35f)
            close()
            
            moveTo(w * 0.73f, h * 0.35f)
            lineTo(w * 0.79f, h * 0.15f)
            lineTo(w * 0.85f, h * 0.35f)
            close()
        }
        drawPath(roofPath, Color(0xFFEC407A))

        // Large gold sparkling decorative round banner arch in center
        drawCircle(
            color = Color(0xFFFFD54F),
            radius = w * 0.22f,
            center = Offset(w * 0.50f, h * 0.50f),
            style = Stroke(width = 16f)
        )

        // Floating balloons on sides
        drawCircle(Color(0xFFB3E5FC), radius = w * 0.05f, center = Offset(w * 0.08f, h * 0.25f))
        drawCircle(Color(0xFFFFCDD2), radius = w * 0.06f, center = Offset(w * 0.12f, h * 0.20f))
        drawCircle(Color(0xFFE8F5E9), radius = w * 0.05f, center = Offset(w * 0.90f, h * 0.28f))
        drawCircle(Color(0xFFFFF9C4), radius = w * 0.06f, center = Offset(w * 0.84f, h * 0.22f))

        // A round table with a golden crown cake representation in front
        drawRoundRect(
            color = Color(0xFFF50057),
            topLeft = Offset(w * 0.38f, h * 0.75f),
            size = Size(w * 0.24f, h * 0.25f),
            cornerRadius = CornerRadius(16f, 16f)
        )

        // Glitter circles (Gold)
        for (i in 0..10) {
            val randomX = (0.2f + i * 0.06f) * w
            val randomY = (0.1f + (i % 3) * 0.12f) * h
            drawCircle(Color(0xFFFFD54F), radius = 6f, center = Offset(randomX, randomY))
        }
    }
}

/**
 * Visual Canvas Art rendering of Masha and the Bear theme
 */
@Composable
fun DrawMashaTheme(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Sunny Forest green gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFDCEDC8), Color(0xFF81C784))
            )
        )

        // Wood texture log elements
        val logColor = Color(0xFF5D4037)
        drawRoundRect(logColor, Offset(w * 0.40f, h * 0.55f), Size(w * 0.2f, h * 0.45f), CornerRadius(20f, 20f))

        // Pine trees in the background
        val treeColor = Color(0xFF2E7D32)
        val treePath = Path().apply {
            moveTo(w * 0.15f, h * 0.65f)
            lineTo(w * 0.05f, h * 0.85f)
            lineTo(w * 0.25f, h * 0.85f)
            close()
            moveTo(w * 0.85f, h * 0.60f)
            lineTo(w * 0.75f, h * 0.80f)
            lineTo(w * 0.95f, h * 0.80f)
            close()
        }
        drawPath(treePath, treeColor)

        // Giant round background panel with Masha and Bear forest illustration representation
        drawCircle(
            color = Color(0xFFFFF9C4),
            radius = w * 0.24f,
            center = Offset(w * 0.50f, h * 0.45f)
        )

        // Forest Sun
        drawCircle(Color(0xFFFFB74D), radius = w * 0.08f, center = Offset(w * 0.82f, h * 0.20f))

        // Balloons (Pink and White)
        drawCircle(Color(0xFFE91E63), radius = w * 0.06f, center = Offset(w * 0.24f, h * 0.35f))
        drawCircle(Color(0xFFFFF5F5), radius = w * 0.05f, center = Offset(w * 0.28f, h * 0.28f))
        drawCircle(Color(0xFFE91E63), radius = w * 0.05f, center = Offset(w * 0.70f, h * 0.32f))

        // Table with cake setup
        drawRoundRect(
            color = Color(0xFFC2185B),
            topLeft = Offset(w * 0.15f, h * 0.72f),
            size = Size(w * 0.22f, h * 0.28f),
            cornerRadius = CornerRadius(12f, 12f)
        )
    }
}

/**
 * Visual Canvas Art of Spongebob theme decoration
 */
@Composable
fun DrawSpongebobTheme(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Ocean depths light aqua gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFE0F7FA), Color(0xFF4DD0E1))
            )
        )

        // Large pineapple house representation in the center
        drawRoundRect(
            color = Color(0xFFFFB74D),
            topLeft = Offset(w * 0.35f, h * 0.30f),
            size = Size(w * 0.30f, h * 0.55f),
            cornerRadius = CornerRadius(32f, 32f)
        )
        // Green leaves on pineapple top
        val leavesPath = Path().apply {
            moveTo(w * 0.50f, h * 0.30f)
            quadraticTo(w * 0.40f, h * 0.15f, w * 0.36f, h * 0.12f)
            quadraticTo(w * 0.48f, h * 0.24f, w * 0.48f, h * 0.30f)
            
            moveTo(w * 0.50f, h * 0.30f)
            quadraticTo(w * 0.50f, h * 0.10f, w * 0.50f, h * 0.08f)
            quadraticTo(w * 0.52f, h * 0.22f, w * 0.52f, h * 0.30f)

            moveTo(w * 0.50f, h * 0.30f)
            quadraticTo(w * 0.60f, h * 0.15f, w * 0.64f, h * 0.12f)
            quadraticTo(w * 0.52f, h * 0.24f, w * 0.52f, h * 0.30f)
        }
        drawPath(leavesPath, Color(0xFF2E7D32))

        // Pineapple lines / grid cross hatch
        for (i in 1..4) {
            drawLine(
                color = Color(0xFFE65100),
                start = Offset(w * (0.35f + i * 0.06f), h * 0.30f),
                end = Offset(w * (0.35f + (i - 1) * 0.06f), h * 0.85f),
                strokeWidth = 4f
            )
            drawLine(
                color = Color(0xFFE65100),
                start = Offset(w * (0.35f + (i - 1) * 0.06f), h * 0.30f),
                end = Offset(w * (0.35f + i * 0.06f), h * 0.85f),
                strokeWidth = 4f
            )
        }

        // Ocean floor sandy yellow
        drawRect(Color(0xFFFFF9C4), Offset(0f, h * 0.82f), Size(w, h * 0.18f))

        // Blue and Yellow balloons floating
        drawCircle(Color(0xFFFBC02D), radius = w * 0.05f, center = Offset(w * 0.18f, h * 0.35f))
        drawCircle(Color(0xFF0288D1), radius = w * 0.06f, center = Offset(w * 0.22f, h * 0.26f))
        drawCircle(Color(0xFF00E676), radius = w * 0.05f, center = Offset(w * 0.80f, h * 0.40f))

        // Coral & Bubbles
        drawCircle(Color.White.copy(alpha = 0.6f), radius = 8f, center = Offset(w * 0.30f, h * 0.50f))
        drawCircle(Color.White.copy(alpha = 0.6f), radius = 12f, center = Offset(w * 0.28f, h * 0.44f))
    }
}

/**
 * Visual Canvas Art of Alianza Lima theme decoration
 */
@Composable
fun DrawAlianzaTheme(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Midnight Blue & White Football theme stripes
        drawRect(Color(0xFF0A192F))

        val stripeWidth = w * 0.08f
        for (i in 0..5) {
            drawRect(
                color = Color.White,
                topLeft = Offset(i * stripeWidth * 2.5f + stripeWidth, 0f),
                size = Size(stripeWidth, h)
            )
        }

        // Central Shield / Crest placeholder
        val shieldPath = Path().apply {
            moveTo(w * 0.38f, h * 0.25f)
            lineTo(w * 0.62f, h * 0.25f)
            lineTo(w * 0.62f, h * 0.55f)
            quadraticTo(w * 0.50f, h * 0.72f, w * 0.50f, h * 0.72f)
            quadraticTo(w * 0.38f, h * 0.55f, w * 0.38f, h * 0.55f)
            close()
        }
        drawPath(shieldPath, Color(0xFF0A192F))
        drawPath(shieldPath, Color.White, style = Stroke(width = 8f))

        // Crown / Stars details inside crest
        drawCircle(Color(0xFFFFD54F), radius = 10f, center = Offset(w * 0.50f, h * 0.35f))
        drawCircle(Color(0xFFFFD54F), radius = 6f, center = Offset(w * 0.45f, h * 0.38f))
        drawCircle(Color(0xFFFFD54F), radius = 6f, center = Offset(w * 0.55f, h * 0.38f))

        // Alianza letters or striped ribbon
        drawLine(Color.White, Offset(w * 0.42f, h * 0.45f), Offset(w * 0.58f, h * 0.45f), strokeWidth = 10f)
        drawLine(Color.White, Offset(w * 0.42f, h * 0.52f), Offset(w * 0.58f, h * 0.52f), strokeWidth = 10f)

        // White and dark blue soccer balloons arch
        for (i in 0..8) {
            val offsetAngle = i * (Math.PI / 8)
            val ballX = (0.5f + Math.cos(offsetAngle) * 0.4f) * w
            val ballY = (0.8f - Math.sin(offsetAngle) * 0.5f) * h
            drawCircle(
                color = if (i % 2 == 0) Color.White else Color(0xFF0A2240),
                radius = w * 0.05f,
                center = Offset(ballX.toFloat(), ballY.toFloat())
            )
        }
    }
}

/**
 * Visual Canvas Art of Baby Shark theme decoration
 */
@Composable
fun DrawBabySharkTheme(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Marine deep blue water
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF00E5FF), Color(0xFF006064))
            )
        )

        // Cute yellow shark silhouette in center
        val sharkPath = Path().apply {
            moveTo(w * 0.25f, h * 0.50f)
            quadraticTo(w * 0.45f, h * 0.30f, w * 0.65f, h * 0.45f) // top body
            quadraticTo(w * 0.78f, h * 0.50f, w * 0.80f, h * 0.42f) // tail top
            lineTo(w * 0.85f, h * 0.58f) // tail tip
            quadraticTo(w * 0.75f, h * 0.58f, w * 0.68f, h * 0.54f) // tail bottom
            quadraticTo(w * 0.45f, h * 0.70f, w * 0.25f, h * 0.50f) // bottom body
            close()
        }
        drawPath(sharkPath, Color(0xFFFFEB3B))

        // Shark underbelly (White)
        val bellyPath = Path().apply {
            moveTo(w * 0.32f, h * 0.56f)
            quadraticTo(w * 0.50f, h * 0.66f, w * 0.66f, h * 0.54f)
            quadraticTo(w * 0.50f, h * 0.54f, w * 0.32f, h * 0.56f)
            close()
        }
        drawPath(bellyPath, Color.White)

        // Little shark eye (black and white)
        drawCircle(Color.White, radius = 10f, center = Offset(w * 0.38f, h * 0.45f))
        drawCircle(Color.Black, radius = 5f, center = Offset(w * 0.39f, h * 0.45f))

        // Coral reef on floor
        drawRect(Color(0xFFFFF176), Offset(0f, h * 0.85f), Size(w, h * 0.15f))
        
        // Sea plants (Green)
        val seaweed = Path().apply {
            moveTo(w * 0.12f, h * 0.85f)
            quadraticTo(w * 0.08f, h * 0.70f, w * 0.14f, h * 0.55f)
            quadraticTo(w * 0.18f, h * 0.70f, w * 0.16f, h * 0.85f)
        }
        drawPath(seaweed, Color(0xFF00E676))

        // Undersea bubble floaters
        drawCircle(Color.White.copy(alpha = 0.5f), radius = 6f, center = Offset(w * 0.55f, h * 0.30f))
        drawCircle(Color.White.copy(alpha = 0.5f), radius = 10f, center = Offset(w * 0.51f, h * 0.22f))
    }
}

/**
 * Visual Canvas Art of Little Mermaid theme decoration
 */
@Composable
fun DrawMermaidTheme(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Mystical violet and teal mermaid waters
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFE040FB), Color(0xFF00B0FF))
            )
        )

        // Golden circular panel representing the sea backdrop
        drawCircle(
            color = Color(0xFFFFD54F),
            radius = w * 0.22f,
            center = Offset(w * 0.50f, h * 0.45f)
        )

        // Dark silhouette representation of mermaid tail
        val tailPath = Path().apply {
            moveTo(w * 0.48f, h * 0.65f)
            quadraticTo(w * 0.46f, h * 0.48f, w * 0.52f, h * 0.32f) // body curve
            quadraticTo(w * 0.54f, h * 0.24f, w * 0.60f, h * 0.20f) // tail base
            // Left fin
            quadraticTo(w * 0.52f, h * 0.16f, w * 0.48f, h * 0.08f)
            quadraticTo(w * 0.54f, h * 0.22f, w * 0.60f, h * 0.24f)
            // Right fin
            quadraticTo(w * 0.68f, h * 0.20f, w * 0.74f, h * 0.14f)
            quadraticTo(w * 0.64f, h * 0.25f, w * 0.60f, h * 0.24f)
            
            // Rejoin body
            quadraticTo(w * 0.54f, h * 0.35f, w * 0.52f, h * 0.65f)
            close()
        }
        drawPath(tailPath, Color(0xFF006064))

        // Giant round organic balloons on left and right edges (Violet, Aqua, White)
        drawCircle(Color(0xFFEA80FC), radius = w * 0.07f, center = Offset(w * 0.15f, h * 0.30f))
        drawCircle(Color(0xFF80DEEA), radius = w * 0.06f, center = Offset(w * 0.22f, h * 0.22f))
        drawCircle(Color(0xFFEA80FC), radius = w * 0.05f, center = Offset(w * 0.18f, h * 0.18f))

        drawCircle(Color(0xFF84FFFF), radius = w * 0.08f, center = Offset(w * 0.85f, h * 0.35f))
        drawCircle(Color(0xFFE040FB), radius = w * 0.06f, center = Offset(w * 0.78f, h * 0.28f))
    }
}

/**
 * Visual Canvas representing Liz Evelyn profile photo (Smiling woman with gold background & flowers)
 */
@Composable
fun DrawLizAvatar(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        SubcomposeAsyncImage(
            model = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&q=80&w=256",
            contentDescription = "Liz Evelyn",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().clip(CircleShape),
            loading = {
                DrawLizVectorCanvas(modifier = Modifier.fillMaxSize())
            },
            error = {
                DrawLizVectorCanvas(modifier = Modifier.fillMaxSize())
            }
        )
    }
}

@Composable
fun DrawLizVectorCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Shiny golden textured background
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFF176), Color(0xFFF57F17)),
                center = Offset(w / 2, h / 2),
                radius = w * 0.7f
            )
        )

        // Draw elegant floral ring border
        drawCircle(
            color = Color(0xFFFFD54F),
            radius = w * 0.43f,
            center = Offset(w / 2, h / 2),
            style = Stroke(width = 12f)
        )

        // Draw the woman smiling (elegant stylization)
        // Hair (Dark Brown)
        val hairPath = Path().apply {
            moveTo(w * 0.30f, h * 0.60f)
            quadraticTo(w * 0.25f, h * 0.25f, w * 0.50f, h * 0.20f)
            quadraticTo(w * 0.75f, h * 0.25f, w * 0.70f, h * 0.60f)
            quadraticTo(w * 0.78f, h * 0.78f, w * 0.82f, h * 0.90f)
            lineTo(w * 0.18f, h * 0.90f)
            quadraticTo(w * 0.22f, h * 0.78f, w * 0.30f, h * 0.60f)
            close()
        }
        drawPath(hairPath, Color(0xFF2D1500))

        // Skin (Warm peach beige)
        drawCircle(
            color = Color(0xFFFFCCAC),
            radius = w * 0.25f,
            center = Offset(w / 2, h * 0.48f)
        )

        // Hair front locks (Brown)
        val locks = Path().apply {
            moveTo(w * 0.35f, h * 0.35f)
            quadraticTo(w * 0.50f, h * 0.30f, w * 0.65f, h * 0.35f)
            quadraticTo(w * 0.50f, h * 0.40f, w * 0.35f, h * 0.35f)
        }
        drawPath(locks, Color(0xFF3E2723))

        // Face features (Simplified eyes, smile with blush)
        // Eyes (Smiling arcs)
        drawArc(
            color = Color(0xFF3E2723),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.38f, h * 0.42f),
            size = Size(w * 0.08f, h * 0.04f),
            style = Stroke(width = 4f)
        )
        drawArc(
            color = Color(0xFF3E2723),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.54f, h * 0.42f),
            size = Size(w * 0.08f, h * 0.04f),
            style = Stroke(width = 4f)
        )

        // Cute blush (Rosy cheeks)
        drawCircle(Color(0xFFFF8A80).copy(alpha = 0.6f), radius = w * 0.035f, center = Offset(w * 0.36f, h * 0.50f))
        drawCircle(Color(0xFFFF8A80).copy(alpha = 0.6f), radius = w * 0.035f, center = Offset(w * 0.64f, h * 0.50f))

        // Happy broad smile
        val smilePath = Path().apply {
            moveTo(w * 0.44f, h * 0.52f)
            quadraticTo(w * 0.50f, h * 0.62f, w * 0.56f, h * 0.52f)
            quadraticTo(w * 0.50f, h * 0.55f, w * 0.44f, h * 0.52f)
        }
        drawPath(smilePath, Color(0xFFB71C1C)) // red lips smile

        // Sparkly white teeth
        val teeth = Path().apply {
            moveTo(w * 0.45f, h * 0.53f)
            lineTo(w * 0.55f, h * 0.53f)
            quadraticTo(w * 0.50f, h * 0.57f, w * 0.45f, h * 0.53f)
        }
        drawPath(teeth, Color.White)

        // Clothing neck (Teal blouse representation)
        val clothes = Path().apply {
            moveTo(w * 0.30f, h * 0.73f)
            lineTo(w * 0.70f, h * 0.73f)
            lineTo(w * 0.75f, h * 0.95f)
            lineTo(w * 0.25f, h * 0.95f)
            close()
        }
        drawPath(clothes, Color(0xFF006064))

        // Floral arrangements overlay at bottom corners (Beautiful pink flowers)
        drawCircle(Color(0xFFEC407A), radius = w * 0.10f, center = Offset(w * 0.15f, h * 0.88f))
        drawCircle(Color(0xFFEC407A), radius = w * 0.10f, center = Offset(w * 0.85f, h * 0.88f))
        drawCircle(Color(0xFFFF8A00), radius = w * 0.07f, center = Offset(w * 0.22f, h * 0.82f))
        drawCircle(Color(0xFFFF8A00), radius = w * 0.07f, center = Offset(w * 0.78f, h * 0.82f))
    }
}

/**
 * Simulated GPS Map drawing with custom markers, pins, roads, and names
 */
@Composable
fun DrawSimulatedMap(
    isLocationFound: Boolean,
    onPinClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Grid/Map background (light cream ground)
        drawRect(Color(0xFFF1EDE6))

        // Draw green parks
        drawRoundRect(Color(0xFFC8E6C9), Offset(w * 0.05f, h * 0.1f), Size(w * 0.35f, h * 0.22f), CornerRadius(16f, 16f))
        drawRoundRect(Color(0xFFC8E6C9), Offset(w * 0.55f, h * 0.55f), Size(w * 0.40f, h * 0.30f), CornerRadius(16f, 16f))

        // Draw grey water river (Rímac river style simulation)
        val riverPath = Path().apply {
            moveTo(0f, h * 0.80f)
            cubicTo(w * 0.25f, h * 0.85f, w * 0.60f, h * 0.72f, w, h * 0.92f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(riverPath, Color(0xFFB2EBF2))

        // Draw streets / roads grid (bold white lines)
        val strokeRoad = 24f
        val roadColor = Color.White
        // Horizontal roads
        drawLine(roadColor, Offset(0f, h * 0.28f), Offset(w, h * 0.28f), strokeWidth = strokeRoad)
        drawLine(roadColor, Offset(0f, h * 0.52f), Offset(w, h * 0.52f), strokeWidth = strokeRoad)
        drawLine(roadColor, Offset(0f, h * 0.75f), Offset(w, h * 0.75f), strokeWidth = strokeRoad)
        // Vertical roads
        drawLine(roadColor, Offset(w * 0.25f, 0f), Offset(w * 0.25f, h), strokeWidth = strokeRoad)
        drawLine(roadColor, Offset(w * 0.50f, 0f), Offset(w * 0.50f, h), strokeWidth = strokeRoad)
        drawLine(roadColor, Offset(w * 0.78f, 0f), Offset(w * 0.78f, h), strokeWidth = strokeRoad)

        // If location is verified, draw pins
        if (isLocationFound) {
            // User current location pin (Deep blue glowing circle with arrow)
            val userX = w * 0.50f
            val userY = h * 0.52f
            
            // Outer pulse circle
            drawCircle(Color(0xFF2979FF).copy(alpha = 0.25f), radius = 55f, center = Offset(userX, userY))
            // Inner core
            drawCircle(Color.White, radius = 22f, center = Offset(userX, userY))
            drawCircle(Color(0xFF2979FF), radius = 15f, center = Offset(userX, userY))

            // Professional pins on the map (representing technicians)
            // Pin 1: Liz Evelyn Obregon (Decoración) - Close to user
            drawMapPin(this, w * 0.38f, h * 0.44f, Terracotta, "Liz Evelyn")
            // Pin 2: Carlos Soto (Electricista)
            drawMapPin(this, w * 0.68f, h * 0.38f, Gold, "Carlos Soto")
            // Pin 3: Elena Huaman (Limpieza)
            drawMapPin(this, w * 0.44f, h * 0.68f, DarkBrown, "Elena H.")
        }
    }
}

private fun drawMapPin(scope: DrawScope, x: Float, y: Float, color: Color, label: String) {
    // Balloon / drop marker path
    val path = Path().apply {
        moveTo(x, y)
        quadraticTo(x - 22f, y - 45f, x - 22f, y - 55f)
        quadraticTo(x - 22f, y - 75f, x, y - 75f)
        quadraticTo(x + 22f, y - 75f, x + 22f, y - 55f)
        quadraticTo(x + 22f, y - 45f, x, y)
        close()
    }
    scope.drawPath(path, color)
    // White inner eye
    scope.drawCircle(Color.White, radius = 10f, center = Offset(x, y - 55f))
    scope.drawCircle(color, radius = 5f, center = Offset(x, y - 55f))

    // Tiny dot anchor shadow
    scope.drawCircle(Color.Black.copy(alpha = 0.3f), radius = 6f, center = Offset(x, y + 2f))
}

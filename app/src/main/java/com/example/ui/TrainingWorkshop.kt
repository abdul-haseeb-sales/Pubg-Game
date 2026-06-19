package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.gestures.detectTapGestures
import kotlin.math.*

// Mini-games chooser enum
enum class ActiveGame {
    NONE,
    BUGGY_CLIMB,   // Hill Climb style buggy game
    SHADOW_DUEL    // Shadow Fight style silhouette fighting combat
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TrainingWorkshopScreen(
    viewModel: CompanionViewModel,
    onClose: () -> Unit
) {
    var activeGame by remember { mutableStateOf(ActiveGame.NONE) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GunmetalDark.copy(alpha = 0.98f))
            .testTag("training_workshop_screen")
    ) {
        // High Contrast Tech Grid Overlay Background representation
        Canvas(modifier = Modifier.fillMaxSize()) {
            val columns = 12
            val rows = 24
            val columnWidth = size.width / columns
            val rowHeight = size.height / rows
            for (i in 0..columns) {
                drawLine(
                    color = Color.White.copy(alpha = 0.015f),
                    start = Offset(i * columnWidth, 0f),
                    end = Offset(i * columnWidth, size.height)
                )
            }
            for (i in 0..rows) {
                drawLine(
                    color = Color.White.copy(alpha = 0.015f),
                    start = Offset(0f, i * rowHeight),
                    end = Offset(size.width, i * rowHeight)
                )
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "TACTICAL ARCADE WORKSHOP",
                        color = SolarYellow,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "CHALLENGE AND PLAY TO EARN LOBBY BONUSES",
                        color = SubtitleGrey,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                IconButton(
                    onClick = {
                        if (activeGame != ActiveGame.NONE) {
                            activeGame = ActiveGame.NONE
                        } else {
                            onClose()
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(SlateGrey.copy(alpha = 0.8f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Arcade",
                        tint = Color.White
                    )
                }
            }

            AnimatedContent(
                targetState = activeGame,
                transitionSpec = {
                    slideInHorizontally { width -> width } with slideOutHorizontally { width -> -width }
                },
                label = "active_game_anim",
                modifier = Modifier.weight(1f)
            ) { game ->
                when (game) {
                    ActiveGame.NONE -> {
                        GameSelectionGrid(
                            onSelectGame = { activeGame = it }
                        )
                    }
                    ActiveGame.BUGGY_CLIMB -> {
                        BuggyClimbGameView(viewModel)
                    }
                    ActiveGame.SHADOW_DUEL -> {
                        ShadowDuelGameView(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun GameSelectionGrid(onSelectGame: (ActiveGame) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SELECT YOUR TARGET PRACTICE",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hill Climb Game Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(0.85f)
                    .background(SlateGrey.copy(alpha = 0.9f), RoundedCornerShape(20.dp))
                    .border(2.dp, TacticalOrange, RoundedCornerShape(20.dp))
                    .clickable { onSelectGame(ActiveGame.BUGGY_CLIMB) }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(TacticalOrange.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "Buggy Hill climb",
                            tint = TacticalOrange,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Text(
                        text = "MIRAMAR BUGGY RUN",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Physics climb simulator. Guide the PUBG Buggy over steep heights to reach dynamic supply drops without flipping over!",
                        color = SubtitleGrey,
                        fontSize = 9.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 13.sp
                    )
                }
            }

            // Shadow Duel Game Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(0.85f)
                    .background(SlateGrey.copy(alpha = 0.9f), RoundedCornerShape(20.dp))
                    .border(2.dp, SolarYellow, RoundedCornerShape(20.dp))
                    .clickable { onSelectGame(ActiveGame.SHADOW_DUEL) }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(SolarYellow.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Shadow Fighter",
                            tint = SolarYellow,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Text(
                        text = "SHADOW COMBAT DUEL",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "High speed samurai duels! Time attacks when shadows are vulnerable, or block precisely to deflect lethal visual sweeps.",
                        color = SubtitleGrey,
                        fontSize = 9.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "🏆 ALL MINI GAME COINS EARNED DIRECTLY INVENTORIES AS REAL LOBBY BP COINS!",
            color = SolarYellow,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center
        )
    }
}

// ==========================================
// GAME 1: MIRAMAR BUGGY CLIMB (HILL CLIMB STYLE)
// ==========================================
@Composable
fun BuggyClimbGameView(viewModel: CompanionViewModel) {
    // Game variables
    var buggyX by remember { mutableStateOf(150f) }
    var buggyY by remember { mutableStateOf(200f) }
    var velocityX by remember { mutableStateOf(0f) }
    var velocityY by remember { mutableStateOf(0f) }
    var tiltAngle by remember { mutableStateOf(0f) } // in radians
    var isFlipped by remember { mutableStateOf(false) }
    var coinsCollected by remember { mutableStateOf(0) }
    var isWon by remember { mutableStateOf(false) }
    var hasCrashed by remember { mutableStateOf(false) }

    var gasPressed by remember { mutableStateOf(false) }
    var brakePressed by remember { mutableStateOf(false) }

    // Coins positions pre-generated along map
    val coinsList = remember {
        List(25) { index ->
            val cx = 400f + index * 160f
            Offset(cx, getTerrainHeight(cx) - 35f)
        }
    }
    val collectedCoins = remember { mutableStateMapOf<Int, Boolean>() }

    // Dimensions
    val buggyWidth = 48.dp
    val targetDistance = 3500f // Goal point

    // Engine loop ticker
    LaunchedEffect(key1 = hasCrashed, key2 = isWon) {
        if (!hasCrashed && !isWon) {
            var lastTime = System.currentTimeMillis()
            while (true) {
                val currTime = System.currentTimeMillis()
                val dt = ((currTime - lastTime) / 1000f).coerceAtMost(0.05f)
                lastTime = currTime

                // Terrain heights around buggy to evaluate placement and slope
                val groundY = getTerrainHeight(buggyX)
                val isNearGround = buggyY >= groundY - 8f

                if (isNearGround) {
                    buggyY = groundY
                    velocityY = 0f

                    // Get terrain slope tilt angles
                    val aheadY = getTerrainHeight(buggyX + 24f)
                    val behindY = getTerrainHeight(buggyX - 24f)
                    val slope = (aheadY - behindY) / 48f
                    val targetAngle = atan2(aheadY - behindY, 48f)

                    // Physics on ground
                    // Align buggy angle dynamically
                    val difference = targetAngle - tiltAngle
                    tiltAngle += difference * 0.20f

                    // Gradients pull the buggy backward
                    velocityX -= slope * 850f * dt

                    // Slower natural engine drag
                    velocityX *= (1f - 0.15f * dt)

                    if (gasPressed) {
                        velocityX += 480f * dt
                    }
                    if (brakePressed) {
                        velocityX -= 600f * dt
                    }

                    // Crash detection - if buggy's alignment flips beyond safe tolerance
                    val rotationDiff = abs(tiltAngle - targetAngle) % (2 * PI.toFloat())
                    if (rotationDiff > 1.7f) {
                        hasCrashed = true
                    }
                } else {
                    // Physics in mid-air
                    // Pull down by gravity
                    velocityY += 600f * dt
                    // High rotation dynamics when buttons are held
                    if (gasPressed) {
                        tiltAngle += 2.8f * dt
                    }
                    if (brakePressed) {
                        tiltAngle -= 2.8f * dt
                    }
                }

                // Drag/Limits
                velocityX = velocityX.coerceIn(-100f, 650f)

                // Move buggy coords
                buggyX += velocityX * dt
                buggyY += velocityY * dt

                // Bounce on floor boundaries
                val currentGround = getTerrainHeight(buggyX)
                if (buggyY > currentGround) {
                    buggyY = currentGround
                }

                // Check distance limitations
                if (buggyX < 150f) {
                    buggyX = 150f
                    velocityX = 0f
                }

                // Coins checking
                coinsList.forEachIndexed { idx, coin ->
                    if (collectedCoins[idx] != true) {
                        val distanceToCoin = sqrt((buggyX - coin.x).pow(2) + (buggyY - 15f - coin.y).pow(2))
                        if (distanceToCoin < 32f) {
                            collectedCoins[idx] = true
                            coinsCollected++
                            // Instantly reward main BP
                            viewModel.bpCurrency.value += 50
                        }
                    }
                }

                // Won checks
                if (buggyX >= targetDistance) {
                    isWon = true
                    viewModel.bpCurrency.value += 1000 // big victory payout !!
                }

                delay(12)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Hud Display panel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(SlateGrey.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DISTANCE COMPLETED",
                    color = SubtitleGrey,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${min(buggyX.toInt(), targetDistance.toInt())}m / ${targetDistance.toInt()}m",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SPEED",
                    color = SubtitleGrey,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${max(0, (velocityX * 0.35f).toInt())} KM/H",
                    color = SolarYellow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "BP RECIPE COINS",
                    color = SubtitleGrey,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "+${coinsCollected * 50} BP",
                    color = WinnerGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Live Gaming Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF2E1C0C)) // Desert sunset feel
                .border(2.dp, SlateGrey),
            contentAlignment = Alignment.TopCenter
        ) {
            // Sun Background
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Ground sun graphic
                drawCircle(
                    color = Color(0xFFFFCC80).copy(alpha = 0.25f),
                    radius = 200f,
                    center = Offset(size.width * 0.5f, size.height * 0.3f)
                )
                drawCircle(
                    color = Color(0xFFFFAB91).copy(alpha = 0.12f),
                    radius = 350f,
                    center = Offset(size.width * 0.5f, size.height * 0.3f)
                )
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val viewScrollX = buggyX - 150f // scroll perspective anchored to buggy

                // Draw Goal Flag / Crate at Target distance
                val flagDrawX = targetDistance - viewScrollX
                if (flagDrawX > -100f && flagDrawX < size.width + 100f) {
                    val crateY = getTerrainHeight(targetDistance)
                    // Pole
                    drawLine(
                        color = Color.White,
                        start = Offset(flagDrawX, crateY),
                        end = Offset(flagDrawX, crateY - 120f),
                        strokeWidth = 4f
                    )
                    // Flag Banner (Flares)
                    val flagPath = Path().apply {
                        moveTo(flagDrawX, crateY - 120f)
                        lineTo(flagDrawX + 45f, crateY - 100f)
                        lineTo(flagDrawX, crateY - 80f)
                        close()
                    }
                    drawPath(flagPath, color = TacticalOrange)

                    // Airdrop loot box
                    drawRoundRect(
                        color = Color(0xFFC62828),
                        topLeft = Offset(flagDrawX - 20f, crateY - 30f),
                        size = Size(40f, 30f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                    // Blue cover cloth
                    drawRect(
                        color = Color(0xFF0D47A1),
                        topLeft = Offset(flagDrawX - 22f, crateY - 30f),
                        size = Size(44f, 8f)
                    )
                }

                // Drawing Coins along path relative to scroll
                coinsList.forEachIndexed { index, coin ->
                    if (collectedCoins[index] != true) {
                        val renderCoinX = coin.x - viewScrollX
                        if (renderCoinX > -50f && renderCoinX < size.width + 50f) {
                            drawCircle(
                                color = SolarYellow,
                                radius = 10f,
                                center = Offset(renderCoinX, coin.y)
                            )
                            drawCircle(
                                color = WinnerGold,
                                radius = 6f,
                                center = Offset(renderCoinX, coin.y),
                                style = Stroke(width = 2f)
                            )
                        }
                    }
                }

                // Drawing Sand Dunes Hills Terrain
                val terrainPath = Path()
                terrainPath.moveTo(0f, size.height)

                val terrainSegments = (size.width / 5).toInt() + 2
                for (i in 0..terrainSegments) {
                    val pieceX = i * 5f
                    val worldX = pieceX + viewScrollX
                    val heightY = getTerrainHeight(worldX)
                    if (i == 0) {
                        terrainPath.lineTo(pieceX, heightY)
                    } else {
                        terrainPath.lineTo(pieceX, heightY)
                    }
                }
                terrainPath.lineTo(size.width, size.height)
                terrainPath.close()

                drawPath(
                    path = terrainPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFE5A65D), Color(0xFF8D531F)),
                        startY = 180f,
                        endY = 550f
                    )
                )

                // Render terrain crust line
                for (i in 1..terrainSegments) {
                    val prevPieceX = (i - 1) * 5f
                    val prevHeightY = getTerrainHeight(prevPieceX + viewScrollX)
                    val currPieceX = i * 5f
                    val currHeightY = getTerrainHeight(currPieceX + viewScrollX)

                    drawLine(
                        color = Color(0xFFFFF3E0),
                        start = Offset(prevPieceX, prevHeightY),
                        end = Offset(currPieceX, currHeightY),
                        strokeWidth = 3f
                    )
                }

                // Drawing Buggy Vehicle !!
                val buggyDisplayX = 150f
                val buggyDisplayY = buggyY

                // Rotate the driver buggy relative to current tilt angle
                // Drawing using coordinate translation
                val buggySaveAngle = tiltAngle
                val buggyRenderOffsetY = buggyDisplayY - 14f

                // Draw wheels
                // Rear Wheel (Offset -20f, +12)
                val wheelRadius = 13f
                // Rotate wheel relative to buggyX
                val wheelRotation = (buggyX / 14f)

                // Background Buggy Chassis
                // Draw rear wheel
                val rWheelCenterX = buggyDisplayX - 18f
                val rWheelCenterY = buggyDisplayY
                drawCircle(color = Color.Black, radius = wheelRadius, center = Offset(rWheelCenterX, rWheelCenterY))
                drawCircle(color = Color.LightGray, radius = 5f, center = Offset(rWheelCenterX, rWheelCenterY))

                // Front Wheel
                val fWheelCenterX = buggyDisplayX + 18f
                val fWheelCenterY = buggyDisplayY
                drawCircle(color = Color.Black, radius = wheelRadius, center = Offset(fWheelCenterX, fWheelCenterY))
                drawCircle(color = Color.LightGray, radius = 5f, center = Offset(fWheelCenterX, fWheelCenterY))

                // Chassis body bar linking
                drawLine(
                    color = Color.DarkGray,
                    start = Offset(rWheelCenterX, rWheelCenterY),
                    end = Offset(fWheelCenterX, fWheelCenterY),
                    strokeWidth = 8f
                )

                // Buggy Rollcage Outline (Using translation equivalent)
                // Draw buggy cockpit cab orange base
                drawRoundRect(
                    color = TacticalOrange,
                    topLeft = Offset(buggyDisplayX - 12f, buggyRenderOffsetY - 18f),
                    size = Size(24f, 16f),
                    cornerRadius = CornerRadius(5f, 5f)
                )

                // Cabin roll cage bars
                drawLine(
                    color = Color.White,
                    start = Offset(buggyDisplayX - 14f, buggyRenderOffsetY - 18f),
                    end = Offset(buggyDisplayX - 2f, buggyRenderOffsetY - 34f),
                    strokeWidth = 4f
                )
                drawLine(
                    color = Color.White,
                    start = Offset(buggyDisplayX - 2f, buggyRenderOffsetY - 34f),
                    end = Offset(buggyDisplayX + 14f, buggyRenderOffsetY - 14f),
                    strokeWidth = 4f
                )

                // Driver tiny head mock
                drawCircle(
                    color = SolarYellow,
                    radius = 6f,
                    center = Offset(buggyDisplayX - 2f, buggyRenderOffsetY - 22f)
                )

                // Red back flag
                drawLine(
                    color = Color.White,
                    start = Offset(buggyDisplayX - 14f, buggyRenderOffsetY),
                    end = Offset(buggyDisplayX - 18f, buggyRenderOffsetY - 26f),
                    strokeWidth = 2f
                )
                drawRect(
                    color = Color.Red,
                    topLeft = Offset(buggyDisplayX - 26f, buggyRenderOffsetY - 26f),
                    size = Size(8f, 6f)
                )
            }

            // Crash or Win alerts state overlay
            if (hasCrashed) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.8f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dangerous,
                            contentDescription = "Crashed",
                            tint = Color.Red,
                            modifier = Modifier.size(54.dp)
                        )
                        Text(
                            text = "BUGGY VEHICLE FLIPPED!",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Secure your vehicle rotation in air by pressing opposing action brakes to balance weight.",
                            color = SubtitleGrey,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = {
                                buggyX = 150f
                                buggyY = 200f
                                velocityX = 0f
                                velocityY = 0f
                                tiltAngle = 0f
                                hasCrashed = false
                                collectedCoins.clear()
                                coinsCollected = 0
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TacticalOrange)
                        ) {
                            Text("REDEPLOY VEHICLE")
                        }
                    }
                }
            }

            if (isWon) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = "Success",
                            tint = SolarYellow,
                            modifier = Modifier.size(54.dp)
                        )
                        Text(
                            text = "AIRDROP SUPPLY REACHED!",
                            color = WinnerGold,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic
                        )
                        Text(
                            text = "Bonus premium check secure: +1000 BP dynamic prize wallet update succeeded!",
                            color = OnSurfaceWhite,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = {
                                buggyX = 150f
                                buggyY = 200f
                                velocityX = 0f
                                velocityY = 0f
                                tiltAngle = 0f
                                isWon = false
                                collectedCoins.clear()
                                coinsCollected = 0
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SolarYellow)
                        ) {
                            Text("RIDE AGAIN", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Touch Control Panels Base
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SlateGrey)
                .padding(vertical = 16.dp, horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Action: Brake / Rotation Backwards
            Button(
                onClick = {},
                modifier = Modifier
                    .width(135.dp)
                    .height(65.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                brakePressed = true
                                tryAwaitRelease()
                                brakePressed = false
                            }
                        )
                    }
                    .testTag("brake_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Brake", tint = Color.White)
                    Text("BRAKE / TILT LH", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Tips Info
            Text(
                text = "HOLD GAS TO DRIVE\nRELEASE TO SLIDE",
                color = SubtitleGrey,
                fontSize = 8.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(90.dp)
            )

            // Right Action: Gas / Rotation Forwards
            Button(
                onClick = {},
                modifier = Modifier
                    .width(135.dp)
                    .height(65.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                gasPressed = true
                                tryAwaitRelease()
                                gasPressed = false
                            }
                        )
                    }
                    .testTag("gas_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Gas", tint = Color.White)
                    Text("GAS / TILT RH", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Generate smooth procedural sinusoidal rolling landscape for buggy racing
fun getTerrainHeight(x: Float): Float {
    return 330f +
            (sin(x * 0.0031f) * 110f).toFloat() +
            (sin(x * 0.0094f) * 35f).toFloat() +
            (cos(x * 0.015f) * 15f).toFloat()
}

// ==========================================
// GAME 2: SHADOW COMBAT DUEL (SHADOW FIGHT STYLE)
// ==========================================
enum class FighterState {
    IDLE,
    ATTACKING,
    BLOCKING,
    HIT
}

enum class EnemyBehavior {
    CALM,
    WARNING,  // Wind up flashing!
    STRIKING, // Active damage sweep
    RECOVERY
}

@Composable
fun ShadowDuelGameView(viewModel: CompanionViewModel) {
    var playerHp by remember { mutableStateOf(100f) }
    var enemyHp by remember { mutableStateOf(100f) }
    var playerState by remember { mutableStateOf(FighterState.IDLE) }
    var enemyState by remember { mutableStateOf(FighterState.IDLE) }
    var enemyBehavior by remember { mutableStateOf(EnemyBehavior.CALM) }

    var scoreStreak by remember { mutableStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }
    var isVictory by remember { mutableStateOf(false) }

    var shakeAmount by remember { mutableStateOf(0f) }
    var comboCounter by remember { mutableStateOf(0) }

    // Screen Slash Spark Overlay Effect
    var slashSparkActive by remember { mutableStateOf(false) }
    var attackLungeOffset by remember { mutableStateOf(0f) }

    val coroutineScope = rememberCoroutineScope()

    // Combat AI Core loop
    LaunchedEffect(key1 = isGameOver, key2 = isVictory) {
        if (!isGameOver && !isVictory) {
            while (true) {
                // Calm time
                delay((1000..2500).random().toLong())
                if (isGameOver || isVictory) break

                // Windup warning
                enemyBehavior = EnemyBehavior.WARNING
                delay(800)
                if (isGameOver || isVictory) break

                // Striking attack sequence
                enemyBehavior = EnemyBehavior.STRIKING
                enemyState = FighterState.ATTACKING

                // Check if player is NOT blocking
                delay(180)
                if (playerState != FighterState.BLOCKING) {
                    // Critical hit player!
                    playerHp -= 18f
                    shakeAmount = 15f
                    playerState = FighterState.HIT
                    comboCounter = 0
                } else {
                    // Blocked! Produce sparkles
                    shakeAmount = 3f
                    slashSparkActive = true
                }

                delay(200)
                slashSparkActive = false
                shakeAmount = 0f
                playerState = FighterState.IDLE
                enemyBehavior = EnemyBehavior.RECOVERY
                enemyState = FighterState.IDLE

                delay(600)
                enemyBehavior = EnemyBehavior.CALM

                // Verify health states
                if (playerHp <= 0) {
                    playerHp = 0f
                    isGameOver = true
                }
            }
        }
    }

    // Dynamic camera shaker physics
    LaunchedEffect(key1 = shakeAmount) {
        if (shakeAmount > 0) {
            delay(150)
            shakeAmount = 0f
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // HP bars
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(SlateGrey.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Player HP
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "REAPER (PLAYER)",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(playerHp / 100f)
                            .background(Color(0xFF2E7D32))
                    )
                }
            }

            // Strike Count Combo Tracker
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .background(Color.Black, CircleShape)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${comboCounter}x",
                    color = SolarYellow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Boss Ninja HP
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "SHADOW ELITE BOSS",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .fillMaxWidth(enemyHp / 100f)
                            .background(Color(0xFFC62828))
                    )
                }
            }
        }

        // Interactive Shadow Duel Screen Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .graphicsLayer(translationY = shakeAmount)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF3E1212), Color(0xFF0F1015)) // Blood red dusk gradient
                    )
                )
                .border(2.dp, SlateGrey),
            contentAlignment = Alignment.Center
        ) {
            // Moon Light Backdrop
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color(0xFFFFEBEE).copy(alpha = 0.15f),
                    center = Offset(size.width * 0.5f, size.height * 0.35f),
                    radius = 120f
                )
            }

            // Interactive Fighters Visual Drawing View
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp)
            ) {
                val groundLineY = size.height - 40f

                // Draw ground line
                drawLine(
                    color = Color.White.copy(alpha = 0.1f),
                    start = Offset(0f, groundLineY),
                    end = Offset(size.width, groundLineY),
                    strokeWidth = 3f
                )

                // Silhouette Player Fighter (Left Side + Lunge offset animation)
                val basePlayerX = size.width * 0.35f + attackLungeOffset
                val shadowColor = Color(0xFF16161C) // silhouette colors

                // Player body head
                drawCircle(
                    color = shadowColor,
                    radius = 22f,
                    center = Offset(basePlayerX, groundLineY - 140f)
                )

                // Torso
                drawLine(
                    color = shadowColor,
                    start = Offset(basePlayerX, groundLineY - 120f),
                    end = Offset(basePlayerX, groundLineY - 50f),
                    strokeWidth = 14f
                )

                // Legs
                drawLine(
                    color = shadowColor,
                    start = Offset(basePlayerX, groundLineY - 50f),
                    end = Offset(basePlayerX - 25f, groundLineY),
                    strokeWidth = 10f
                )
                drawLine(
                    color = shadowColor,
                    start = Offset(basePlayerX, groundLineY - 50f),
                    end = Offset(basePlayerX + 15f, groundLineY),
                    strokeWidth = 10f
                )

                // Arms (Drawing different pose matching state action triggers)
                if (playerState == FighterState.BLOCKING) {
                    // Raised shield visual defense posture
                    drawLine(
                        color = Color(0xFF00E676),
                        start = Offset(basePlayerX, groundLineY - 100f),
                        end = Offset(basePlayerX + 35f, groundLineY - 120f),
                        strokeWidth = 8f
                    )
                    // Shield glowing aura ring arc
                    drawCircle(
                        color = Color(0xFF00E676).copy(alpha = 0.3f),
                        radius = 45f,
                        center = Offset(basePlayerX + 35f, groundLineY - 110f),
                        style = Stroke(width = 4f)
                    )
                } else if (playerState == FighterState.ATTACKING) {
                    // Blade strike swing pose left-to-right slash arc
                    drawLine(
                        color = shadowColor,
                        start = Offset(basePlayerX, groundLineY - 100f),
                        end = Offset(basePlayerX + 55f, groundLineY - 130f),
                        strokeWidth = 8f
                    )
                    // Slash flash overlay arc
                    drawArc(
                        color = Color.White.copy(alpha = 0.7f),
                        startAngle = -45f,
                        sweepAngle = 100f,
                        useCenter = false,
                        topLeft = Offset(basePlayerX + 15f, groundLineY - 170f),
                        size = Size(80f, 80f),
                        style = Stroke(width = 6f)
                    )
                } else {
                    // Default combat rest stance pose
                    drawLine(
                        color = shadowColor,
                        start = Offset(basePlayerX, groundLineY - 100f),
                        end = Offset(basePlayerX + 25f, groundLineY - 70f),
                        strokeWidth = 8f
                    )
                    // In-pocket sword sheath silhouette lines
                    drawLine(
                        color = Color(0xFFD50000).copy(alpha = 0.6f),
                        start = Offset(basePlayerX - 10f, groundLineY - 70f),
                        end = Offset(basePlayerX + 20f, groundLineY - 100f),
                        strokeWidth = 4f
                    )
                }

                // Silhouette Enemy Fighter (Right Side)
                val baseEnemyX = size.width * 0.65f
                val enemyColor = if (enemyBehavior == EnemyBehavior.WARNING) {
                    Color(0xFFD50000) // Danger Red alerts flashing warning
                } else {
                    Color(0xFF101014)
                }

                // Enemy Body Head
                drawCircle(
                    color = enemyColor,
                    radius = 22f,
                    center = Offset(baseEnemyX, groundLineY - 140f)
                )

                // Torso
                drawLine(
                    color = enemyColor,
                    start = Offset(baseEnemyX, groundLineY - 120f),
                    end = Offset(baseEnemyX, groundLineY - 50f),
                    strokeWidth = 14f
                )

                // Legs
                drawLine(
                    color = enemyColor,
                    start = Offset(baseEnemyX, groundLineY - 50f),
                    end = Offset(baseEnemyX + 25f, groundLineY),
                    strokeWidth = 10f
                )
                drawLine(
                    color = enemyColor,
                    start = Offset(baseEnemyX, groundLineY - 50f),
                    end = Offset(baseEnemyX - 15f, groundLineY),
                    strokeWidth = 10f
                )

                // Enemy Slash swing visual representation
                if (enemyBehavior == EnemyBehavior.STRIKING) {
                    drawLine(
                        color = enemyColor,
                        start = Offset(baseEnemyX, groundLineY - 100f),
                        end = Offset(baseEnemyX - 60f, groundLineY - 110f),
                        strokeWidth = 8f
                    )
                    // Danger swipe slash red arc
                    drawArc(
                        color = Color.Red.copy(alpha = 0.8f),
                        startAngle = 135f,
                        sweepAngle = 100f,
                        useCenter = false,
                        topLeft = Offset(baseEnemyX - 95f, groundLineY - 160f),
                        size = Size(80f, 80f),
                        style = Stroke(width = 6f)
                    )
                } else {
                    drawLine(
                        color = enemyColor,
                        start = Offset(baseEnemyX, groundLineY - 100f),
                        end = Offset(baseEnemyX - 25f, groundLineY - 70f),
                        strokeWidth = 8f
                    )
                }

                // Spark sparks effect if blocked
                if (slashSparkActive) {
                    val midpointX = (basePlayerX + baseEnemyX) * 0.5f
                    val midpointY = groundLineY - 110f
                    drawCircle(
                        color = SolarYellow,
                        radius = 20f,
                        center = Offset(midpointX, midpointY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 8f,
                        center = Offset(midpointX, midpointY)
                    )
                }
            }

            // Warning Banner overlay triggers
            if (enemyBehavior == EnemyBehavior.WARNING) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 20.dp)
                        .background(Color.Red, RoundedCornerShape(4.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "⚠️ ENEMY FLASHING! GET READY TO CO-BLOCK CHIEF !!",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            if (isGameOver) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dangerous,
                            contentDescription = "Defeat",
                            tint = Color.Red,
                            modifier = Modifier.size(54.dp)
                        )
                        Text(
                            text = "SHADOW REAPER DIED IN COMBAT",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Learn to hold the TACTICAL GUARD SHIELD right when the Elite Ninja flashes red to deflect damages.",
                            color = SubtitleGrey,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = {
                                playerHp = 100f
                                enemyHp = 100f
                                comboCounter = 0
                                isGameOver = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("ENTER COMBAT COOP")
                        }
                    }
                }
            }

            if (isVictory) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Victory",
                            tint = SolarYellow,
                            modifier = Modifier.size(54.dp)
                        )
                        Text(
                            text = "CHICKEN DUEL VICTORY CHAMP!",
                            color = WinnerGold,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic
                        )
                        Text(
                            text = "Elite Shadow vanquished: Dynamic prize +500 BP credited into your character wallet!",
                            color = OnSurfaceWhite,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = {
                                playerHp = 100f
                                enemyHp = 100f
                                comboCounter = 0
                                isVictory = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SolarYellow)
                        ) {
                            Text("CHALLENGE AGAIN", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Duel Battle Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SlateGrey)
                .padding(vertical = 16.dp, horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Defense Block Shield modifier button
            Button(
                onClick = {},
                modifier = Modifier
                    .width(135.dp)
                    .height(65.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                playerState = FighterState.BLOCKING
                                tryAwaitRelease()
                                playerState = FighterState.IDLE
                            }
                        )
                    }
                    .testTag("shield_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = "Hold Block", tint = Color.White)
                    Text("TACTICAL DEFEN", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Quick tips overlay info
            Text(
                text = "BLOCK FLASHES\nATTACK EMPTY SPACES",
                color = SubtitleGrey,
                fontSize = 8.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(90.dp)
            )

            // Attack Strike Button
            Button(
                onClick = {
                    if (playerState != FighterState.BLOCKING && !isGameOver && !isVictory) {
                        // Swing animation sequence
                        playerState = FighterState.ATTACKING
                        attackLungeOffset = 45f

                        // Check if opponent is NOT attacking or is vulnerable
                        if (enemyBehavior != EnemyBehavior.STRIKING) {
                            enemyHp -= 12f
                            comboCounter++
                            scoreStreak++
                            shakeAmount = 8f

                            // Instant small coin reward
                            viewModel.bpCurrency.value += 15

                            if (enemyHp <= 0) {
                                enemyHp = 0f
                                isVictory = true
                            }
                        } else {
                            // hit wall, block penalty
                            comboCounter = 0
                        }

                        // Release offset delay
                        coroutineScope.launch {
                            delay(180)
                            attackLungeOffset = 0f
                            playerState = FighterState.IDLE
                        }
                    }
                },
                modifier = Modifier
                    .width(135.dp)
                    .height(65.dp)
                    .testTag("strike_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD50000)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.FlashOn, contentDescription = "Slash Duel Attack", tint = Color.White)
                    Text("LETHAL STRIKE", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

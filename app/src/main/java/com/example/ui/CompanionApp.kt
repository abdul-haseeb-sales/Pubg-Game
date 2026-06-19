package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.DropZone
import com.example.data.TacticalBuild
import com.example.data.Weapon
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CompanionApp(viewModel: CompanionViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val bpCurrency by viewModel.bpCurrency.collectAsStateWithLifecycle()
    val ucCurrency by viewModel.ucCurrency.collectAsStateWithLifecycle()
    val userLevel by viewModel.userLevel.collectAsStateWithLifecycle()
    val levelProgress by viewModel.levelProgress.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_scaffold"),
        containerColor = GunmetalDark,
        bottomBar = {
            CompanionBottomBar(
                currentTab = currentTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Radial Glowing Background matching the Immersive theme specification
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(SlateGrey, GunmetalDark),
                            center = Offset(500f, 600f),
                            radius = 1200f
                        )
                    )
            )

            // Dynamic Cross-fading transition between navigation tabs
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) with fadeOut(animationSpec = tween(200))
                },
                label = "tab_fade_transition"
            ) { targetTab ->
                when (targetTab) {
                    AppTab.LOBBY -> LobbyTab(viewModel, bpCurrency, ucCurrency, userLevel, levelProgress)
                    AppTab.ARMORY -> ArmoryTab(viewModel)
                    AppTab.MAP_TACTICS -> MapTacticsTab(viewModel)
                    AppTab.LOADOUTS -> LoadoutsTab(viewModel)
                    AppTab.STATS -> StatsTab(viewModel)
                }
            }
        }
    }
}

// ==========================================
// 1. TOP HUD BAR COMPOSABLE (LOBBY VIEW)
// ==========================================
@Composable
fun TopHUDBar(
    bpCurrency: Int,
    ucCurrency: Int,
    userLevel: Int,
    levelProgress: Float
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("top_hud_bar"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Person Info & Level Badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Rotating gold avatar border outline
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(TacticalOrange, SolarYellow)
                        )
                    )
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(GunmetalDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Combat Profile",
                        tint = SolarYellow,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Stats info
            Column {
                Text(
                    text = "SGT_REAPER_99",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Small progress bar indicator
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(levelProgress)
                                .clip(CircleShape)
                                .background(TacticalOrange)
                        )
                    }
                    Text(
                        text = "LV.$userLevel",
                        color = SolarYellow,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Wallet Currencies (BP and UC)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // BP Info
            Row(
                modifier = Modifier
                    .background(SlateGrey, RoundedCornerShape(16.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "BP",
                    color = SolarYellow,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = String.format("%,d", bpCurrency),
                    color = OnSurfaceWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // UC Info
            Row(
                modifier = Modifier
                    .background(SlateGrey, RoundedCornerShape(16.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "UC",
                    color = Color(0xFF4FC3F7), // Blue UC currency color
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "$ucCurrency",
                    color = OnSurfaceWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

// ==========================================
// 1. LOBBY TAB WINDOW
// ==========================================
@Composable
fun LobbyTab(
    viewModel: CompanionViewModel,
    bp: Int,
    uc: Int,
    lvl: Int,
    prog: Float
) {
    val selectedMap by viewModel.selectedMap.collectAsStateWithLifecycle()
    val isMatchmaking by viewModel.isMatchmaking.collectAsStateWithLifecycle()
    val matchmakingMsg by viewModel.matchmakingStatusText.collectAsStateWithLifecycle()
    val matchmakingTime by viewModel.matchmakingTime.collectAsStateWithLifecycle()

    var showMapDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("lobby_tab")
    ) {
        // Status Top Badge
        TopHUDBar(bp, uc, lvl, prog)

        // Character Showcase space
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Ambient base glow
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
                    .width(180.dp)
                    .height(24.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(TacticalOrange.copy(alpha = 0.25f), Color.Transparent)
                        )
                    )
                    .blur(16.dp)
            )

            // Character Stand Frame Mock
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .width(150.dp)
                    .height(260.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, SlateGrey.copy(alpha = 0.45f))
                        ),
                        shape = RoundedCornerShape(topStartPercent = 40, topEndPercent = 40)
                    )
                    .border(
                        1.dp,
                        Color.White.copy(alpha = 0.05f),
                        RoundedCornerShape(topStartPercent = 40, topEndPercent = 40)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Soldier stylized silhouette placeholder
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Tactical Level 3 Armor Icon",
                        tint = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.size(90.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "SOLDIER INSTANCE ACTIVE",
                        color = Color.White.copy(alpha = 0.3f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Laser dot accent pointer
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(bottom = 20.dp)
                        .size(8.dp)
                        .background(Color(0xFFFF5252), CircleShape)
                        .blur(1.dp)
                )
            }

            // Left HUD action item
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(SlateGrey.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .clickable { viewModel.selectTab(AppTab.STATS) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Season Ranks",
                        tint = SolarYellow,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(SlateGrey.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .clickable { viewModel.selectTab(AppTab.LOADOUTS) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GridOn,
                        contentDescription = "Tactical setups",
                        tint = OnSurfaceWhite,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Right HUD action item (Mail/Social Hub triggers)
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(SlateGrey.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Box {
                        Icon(
                            imageVector = Icons.Default.Mail,
                            contentDescription = "System log Mail",
                            tint = OnSurfaceWhite,
                            modifier = Modifier.size(22.dp)
                        )
                        // Alert circular indicator
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(8.dp)
                                .background(Color.Red, CircleShape)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(SlateGrey.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .clickable { viewModel.selectTab(AppTab.MAP_TACTICS) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Map tactical drop coordinates",
                        tint = TacticalOrange,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Bottom Controls Area
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Match Map Selection Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateGrey.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .clickable { showMapDialog = true }
                    .padding(12.dp)
                    .testTag("map_picker_card"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(GunmetalDark, RoundedCornerShape(8.dp))
                            .border(1.dp, TacticalOrange.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Selected Map",
                            tint = TacticalOrange,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = selectedMap.uppercase(),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "CLASSIC • TPP • SQUAD",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(
                    onClick = { showMapDialog = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Change Server Map",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Big tactical Start Button
            Button(
                onClick = { viewModel.startSimulatedMatchmaking() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_match_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = if (isMatchmaking) {
                                    listOf(Color(0xFF37474F), Color(0xFF263238))
                                } else {
                                    listOf(TacticalOrange, SolarYellow)
                                }
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isMatchmaking) "CANCEL MATCH" else "START MATCH",
                            color = if (isMatchmaking) OnSurfaceWhite else Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            letterSpacing = 1.sp
                        )
                        Icon(
                            imageVector = if (isMatchmaking) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Action play",
                            tint = if (isMatchmaking) OnSurfaceWhite else Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Matchmaking HUD Overlay
        if (isMatchmaking) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                    .border(1.dp, SolarYellow.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "STATUS: $matchmakingMsg",
                            color = SolarYellow,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        LinearProgressIndicator(
                            color = TacticalOrange,
                            trackColor = Color.White.copy(alpha = 0.1f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp)
                                .height(3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "0:0$matchmakingTime",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }

    // Modal Map Selection dialog
    if (showMapDialog) {
        AlertDialog(
            onDismissRequest = { showMapDialog = false },
            title = {
                Text(
                    text = "SELECT CURRENT COMBAT AREA",
                    color = SolarYellow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Erangel choice
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (selectedMap == "Erangel") SlateGrey else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                1.dp,
                                if (selectedMap == "Erangel") TacticalOrange else Color.White.copy(alpha = 0.05f),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                viewModel.setMap("Erangel")
                                showMapDialog = false
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forest,
                            contentDescription = "Grasslands Map",
                            tint = if (selectedMap == "Erangel") TacticalOrange else OnSurfaceWhite,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ERANGEL (Classic)",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Lush grasslands and heavy forests.",
                                color = SubtitleGrey,
                                fontSize = 9.sp
                            )
                        }
                    }

                    // Miramar choice
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (selectedMap == "Miramar") SlateGrey else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                1.dp,
                                if (selectedMap == "Miramar") TacticalOrange else Color.White.copy(alpha = 0.05f),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                viewModel.setMap("Miramar")
                                showMapDialog = false
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = "Desert Map",
                            tint = if (selectedMap == "Miramar") TacticalOrange else OnSurfaceWhite,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "MIRAMAR (Classic)",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Vast desert layout with urban compounds.",
                                color = SubtitleGrey,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showMapDialog = false }) {
                    Text("CLOSE", color = OnSurfaceWhite)
                }
            },
            containerColor = GunmetalDark
        )
    }
}

// ==========================================
// 2. ARMORY INVENTORY & BUILDER TAB Window
// ==========================================
@Composable
fun ArmoryTab(viewModel: CompanionViewModel) {
    val weapons = viewModel.allWeapons
    val selectedWeapon by viewModel.selectedWeapon.collectAsStateWithLifecycle()
    val searchQuery by viewModel.weaponSearchQuery.collectAsStateWithLifecycle()

    val selectedScope by viewModel.selectedScope.collectAsStateWithLifecycle()
    val selectedMuzzle by viewModel.selectedMuzzle.collectAsStateWithLifecycle()
    val selectedGrip by viewModel.selectedGrip.collectAsStateWithLifecycle()
    val selectedMagazine by viewModel.selectedMagazine.collectAsStateWithLifecycle()

    val modifiedStats = viewModel.calculateModifiedStats(selectedWeapon)

    var currentCategoryFilter by remember { mutableStateOf("All") }
    var showSaveBuildSheet by remember { mutableStateOf(false) }

    // Save build form states
    var buildName by remember { mutableStateOf("") }
    var secondaryWeaponChoice by remember { mutableStateOf("") }
    var buildNotes by remember { mutableStateOf("") }

    val filteredWeapons = weapons.filter {
        (currentCategoryFilter == "All" || it.category.equals(currentCategoryFilter, ignoreCase = true)) &&
                it.name.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("armory_tab"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        // Tab header
        item {
            Column {
                Text(
                    text = "COMBAT CUSTOMIZATIONS",
                    color = SolarYellow,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Select weapon assets and attachments to calculate custom tactical ratings.",
                    color = SubtitleGrey,
                    fontSize = 10.sp
                )
            }
        }

        // Interactive Categories Scroll list
        item {
            val categories = listOf("All", "Assault Rifle", "Sniper Rifle", "SMG", "Shotgun")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    Box(
                        modifier = Modifier
                            .background(
                                if (currentCategoryFilter == category) TacticalOrange else SlateGrey,
                                RoundedCornerShape(20.dp)
                            )
                            .border(
                                1.dp,
                                if (currentCategoryFilter == category) SolarYellow else Color.White.copy(alpha = 0.05f),
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { currentCategoryFilter = category }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = category.uppercase(),
                            color = if (currentCategoryFilter == category) Color.Black else OnSurfaceWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Horizontal weapons selector scroll list
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredWeapons) { wep ->
                    val isSelected = wep.id == selectedWeapon.id
                    Box(
                        modifier = Modifier
                            .width(135.dp)
                            .background(
                                if (isSelected) SlateGrey else SlateGrey.copy(alpha = 0.5f),
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                1.dp,
                                if (isSelected) TacticalOrange else Color.White.copy(alpha = 0.05f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.selectWeapon(wep) }
                            .padding(10.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .background(GunmetalDark, RoundedCornerShape(6.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (wep.category == "Sniper Rifle") Icons.Default.Troubleshoot else Icons.Default.PowerSettingsNew,
                                    contentDescription = "Weapon design",
                                    tint = if (isSelected) SolarYellow else SubtitleGrey,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = wep.name,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = wep.ammoType,
                                color = if (isSelected) TacticalOrange else SubtitleGrey,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Core Interactive Info Panel & Custom sliders
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateGrey, RoundedCornerShape(14.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = selectedWeapon.name.uppercase(),
                    color = SolarYellow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = selectedWeapon.description,
                    color = OnSurfaceWhite.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Divider(
                    color = Color.White.copy(alpha = 0.05f),
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                // Stats Comparison Bars
                StatSliderRow("BULLET DAMAGE", modifiedStats["Damage"] ?: 0f, selectedWeapon.damage)
                StatSliderRow("FIRE RATE RATIO", modifiedStats["FireRate"] ?: 0f, selectedWeapon.fireRate)
                StatSliderRow("STABILITY/RECOIL", modifiedStats["Stability"] ?: 0f, selectedWeapon.stability)
                StatSliderRow("EFFECTIVE range", modifiedStats["Range"] ?: 0f, selectedWeapon.range)

                Spacer(modifier = Modifier.height(10.dp))

                // Detail Specs Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AmmoSpecLabel("CALIBER", selectedWeapon.ammoType)
                    AmmoSpecLabel("VELOCITY", selectedWeapon.bulletVelocity)
                    AmmoSpecLabel("RECOMMENDED MAG", selectedWeapon.optimalMag)
                }
            }
        }

        // Tactical Attachment selectors
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "ATTACHMENT CONFIGURATOR",
                    color = SolarYellow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    fontFamily = FontFamily.Monospace
                )

                // Scopes Row selector
                AttachmentBlock(
                    category = "Scope",
                    current = selectedScope,
                    options = listOf("No Scope", "2x Aimpoint", "4x ACOG", "8x CQB Scope"),
                    onSelect = { viewModel.selectAttachment("Scope", it) }
                )

                // Muzzle Row selector
                AttachmentBlock(
                    category = "Muzzle",
                    current = selectedMuzzle,
                    options = listOf("No Muzzle", "Compensator", "Flash Hider", "Suppressor"),
                    onSelect = { viewModel.selectAttachment("Muzzle", it) }
                )

                // Grip Row selector
                AttachmentBlock(
                    category = "Grip",
                    current = selectedGrip,
                    options = listOf("No Grip", "Vertical Foregrip", "Angled Grip", "Light Grip"),
                    onSelect = { viewModel.selectAttachment("Grip", it) }
                )

                // Magazine Row selector
                AttachmentBlock(
                    category = "Magazine",
                    current = selectedMagazine,
                    options = listOf("No Magazine", "Extended Mag", "Quickdraw Mag", "Ext. Quickdraw Mag"),
                    onSelect = { viewModel.selectAttachment("Magazine", it) }
                )
            }
        }

        // Save customized loadout trigger button representation
        item {
            Button(
                onClick = {
                    buildName = "${selectedWeapon.name} custom tac"
                    secondaryWeaponChoice = ""
                    buildNotes = ""
                    showSaveBuildSheet = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_loadout_trigger_button"),
                colors = ButtonDefaults.buttonColors(containerColor = TacticalOrange)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save Setup to Database"
                    )
                    Text(
                        text = "SAVE TAC SETUP",
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }

    // Modal save dialog sheet
    if (showSaveBuildSheet) {
        AlertDialog(
            onDismissRequest = { showSaveBuildSheet = false },
            title = {
                Text(
                    text = "SAVE TAC SETUP TO ROOM",
                    color = SolarYellow,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = buildName,
                        onValueChange = { buildName = it },
                        label = { Text("Setup Nickname") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TacticalOrange,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                            focusedLabelColor = TacticalOrange
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("build_nickname_input")
                    )

                    OutlinedTextField(
                        value = secondaryWeaponChoice,
                        onValueChange = { secondaryWeaponChoice = it },
                        label = { Text("Secondary Weapon (e.g. UMP45, DBS)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TacticalOrange,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                            focusedLabelColor = TacticalOrange
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("secondary_weapon_input")
                    )

                    OutlinedTextField(
                        value = buildNotes,
                        onValueChange = { buildNotes = it },
                        label = { Text("Tactical notes") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TacticalOrange,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                            focusedLabelColor = TacticalOrange
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("build_notes_input")
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.saveCustomBuild(buildName, secondaryWeaponChoice, buildNotes)
                        showSaveBuildSheet = false
                    },
                    modifier = Modifier.testTag("confirm_save_build_button")
                ) {
                    Text("SAVE TO ARCHIVE", color = SolarYellow, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveBuildSheet = false }) {
                    Text("DISMISS", color = OnSurfaceWhite)
                }
            },
            containerColor = GunmetalDark
        )
    }
}

@Composable
fun AttachmentBlock(
    category: String,
    current: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    Column {
        Text(
            text = category.uppercase(),
            color = OnSurfaceWhite.copy(alpha = 0.4f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(options) { opt ->
                val active = current == opt
                Box(
                    modifier = Modifier
                        .background(
                            if (active) TacticalOrange.copy(alpha = 0.2f) else SlateGrey,
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            if (active) TacticalOrange else Color.White.copy(alpha = 0.05f),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onSelect(opt) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = opt,
                        color = if (active) TacticalOrange else OnSurfaceWhite,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun StatSliderRow(title: String, currentValue: Float, baseValue: Float) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = OnSurfaceWhite,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "${baseValue.toInt()}",
                    color = SubtitleGrey,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
                if (currentValue != baseValue) {
                    val diff = currentValue - baseValue
                    val sign = if (diff > 0) "+" else ""
                    val color = if (diff > 0) Color(0xFF00E676) else Color(0xFFFF1744)
                    Text(
                        text = "($sign${diff.toInt()})",
                        color = color,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(currentValue / 100f)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            colors = if (currentValue >= baseValue) {
                                listOf(TacticalOrange, SolarYellow)
                            } else {
                                listOf(Color(0xFFE53935), TacticalOrange)
                            }
                        )
                    )
            )
        }
    }
}

@Composable
fun AmmoSpecLabel(title: String, value: String) {
    Column(modifier = Modifier.width(90.dp)) {
        Text(
            text = title,
            color = OnSurfaceWhite.copy(alpha = 0.35f),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            color = OnSurfaceWhite,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ==========================================
// 3. MAP STRATEGY & DROPZONES VISUALIZER TAB
// ==========================================
@Composable
fun MapTacticsTab(viewModel: CompanionViewModel) {
    val selectedMap by viewModel.selectedMap.collectAsStateWithLifecycle()
    val activeDropZone by viewModel.selectedDropZone.collectAsStateWithLifecycle()
    val dropZones = viewModel.getDropZonesForSelectedMap(selectedMap)

    val pStartX by viewModel.planeStartX.collectAsStateWithLifecycle()
    val pStartY by viewModel.planeStartY.collectAsStateWithLifecycle()
    val pEndX by viewModel.planeEndX.collectAsStateWithLifecycle()
    val pEndY by viewModel.planeEndY.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("map_tactics_tab"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TACTICAL RADAR DEPLOYMENT",
                        color = SolarYellow,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Current: ${selectedMap.uppercase()} Battleground grid map.",
                        color = SubtitleGrey,
                        fontSize = 10.sp
                    )
                }

                Button(
                    onClick = { viewModel.randomizePlanePath() },
                    colors = ButtonDefaults.buttonColors(containerColor = SlateGrey),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("REGEN PATH", color = SolarYellow, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Tactical drawing board canvas representing the Battleground Map
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.1f)
                    .background(SlateGrey, RoundedCornerShape(16.dp))
                    .border(1.dp, SolarYellow.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .testTag("tactical_map_canvas")
            ) {
                // Interactive Grid Drawing Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(dropZones) {
                            detectTapGestures { offset ->
                                // Find closest hotspot clicked in range
                                val canvasWidth = size.width
                                val canvasHeight = size.height

                                val clickedZone = dropZones.minByOrNull { zone ->
                                    val targetOffset = Offset(
                                        (zone.x / 100f) * canvasWidth,
                                        (zone.y / 100f) * canvasHeight
                                    )
                                    (offset - targetOffset).getDistanceSquared()
                                }

                                clickedZone?.let { zone ->
                                    // Make sure it's within 60dp touch boundaries
                                    val targetOffset = Offset(
                                        (zone.x / 100f) * canvasWidth,
                                        (zone.y / 100f) * canvasHeight
                                    )
                                    if ((offset - targetOffset).getDistance() < 120f) {
                                        viewModel.selectDropZone(zone)
                                    }
                                }
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height

                    // Grid layout matrix drawing
                    val squareDim = 10
                    for (i in 1 until squareDim) {
                        val colX = (w / squareDim) * i
                        drawLine(
                            color = Color.White.copy(alpha = 0.05f),
                            start = Offset(colX, 0f),
                            end = Offset(colX, h),
                            strokeWidth = 1f
                        )
                        val rowY = (h / squareDim) * i
                        drawLine(
                            color = Color.White.copy(alpha = 0.05f),
                            start = Offset(0f, rowY),
                            end = Offset(w, rowY),
                            strokeWidth = 1f
                        )
                    }

                    // Draw flight plane path trajectory (Warning Golden dash line)
                    val flightStart = Offset((pStartX / 100f) * w, (pStartY / 100f) * h)
                    val flightEnd = Offset((pEndX / 100f) * w, (pEndY / 100f) * h)

                    drawLine(
                        color = SolarYellow,
                        start = flightStart,
                        end = flightEnd,
                        strokeWidth = 4f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 15f), 0f)
                    )

                    // Flight launch markers
                    drawCircle(
                        color = TacticalOrange,
                        radius = 12f,
                        center = flightStart
                    )

                    drawCircle(
                        color = Color.White,
                        radius = 6f,
                        center = flightEnd
                    )

                    // Draw DropZones coordinates circles
                    dropZones.forEach { zone ->
                        val locationOffset = Offset((zone.x / 100f) * w, (zone.y / 100f) * h)
                        val isHighlighted = activeDropZone?.id == zone.id

                        // Pulsing circle overlay for selected dropzone
                        if (isHighlighted) {
                            drawCircle(
                                color = TacticalOrange.copy(alpha = 0.35f),
                                radius = 32f,
                                center = locationOffset
                            )
                        }

                        // Anchor military crosshair
                        drawCircle(
                            color = if (isHighlighted) SolarYellow else Color.White.copy(alpha = 0.7f),
                            radius = if (isHighlighted) 12f else 8f,
                            center = locationOffset
                        )

                        drawCircle(
                            color = GunmetalDark,
                            radius = if (isHighlighted) 6f else 4f,
                            center = locationOffset
                        )
                    }
                }

                // Plane label overlay
                Text(
                    text = "► MILITARY HERCULES PATH",
                    color = SolarYellow,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )

                // Render dynamic text on top of canvas for clarity
                dropZones.forEach { zone ->
                    Box(
                        modifier = Modifier
                            .offset(
                                x = ((zone.x / 100f) * 320).dp,
                                y = (((zone.y / 100f) * 310) - 20).dp
                            )
                    ) {
                        Text(
                            text = zone.name.split(" ").firstOrNull() ?: "",
                            color = if (activeDropZone?.id == zone.id) SolarYellow else Color.White.copy(alpha = 0.4f),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(2.dp))
                                .padding(horizontal = 3.dp)
                        )
                    }
                }
            }
        }

        // Active Drop Zone details card
        item {
            activeDropZone?.let { zone ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SlateGrey, RoundedCornerShape(14.dp))
                        .border(1.dp, TacticalOrange.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = zone.name.uppercase(),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )

                        Box(
                            modifier = Modifier
                                .background(TacticalOrange.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = zone.riskLevel.uppercase(),
                                color = TacticalOrange,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = zone.details,
                        color = OnSurfaceWhite.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "LOOT INDEX",
                                color = OnSurfaceWhite.copy(alpha = 0.35f),
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = zone.lootGrade,
                                color = SolarYellow,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column {
                            Text(
                                text = "CAR/VEHICLE RATE",
                                color = OnSurfaceWhite.copy(alpha = 0.35f),
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "${zone.vehicleSpawnRate}% Spawn Chance",
                                color = OnSurfaceWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Divider(
                        color = Color.White.copy(alpha = 0.05f),
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    // Tactical Survivor Tip
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Tips Icon",
                            tint = SolarYellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = zone.tacticalTip,
                            color = SolarYellow,
                            fontSize = 10.sp,
                            fontStyle = FontStyle.Italic,
                            lineHeight = 13.sp
                        )
                    }
                }
            } ?: Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tap on glowing locations to reveal strategy logs.",
                    color = SubtitleGrey,
                    fontSize = 11.sp
                )
            }
        }
    }
}

// ==========================================
// 4. SAVED ROOM BUILDS LOOT TAB Window
// ==========================================
@Composable
fun LoadoutsTab(viewModel: CompanionViewModel) {
    val savedBuilds by viewModel.savedBuilds.collectAsStateWithLifecycle(initialValue = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("loadouts_tab"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        item {
            Column {
                Text(
                    text = "SAVED TACTICAL DEPLOYMENTS",
                    color = SolarYellow,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Saved configurations registered in Room SQLite DB.",
                    color = SubtitleGrey,
                    fontSize = 10.sp
                )
            }
        }

        if (savedBuilds.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory,
                            contentDescription = "Empty locker",
                            tint = Color.White.copy(alpha = 0.15f),
                            modifier = Modifier.size(68.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Locker empty. Setup weapons first in the ARMORY configuration tool.",
                            color = SubtitleGrey,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(savedBuilds, key = { it.id }) { build ->
                SavedBuildCard(
                    build = build,
                    onDelete = { viewModel.deleteTacticalBuild(build) }
                )
            }
        }
    }
}

@Composable
fun SavedBuildCard(
    build: TacticalBuild,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SlateGrey, RoundedCornerShape(12.dp))
            .border(1.dp, SolarYellow.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = build.name.uppercase(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "PRIMARY: ${build.primaryWeapon}",
                    color = TacticalOrange,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Trash build",
                    tint = Color.Red.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Grid attachments summary labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AttachmentLabelBadge("Scope", build.primaryScope)
            AttachmentLabelBadge("Muzzle", build.primaryMuzzle)
            AttachmentLabelBadge("Grip", build.primaryGrip)
            AttachmentLabelBadge("Mag", build.primaryMagazine)
        }

        if (build.secondaryWeapon.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "SECONDARY SECURED:",
                    color = OnSurfaceWhite.copy(alpha = 0.4f),
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = build.secondaryWeapon.uppercase(),
                    color = OnSurfaceWhite,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (build.notes.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Notes: ${build.notes}",
                color = SubtitleGrey,
                fontSize = 9.sp,
                fontStyle = FontStyle.Italic,
                lineHeight = 12.sp
            )
        }
    }
}

@Composable
fun RowScope.AttachmentLabelBadge(category: String, value: String) {
    Box(
        modifier = Modifier
            .weight(1f)
            .background(GunmetalDark, RoundedCornerShape(6.dp))
            .padding(horizontal = 4.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = category.uppercase(),
                color = OnSurfaceWhite.copy(alpha = 0.35f),
                fontSize = 7.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = value,
                color = OnSurfaceWhite,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ==========================================
// 5. COMBAT STATS SEASON EVALUATION WINDOW
// ==========================================
@Composable
fun StatsTab(viewModel: CompanionViewModel) {
    val kills by viewModel.killsCount.collectAsStateWithLifecycle()
    val wins by viewModel.winsCount.collectAsStateWithLifecycle()
    val kd by viewModel.kdRatio.collectAsStateWithLifecycle()
    val matches by viewModel.matchesPlayed.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("stats_tab"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        item {
            Column {
                Text(
                    text = "BATTLE RATING FEEDBACK",
                    color = SolarYellow,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Historic server performance logs compiled for SGT_REAPER_99.",
                    color = SubtitleGrey,
                    fontSize = 10.sp
                )
            }
        }

        // Large highlight performance rating card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateGrey, RoundedCornerShape(16.dp))
                    .border(1.dp, SolarYellow.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Stars,
                    contentDescription = "Conqueror rating",
                    tint = SolarYellow,
                    modifier = Modifier.size(52.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "RANK TIER: ACEMASTER VII",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "TOP 1.2% REGIONAL BATTLE HERO",
                    color = TacticalOrange,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                Divider(
                    color = Color.White.copy(alpha = 0.05f),
                    modifier = Modifier.padding(vertical = 14.dp)
                )

                // Stats rows
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    BigNumbersBadge("K/D RATIO", String.format("%.2f", kd))
                    BigNumbersBadge("MATCHES", "$matches")
                    BigNumbersBadge("WIN RATIO", "${((wins.toFloat() / matches) * 100).toInt()}%")
                    BigNumbersBadge("CHICKEN DINNERS", "$wins")
                }
            }
        }

        // Rank achievements progress
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateGrey, RoundedCornerShape(14.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "TACTICAL STRENGTH SUMMARY",
                    color = SolarYellow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                AchievementRow("Pochinki Gladiator", "Close quarters multi-kill threat.", 95)
                AchievementRow("Bridge Blocker camper", "Successfully choke-point bridges.", 80)
                AchievementRow("Sniper headshot assassin", "Direct bullet helmet impacts.", 65)
                AchievementRow("Unarmed Chicken Dinner", "Survival rate without primary bullet deployment.", 12)
            }
        }
    }
}

@Composable
fun BigNumbersBadge(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = SubtitleGrey,
            fontSize = 8.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            color = OnSurfaceWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun AchievementRow(title: String, subtitle: String, percentile: Int) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = SubtitleGrey,
                    fontSize = 8.sp
                )
            }
            Text(
                text = "$percentile%",
                color = TacticalOrange,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentile / 100f)
                    .clip(CircleShape)
                    .background(TacticalOrange)
            )
        }
    }
}

// ==========================================
// CENTRAL BOTTOM NAVIGATION BAR COMPOSABLE
// ==========================================
@Composable
fun CompanionBottomBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("lobby_bottom_nav_bar"),
        containerColor = Color(0xFF16191E),
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentTab == AppTab.LOBBY,
            onClick = { onTabSelected(AppTab.LOBBY) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Lobby Home",
                    tint = if (currentTab == AppTab.LOBBY) TacticalOrange else SubtitleGrey
                )
            },
            label = {
                Text(
                    text = "LOBBY",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = TacticalOrange.copy(alpha = 0.15f),
                selectedIconColor = TacticalOrange,
                selectedTextColor = TacticalOrange,
                unselectedIconColor = SubtitleGrey,
                unselectedTextColor = SubtitleGrey
            ),
            modifier = Modifier.testTag("tab_lobby")
        )

        NavigationBarItem(
            selected = currentTab == AppTab.ARMORY,
            onClick = { onTabSelected(AppTab.ARMORY) },
            icon = {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = "Weapons Armory Shop",
                    tint = if (currentTab == AppTab.ARMORY) TacticalOrange else SubtitleGrey
                )
            },
            label = {
                Text(
                    text = "SHOP",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = TacticalOrange.copy(alpha = 0.15f),
                selectedIconColor = TacticalOrange,
                selectedTextColor = TacticalOrange,
                unselectedIconColor = SubtitleGrey,
                unselectedTextColor = SubtitleGrey
            ),
            modifier = Modifier.testTag("tab_shop")
        )

        NavigationBarItem(
            selected = currentTab == AppTab.MAP_TACTICS,
            onClick = { onTabSelected(AppTab.MAP_TACTICS) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Inventory2,
                    contentDescription = "Maps Tactics Gear",
                    tint = if (currentTab == AppTab.MAP_TACTICS) TacticalOrange else SubtitleGrey
                )
            },
            label = {
                Text(
                    text = "GEAR",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = TacticalOrange.copy(alpha = 0.15f),
                selectedIconColor = TacticalOrange,
                selectedTextColor = TacticalOrange,
                unselectedIconColor = SubtitleGrey,
                unselectedTextColor = SubtitleGrey
            ),
            modifier = Modifier.testTag("tab_gear")
        )

        NavigationBarItem(
            selected = currentTab == AppTab.LOADOUTS,
            onClick = { onTabSelected(AppTab.LOADOUTS) },
            icon = {
                Icon(
                    imageVector = Icons.Default.MilitaryTech,
                    contentDescription = "Custom Builds saved Locker",
                    tint = if (currentTab == AppTab.LOADOUTS) TacticalOrange else SubtitleGrey
                )
            },
            label = {
                Text(
                    text = "LOCKER",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = TacticalOrange.copy(alpha = 0.15f),
                selectedIconColor = TacticalOrange,
                selectedTextColor = TacticalOrange,
                unselectedIconColor = SubtitleGrey,
                unselectedTextColor = SubtitleGrey
            ),
            modifier = Modifier.testTag("tab_locker")
        )

        NavigationBarItem(
            selected = currentTab == AppTab.STATS,
            onClick = { onTabSelected(AppTab.STATS) },
            icon = {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Seasons rating charts",
                    tint = if (currentTab == AppTab.STATS) TacticalOrange else SubtitleGrey
                )
            },
            label = {
                Text(
                    text = "SEASON",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = TacticalOrange.copy(alpha = 0.15f),
                selectedIconColor = TacticalOrange,
                selectedTextColor = TacticalOrange,
                unselectedIconColor = SubtitleGrey,
                unselectedTextColor = SubtitleGrey
            ),
            modifier = Modifier.testTag("tab_season")
        )
    }
}

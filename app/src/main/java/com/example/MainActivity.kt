package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.*
import com.example.ui.*
import com.example.ui.theme.*
import coil.compose.SubcomposeAsyncImage
import androidx.compose.ui.layout.ContentScale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    val viewModel: AyniViewModel = viewModel()
    val currentScreen by viewModel.currentScreen.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AyniHeader(viewModel = viewModel)
        },
        bottomBar = {
            AyniBottomNavigation(viewModel = viewModel)
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftCream)
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    if (targetState == "profile" || targetState == "chat_liz" || targetState == "dashboard") {
                        (slideInHorizontally { width -> width / 4 } + fadeIn(animationSpec = tween(300))) togetherWith
                            (slideOutHorizontally { width -> -width / 4 } + fadeOut(animationSpec = tween(300)))
                    } else if (initialState == "profile" || initialState == "chat_liz" || initialState == "dashboard") {
                        (slideInHorizontally { width -> -width / 4 } + fadeIn(animationSpec = tween(300))) togetherWith
                            (slideOutHorizontally { width -> width / 4 } + fadeOut(animationSpec = tween(300)))
                    } else {
                        fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200))
                    }
                },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    "home" -> HomeScreen(viewModel = viewModel)
                    "search" -> SearchScreen(viewModel = viewModel)
                    "profile" -> ProviderProfileScreen(viewModel = viewModel)
                    "chat_liz" -> ChatLizScreen(viewModel = viewModel)
                    "support_bot" -> SupportBotScreen(viewModel = viewModel)
                    "dashboard" -> DashboardScreen(viewModel = viewModel)
                    "login" -> LoginRegisterScreen(viewModel = viewModel)
                }
            }

            // Real-time Push Notification Banner Overlay
            NotificationBannerOverlay(viewModel = viewModel)
        }
    }
}

/**
 * Universal app header with centred logo and navigation triggers
 */
@Composable
fun AyniHeader(viewModel: AyniViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isHome = currentScreen == "home"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back button if not home
                if (!isHome) {
                    IconButton(
                        onClick = { viewModel.goBack() },
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Terracotta
                        )
                    }
                } else {
                    // Profile/Login simulation button
                    IconButton(
                        onClick = { viewModel.navigateWithHistory("login") },
                        modifier = Modifier.testTag("user_avatar_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Cuenta",
                            tint = DarkBrown,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Center large Logo
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    AyniLogo(
                        showSlogan = isHome,
                        sizeScale = if (isHome) 0.65f else 0.5f,
                        modifier = Modifier.clickable { viewModel.navigateTo("home") }
                    )
                }

                // Header Notification Icon
                IconButton(
                    onClick = { viewModel.navigateWithHistory("dashboard") },
                    modifier = Modifier.testTag("header_notifications")
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificaciones",
                        tint = Gold
                    )
                }
            }
        }
    }
}

/**
 * Modern Bottom Navigation Bar conforming to Safe Area Insets and dynamic styling
 */
@Composable
fun AyniBottomNavigation(viewModel: AyniViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        NavigationBarItem(
            selected = currentScreen == "home",
            onClick = { viewModel.navigateTo("home") },
            label = { Text("Inicio") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Terracotta,
                selectedTextColor = Terracotta,
                indicatorColor = LightTerracotta,
                unselectedIconColor = TextLight,
                unselectedTextColor = TextLight
            ),
            modifier = Modifier.testTag("nav_home")
        )

        NavigationBarItem(
            selected = currentScreen == "search" || currentScreen == "profile",
            onClick = { viewModel.navigateTo("search") },
            label = { Text("Buscar") },
            icon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Terracotta,
                selectedTextColor = Terracotta,
                indicatorColor = LightTerracotta,
                unselectedIconColor = TextLight,
                unselectedTextColor = TextLight
            ),
            modifier = Modifier.testTag("nav_search")
        )

        NavigationBarItem(
            selected = currentScreen == "dashboard",
            onClick = { viewModel.navigateTo("dashboard") },
            label = { Text("Escrow") },
            icon = { Icon(Icons.Default.OfflineBolt, contentDescription = "Escrow") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Terracotta,
                selectedTextColor = Terracotta,
                indicatorColor = LightTerracotta,
                unselectedIconColor = TextLight,
                unselectedTextColor = TextLight
            ),
            modifier = Modifier.testTag("nav_dashboard")
        )

        NavigationBarItem(
            selected = currentScreen == "support_bot",
            onClick = { viewModel.navigateTo("support_bot") },
            label = { Text("Soporte") },
            icon = { Icon(Icons.Default.SupportAgent, contentDescription = "Soporte") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Terracotta,
                selectedTextColor = Terracotta,
                indicatorColor = LightTerracotta,
                unselectedIconColor = TextLight,
                unselectedTextColor = TextLight
            ),
            modifier = Modifier.testTag("nav_support")
        )
    }
}

/**
 * Home Landing Page Screen
 */
@Composable
fun HomeScreen(viewModel: AyniViewModel) {
    var searchInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Header Section
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LightTerracotta),
                border = BorderStroke(1.dp, Terracotta.copy(alpha = 0.10f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Encuentra técnicos y freelancers verificados en minutos",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkBrown,
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Conectamos hogares peruanos con mano de obra calificada de confianza, bajo el concepto andino de ayuda mutua (Ayni).",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextDark,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Buttons row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AyniButton(
                            text = "Buscar servicios",
                            onClick = { viewModel.navigateTo("search") },
                            modifier = Modifier.weight(1f).testTag("btn_buscar_servicios")
                        )
                        AyniOutlinedButton(
                            text = "Ser Profesional",
                            onClick = { viewModel.navigateWithHistory("login") },
                            modifier = Modifier.weight(1f).testTag("btn_ser_profesional")
                        )
                    }
                }
            }
        }

        // Search Box "¿Qué servicio necesitas hoy?"
        item {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = searchInput,
                onValueChange = { searchInput = it },
                placeholder = { Text("¿Qué servicio necesitas hoy?") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Terracotta) },
                trailingIcon = {
                    if (searchInput.isNotEmpty()) {
                        IconButton(onClick = {
                            viewModel.updateSearchQuery(searchInput)
                            viewModel.navigateTo("search")
                        }) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Buscar", tint = Gold)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Terracotta,
                    unfocusedBorderColor = GrayBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("main_search_bar")
            )
        }

        // Categories list
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Categorías Destacadas",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            val categories = listOf(
                "Decoración" to Icons.Default.Celebration,
                "Electricista" to Icons.Default.FlashOn,
                "Gasfitería" to Icons.Default.WaterDrop,
                "Programación" to Icons.Default.Code,
                "Limpieza" to Icons.Default.CleaningServices,
                "Carpintería" to Icons.Default.HomeRepairService,
                "Diseño gráfico" to Icons.Default.Palette,
                "Delivery" to Icons.Default.TwoWheeler
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories) { (name, icon) ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, GrayBorder),
                        modifier = Modifier
                            .width(110.dp)
                            .clickable {
                                viewModel.selectCategory(name)
                                viewModel.navigateTo("search")
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(LightGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = name,
                                    tint = Gold,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = name,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DarkBrown,
                                    fontSize = 11.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // How it works
        item {
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "Cómo Funciona",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HowItWorksCard(
                    number = "1",
                    title = "Busca",
                    description = "Filtra profesionales calificados en minutos por categoría, precio y cercanía."
                )
                HowItWorksCard(
                    number = "2",
                    title = "Conecta",
                    description = "Conversa en tiempo real con intercambio seguro y firma contratos digitales simples."
                )
                HowItWorksCard(
                    number = "3",
                    title = "Resuelve",
                    description = "Paga con sistema Escrow retenido. Los fondos solo se liberan al terminar el trabajo."
                )
            }
        }

        // Trust and Verification
        item {
            Spacer(modifier = Modifier.height(28.dp))
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkBrown),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Garantía de Confianza Ayni",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Gold
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    TrustItem(icon = Icons.Default.CheckCircle, text = "Técnicos verificados con antecedentes (KYC)")
                    TrustItem(icon = Icons.Default.Security, text = "Sistema Escrow: Pago liberado al terminar")
                    TrustItem(icon = Icons.Default.Star, text = "Calificaciones y reseñas bidireccionales reales")
                    TrustItem(icon = Icons.Default.SupportAgent, text = "Soporte técnico y solución de disputas 24/7")
                }
            }
        }

        // Testimonials Section
        item {
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "Clientes Satisfechos",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, GrayBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Gold),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("M", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("María Fernández", fontWeight = FontWeight.Bold, color = DarkBrown)
                            Row {
                                repeat(5) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Gold, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "\"Reservé una decoración de cumpleaños infantil con Liz Evelyn. La comunicación fue excelente, y el pago Escrow en Yape me dio total seguridad. ¡Súper recomendados!\"",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextDark, lineHeight = 18.sp)
                    )
                }
            }
        }

        // App promo
        item {
            Spacer(modifier = Modifier.height(28.dp))
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = LightGold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        tint = Gold,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Lleva Ayni.pe en tu bolsillo", fontWeight = FontWeight.Bold, color = DarkBrown)
                        Text("Descarga nuestra app móvil oficial próximamente.", fontSize = 12.sp, color = TextDark)
                    }
                }
            }
        }

        // Footer
        item {
            Spacer(modifier = Modifier.height(40.dp))
            Divider(color = GrayBorder)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Ayni.pe • Encuentra • Conecta • Resuelve\nHecho con amor en el Perú ❤️\nPreguntas Frecuentes | Soporte 24/7 | Términos de Servicio",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextLight,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            )
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun HowItWorksCard(number: String, title: String, description: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, GrayBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Terracotta),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, color = DarkBrown)
                Text(text = description, style = MaterialTheme.typography.bodyMedium.copy(color = TextDark))
            }
        }
    }
}

@Composable
fun TrustItem(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Gold, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, color = Color.White, fontSize = 13.sp)
    }
}

/**
 * Search and GPS Geolocation Simulation Screen
 */
@Composable
fun SearchScreen(viewModel: AyniViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val isSimulatingLocation by viewModel.isSimulatingLocation.collectAsState()
    val showNearbyProfessionals by viewModel.showNearbyProfessionals.collectAsState()
    val technicians by viewModel.filteredTechnicians.collectAsState()

    val selectedDistrict by viewModel.selectedDistrict.collectAsState()
    val maxPriceFilter by viewModel.maxPriceFilter.collectAsState()
    val minRatingFilter by viewModel.minRatingFilter.collectAsState()
    var showAdvancedFilters by remember { mutableStateOf(false) }

    val categories = listOf("Todos", "Decoración", "Electricista", "Gasfitería", "Programación", "Limpieza", "Carpintería")

    // Shimmer effect state trigger
    var isFilteringLoading by remember { mutableStateOf(false) }
    LaunchedEffect(searchQuery, selectedCategory, selectedDistrict, maxPriceFilter, minRatingFilter) {
        isFilteringLoading = true
        kotlinx.coroutines.delay(600)
        isFilteringLoading = false
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val isTablet = maxWidth > 650.dp

        @Composable
        fun SearchControlsBlock() {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Buscar por nombre, rubro o ubicación...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Terracotta) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Terracotta,
                        unfocusedBorderColor = GrayBorder,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("search_screen_query")
                )

                // Categories selector
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectCategory(cat) },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LightTerracotta,
                                selectedLabelColor = Terracotta,
                                selectedLeadingIconColor = Terracotta
                            )
                        )
                    }
                }

                // Advanced Filters toggler
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filtros rápidos",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = DarkBrown)
                    )
                    TextButton(
                        onClick = { showAdvancedFilters = !showAdvancedFilters },
                        colors = ButtonDefaults.textButtonColors(contentColor = Terracotta)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (showAdvancedFilters) Icons.Default.FilterListOff else Icons.Default.FilterList,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (showAdvancedFilters) "Ocultar Filtros" else "Filtros Avanzados", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                AnimatedVisibility(visible = showAdvancedFilters) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmWhite),
                        border = BorderStroke(1.dp, GrayBorder),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // District selection
                            Text("Ubicación (Distrito)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarkBrown)
                            val districts = listOf("Todos", "Villa El Salvador", "Villa María del Triunfo", "Miraflores")
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(districts) { dist ->
                                    val isSel = selectedDistrict == dist
                                    FilterChip(
                                        selected = isSel,
                                        onClick = { viewModel.selectDistrict(dist) },
                                        label = { Text(dist, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = LightTerracotta,
                                            selectedLabelColor = Terracotta
                                        )
                                    )
                                }
                            }

                            // Price Slider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Precio Máximo", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarkBrown)
                                Text("Hasta S/ ${maxPriceFilter.toInt()}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Terracotta)
                            }
                            Slider(
                                value = maxPriceFilter.toFloat(),
                                onValueChange = { viewModel.setMaxPrice(it.toDouble()) },
                                valueRange = 50f..1000f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Terracotta,
                                    activeTrackColor = Terracotta,
                                    inactiveTrackColor = GrayBorder
                                )
                            )

                            // Min Rating
                            Text("Calificación Mínima", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarkBrown)
                            val ratings = listOf(0.0 to "Cualquiera", 4.0 to "4.0+ ⭐", 4.5 to "4.5+ ⭐", 4.8 to "4.8+ ⭐")
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ratings.forEach { (stars, label) ->
                                    val isSel = minRatingFilter == stars
                                    FilterChip(
                                        selected = isSel,
                                        onClick = { viewModel.setMinRating(stars) },
                                        label = { Text(label, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = LightTerracotta,
                                            selectedLabelColor = Terracotta
                                        )
                                    )
                                }
                            }

                            // Reset Button
                            AyniOutlinedButton(
                                text = "Restablecer Filtros",
                                onClick = { viewModel.resetFilters() },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        @Composable
        fun GeolocationAndMapBlock(modifier: Modifier = Modifier) {
            Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Geolocation block
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LightGold),
                    border = BorderStroke(1.dp, Gold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Terracotta)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Ubicación de búsqueda", fontSize = 11.sp, color = TextLight)
                                Text(userLocation, fontWeight = FontWeight.Bold, color = DarkBrown, maxLines = 1)
                            }

                            if (isSimulatingLocation) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Terracotta, strokeWidth = 2.dp)
                            } else {
                                IconButton(
                                    onClick = { viewModel.simulateGeolocation() },
                                    modifier = Modifier.testTag("btn_geolocation")
                                ) {
                                    Icon(Icons.Default.MyLocation, contentDescription = "Simular ubicación", tint = Terracotta)
                                }
                            }
                        }

                        if (!showNearbyProfessionals) {
                            Spacer(modifier = Modifier.height(8.dp))
                            AyniButton(
                                text = "Buscar profesionales cercanos por GPS",
                                onClick = { viewModel.simulateGeolocation() },
                                icon = Icons.Default.MyLocation,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Simulated GPS Map representation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isTablet) 240.dp else 180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, GrayBorder, RoundedCornerShape(16.dp))
                ) {
                    DrawSimulatedMap(
                        isLocationFound = showNearbyProfessionals,
                        onPinClicked = { name ->
                            if (name.contains("Liz")) viewModel.navigateWithHistory("profile")
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Scanning overlay
                    if (isSimulatingLocation) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Terracotta)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Escaneando cercanía GPS...", fontWeight = FontWeight.Bold, color = Terracotta)
                            }
                        }
                    }

                    // Interactive Decorator Overlay matching the specific trigger
                    val queryMatchesDecor = searchQuery.lowercase().contains("decor") || selectedCategory == "Decoración"
                    if (showNearbyProfessionals && queryMatchesDecor && !isSimulatingLocation) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.5.dp, Terracotta),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(10.dp)
                                .clickable { viewModel.navigateWithHistory("profile") }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                ) {
                                    DrawLizAvatar(modifier = Modifier.fillMaxSize())
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Liz Evelyn Obregon",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = DarkBrown
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(GreenEscrow.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("Disponible Hoy", fontSize = 8.sp, color = GreenEscrow, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Text("Decoradora de Eventos • S/ 120 - 750", fontSize = 11.sp, color = TextLight)
                                    Text("📍 Av. 3 de Octubre: Villa El Salvador (A 1.2 km)", fontSize = 10.sp, color = Terracotta, fontWeight = FontWeight.Bold)
                                }
                                IconButton(
                                    onClick = { viewModel.navigateWithHistory("profile") },
                                    modifier = Modifier.background(LightTerracotta, CircleShape).size(36.dp)
                                ) {
                                    Icon(Icons.Default.ChevronRight, contentDescription = "Ver Perfil", tint = Terracotta)
                                }
                            }
                        }
                    }
                }
            }
        }

        @Composable
        fun ProfessionalsListBlock(modifier: Modifier = Modifier) {
            Column(modifier = modifier) {
                Text(
                    text = "Profesionales Disponibles (${technicians.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DarkBrown)
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (isFilteringLoading) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(3) {
                            ShimmerSearchCard()
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(technicians) { tech ->
                            ProfessionalCard(
                                tech = tech,
                                onClick = {
                                    if (tech.id == "liz_evelyn") {
                                        viewModel.navigateWithHistory("profile")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        if (isTablet) {
            // Tablet layout
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SearchControlsBlock()
                    ProfessionalsListBlock(modifier = Modifier.weight(1f))
                }

                Column(
                    modifier = Modifier
                        .weight(0.9f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GeolocationAndMapBlock()
                    // Add a tablet-only Mini Profile Preview block
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, GrayBorder),
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Box(modifier = Modifier.size(64.dp).clip(CircleShape)) {
                                DrawLizAvatar(modifier = Modifier.fillMaxSize())
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Liz Evelyn Obregon", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkBrown)
                            Text("Decoradora de Eventos • 537 trabajos", fontSize = 11.sp, color = TextLight)
                            Spacer(modifier = Modifier.height(10.dp))
                            AyniButton(
                                text = "Ver Perfil Completo",
                                onClick = { viewModel.navigateWithHistory("profile") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        } else {
            // Mobile vertical layout
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SearchControlsBlock()
                GeolocationAndMapBlock()
                ProfessionalsListBlock(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun DrawTechnicianAvatar(techId: String, name: String, modifier: Modifier = Modifier) {
    val imageUrl = when (techId) {
        "liz_evelyn" -> "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&q=80&w=256"
        "carlos_soto" -> "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=256"
        "sofia_ruiz" -> "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=256"
        "mateo_quispe" -> "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=256"
        "elena_huaman" -> "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&q=80&w=256"
        "raul_carpintero" -> "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&q=80&w=256"
        "ana_diseno" -> "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=256"
        else -> null
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (techId == "liz_evelyn") {
            DrawLizAvatar(modifier = Modifier.fillMaxSize())
        } else if (imageUrl != null) {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    DefaultAvatarFallback(name)
                },
                error = {
                    DefaultAvatarFallback(name)
                }
            )
        } else {
            DefaultAvatarFallback(name)
        }
    }
}

@Composable
fun DefaultAvatarFallback(name: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Gold),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.firstOrNull()?.toString() ?: "?",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
    }
}

@Composable
fun ProfessionalCard(tech: Technician, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, GrayBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar profile
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                DrawTechnicianAvatar(techId = tech.id, name = tech.name, modifier = Modifier.fillMaxSize())
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tech.name,
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(LightTerracotta, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(tech.category, fontSize = 9.sp, color = Terracotta, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Gold, modifier = Modifier.size(14.dp))
                    Text(" ${tech.rating} (${tech.completedJobs} trabajos)  •  ", fontSize = 11.sp, color = TextLight)
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Terracotta, modifier = Modifier.size(11.dp))
                    Text(" A ${tech.distanceKm} km", fontSize = 11.sp, color = TextLight, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Ayni Escrow Seguro  •  Yape / Plin",
                    fontSize = 11.sp,
                    color = GreenEscrow,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = onClick) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Ver Perfil", tint = Terracotta)
            }
        }
    }
}

/**
 * Provider Profile Screen for Liz Evelyn Obregon Olivera
 */
@Composable
fun ProviderProfileScreen(viewModel: AyniViewModel) {
    val tech = viewModel.lizEvelyn
    var isBookingFlowOpen by remember { mutableStateOf(false) }
    var selectedZoomedJob by remember { mutableStateOf<String?>(null) } // Facebook style image enlarge

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Profile cover banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    DrawPrincessTheme(modifier = Modifier.fillMaxSize())
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.2f))
                    )
                }
            }

            // Profile info & statistics
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile picture overlapping banner
                    Box(
                        modifier = Modifier
                            .offset(y = (-45).dp)
                            .size(90.dp)
                            .clip(CircleShape)
                            .border(4.dp, Color.White, CircleShape)
                    ) {
                        DrawLizAvatar(modifier = Modifier.fillMaxSize())
                    }

                    // User name & slogan
                    Column(
                        modifier = Modifier.offset(y = (-35).dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tech.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = DarkBrown)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tech.category,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Terracotta, fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Star ratings pill
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = LightGold,
                            border = BorderStroke(1.dp, Gold.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Gold, modifier = Modifier.size(16.dp))
                                Text(" ${tech.rating} Calificación", fontWeight = FontWeight.Bold, color = DarkBrown, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Divider(modifier = Modifier.width(1.dp).height(12.dp), color = TextLight)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("${tech.completedJobs} Trabajos", fontWeight = FontWeight.Bold, color = DarkBrown, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Liz detailed data card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, GrayBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-20).dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WorkHistory, contentDescription = null, tint = Terracotta)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Experiencia: ${tech.experience}", fontWeight = FontWeight.Bold, color = DarkBrown)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ThumbUp, contentDescription = null, tint = Terracotta)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Recomendaciones: ${tech.recommendationRate}% lo recomienda", fontWeight = FontWeight.Bold, color = DarkBrown)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Terracotta)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Ubicación: ${tech.locationDescription}", color = TextDark)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "📌 ${tech.serviceArea}",
                            color = Terracotta,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 32.dp)
                        )
                    }
                }
            }

            // Escrow explanation notice
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LightTerracotta),
                    border = BorderStroke(1.dp, Terracotta.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-10).dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = Terracotta, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Sistema Escrow Ayni", fontWeight = FontWeight.Bold, color = Terracotta, fontSize = 13.sp)
                            Text("Tu dinero está protegido. El pago solo se libera al profesional una vez culminado y validado el trabajo.", fontSize = 11.sp, color = TextDark)
                        }
                    }
                }
            }

            // Recent Jobs Section (Trabajos Recientes)
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Trabajos Recientes (Portafolio)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DarkBrown)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Selecciona una foto para ampliarla al estilo Facebook", fontSize = 11.sp, color = TextLight)
                    Spacer(modifier = Modifier.height(10.dp))

                    val portfolioJobs = listOf(
                        "Princess party" to "Princess Theme Decoration",
                        "Masha and Bear" to "Masha & Bear Event Setup",
                        "SpongeBob theme" to "SpongeBob Undersea Birthday",
                        "Alianza Lima football" to "Alianza Lima Soccer Party",
                        "Baby Shark marine" to "Baby Shark Balloon Arch",
                        "Little Mermaid tail" to "Little Mermaid Birthday"
                    )

                    // Grid layout of 2 columns
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (i in portfolioJobs.indices step 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(120.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(1.dp, GrayBorder, RoundedCornerShape(12.dp))
                                        .clickable { selectedZoomedJob = portfolioJobs[i].first }
                                ) {
                                    DrawPortfolioThumbnail(name = portfolioJobs[i].first)
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .fillMaxWidth()
                                            .background(Color.Black.copy(alpha = 0.5f))
                                            .padding(vertical = 4.dp, horizontal = 8.dp)
                                    ) {
                                        Text(portfolioJobs[i].second, color = Color.White, fontSize = 10.sp, maxLines = 1)
                                    }
                                }

                                if (i + 1 < portfolioJobs.size) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(120.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .border(1.dp, GrayBorder, RoundedCornerShape(12.dp))
                                            .clickable { selectedZoomedJob = portfolioJobs[i+1].first }
                                    ) {
                                        DrawPortfolioThumbnail(name = portfolioJobs[i+1].first)
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .fillMaxWidth()
                                                .background(Color.Black.copy(alpha = 0.5f))
                                                .padding(vertical = 4.dp, horizontal = 8.dp)
                                        ) {
                                            Text(portfolioJobs[i+1].second, color = Color.White, fontSize = 10.sp, maxLines = 1)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Services & Tariffs Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Servicios y Tarifas",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DarkBrown)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, GrayBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Service 1
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Decoración de Eventos Completa", fontWeight = FontWeight.Bold, color = DarkBrown)
                                    Text("Incluye fondo, globos temáticos, mesa principal, luces.", fontSize = 12.sp, color = TextLight)
                                    Text("⚠️ El precio final puede variar según la complejidad.", fontSize = 10.sp, color = Terracotta, fontWeight = FontWeight.Bold)
                                }
                                Text("S/ 120 - 750", fontWeight = FontWeight.Bold, color = Terracotta)
                            }

                            Divider(modifier = Modifier.padding(vertical = 12.dp), color = GrayBorder)

                            // Service 2
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Alquiler de Mobiliario", fontWeight = FontWeight.Bold, color = DarkBrown)
                                    Text("Mesas cilíndricas, paneles circulares, fondos dorados, estantes.", fontSize = 12.sp, color = TextLight)
                                    Text("⚠️ Depende del servicio o cantidad solicitada.", fontSize = 10.sp, color = Terracotta, fontWeight = FontWeight.Bold)
                                }
                                Text("Cotizar", fontWeight = FontWeight.Bold, color = Terracotta)
                            }

                            Divider(modifier = Modifier.padding(vertical = 12.dp), color = GrayBorder)

                            // Service 3
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Alquiler de Silla Vestida", fontWeight = FontWeight.Bold, color = DarkBrown)
                                    Text("Sillas de plástico con forro blanco y lazo de color a elección.", fontSize = 12.sp, color = TextLight)
                                }
                                Text("S/ 1.50 c/u", fontWeight = FontWeight.Bold, color = Terracotta)
                            }
                        }
                    }
                }
            }

            // Dynamic Calendar Availability & Agenda Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Disponibilidad y Agenda (Julio 2026)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DarkBrown)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Selecciona un día libre en verde para coordinar una reunión", fontSize = 11.sp, color = TextLight)
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, GrayBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Julio 2026", fontWeight = FontWeight.Bold, color = DarkBrown, fontSize = 14.sp)
                                Row {
                                    Box(
                                        modifier = Modifier
                                            .background(GreenEscrow.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("🟢 Libre", fontSize = 9.sp, color = GreenEscrow, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(Color.Red.copy(alpha = 0.10f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("🔴 Ocupado", fontSize = 9.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            val daysOfWeek = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                daysOfWeek.forEach { d ->
                                    Text(
                                        text = d,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextLight,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            val totalDays = 31
                            val offsetDays = 2 // Starts on Wednesday
                            val calendarBooked = viewModel.calendarBookings
                            val selectedDate by viewModel.selectedCalendarDate.collectAsState()

                            var dayCounter = 1
                            val rows = 5
                            for (r in 0 until rows) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    for (c in 0..6) {
                                        val isPadding = (r == 0 && c < offsetDays) || (dayCounter > totalDays)
                                        if (isPadding) {
                                            Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                                        } else {
                                            val currentDay = dayCounter
                                            val isBooked = calendarBooked.contains(currentDay)
                                            val isSelected = selectedDate == currentDay

                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .aspectRatio(1f)
                                                    .padding(2.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        when {
                                                            isSelected -> Terracotta
                                                            isBooked -> Color.Red.copy(alpha = 0.08f)
                                                            else -> GreenEscrow.copy(alpha = 0.08f)
                                                        }
                                                    )
                                                    .clickable(enabled = !isBooked) {
                                                        viewModel.selectCalendarDate(currentDay)
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "$currentDay",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = when {
                                                        isSelected -> Color.White
                                                        isBooked -> Color.Red
                                                        else -> GreenEscrow
                                                    }
                                                )
                                            }
                                            dayCounter++
                                        }
                                    }
                                }
                            }

                            selectedDate?.let { day ->
                                Spacer(modifier = Modifier.height(12.dp))
                                Divider(color = GrayBorder)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Fecha Seleccionada:", fontSize = 11.sp, color = TextLight)
                                        Text("Julio $day, 2026", fontWeight = FontWeight.Bold, color = DarkBrown, fontSize = 12.sp)
                                    }
                                    Button(
                                        onClick = { viewModel.requestMeetingForDate(day) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.VideoCall, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Pedir Videollamada", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Historial de Opiniones y Reseñas
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Opiniones de Clientes (${537})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DarkBrown)
                    )

                    var isWriteReviewOpen by remember { mutableStateOf(false) }
                    TextButton(
                        onClick = { isWriteReviewOpen = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = Terracotta)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Escribir Opinión", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (isWriteReviewOpen) {
                        var revAuthor by remember { mutableStateOf("") }
                        var revText by remember { mutableStateOf("") }
                        var revRating by remember { mutableStateOf(5.0) }

                        AlertDialog(
                            onDismissRequest = { isWriteReviewOpen = false },
                            title = { Text("Escribe tu Opinión", fontWeight = FontWeight.Bold, color = DarkBrown) },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedTextField(
                                        value = revAuthor,
                                        onValueChange = { revAuthor = it },
                                        placeholder = { Text("Tu Nombre") },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Calificación: ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkBrown)
                                        listOf(1, 2, 3, 4, 5).forEach { stars ->
                                            IconButton(onClick = { revRating = stars.toDouble() }) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = if (stars <= revRating) Gold else GrayBorder
                                                )
                                            }
                                        }
                                    }

                                    OutlinedTextField(
                                        value = revText,
                                        onValueChange = { revText = it },
                                        placeholder = { Text("¿Cómo fue tu experiencia con Liz?") },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().height(80.dp)
                                    )
                                }
                            },
                            confirmButton = {
                                Button(
                                    colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
                                    onClick = {
                                        if (revAuthor.isNotEmpty() && revText.isNotEmpty()) {
                                            viewModel.addReview(revAuthor, revRating, revText)
                                        }
                                        isWriteReviewOpen = false
                                    }
                                ) {
                                    Text("Enviar", color = Color.White)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { isWriteReviewOpen = false }) {
                                    Text("Cancelar", color = TextLight)
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val reviewsList by viewModel.lizReviews.collectAsState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    reviewsList.forEach { rev ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, GrayBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(LightTerracotta),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(rev.authorName.first().toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Terracotta)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(rev.authorName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarkBrown)
                                    }
                                    Text(rev.date, fontSize = 9.sp, color = TextLight)
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    listOf(1, 2, 3, 4, 5).forEach { s ->
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = if (s <= rev.rating) Gold else GrayBorder,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(String.format("%.1f ⭐", rev.rating), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Gold)
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(rev.text, fontSize = 11.sp, color = TextDark)

                                if (rev.photos.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = GreenEscrow, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Verificado por Escrow Ayni", fontSize = 9.sp, color = GreenEscrow, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // CTAs block (Reservar and Chat)
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AyniOutlinedButton(
                        text = "Escribir Chat",
                        onClick = { viewModel.navigateWithHistory("chat_liz") },
                        icon = Icons.AutoMirrored.Filled.Chat,
                        modifier = Modifier.weight(1f).testTag("btn_write_chat_liz")
                    )
                    AyniButton(
                        text = "Reservar Ahora",
                        onClick = { isBookingFlowOpen = true },
                        icon = Icons.Default.Event,
                        modifier = Modifier.weight(1.2f).testTag("btn_reserve_liz")
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // Booking and Payment Escrow Flow Dialog Overlay
        if (isBookingFlowOpen) {
            EscrowBookingDialog(
                tech = tech,
                onDismiss = { isBookingFlowOpen = false },
                onBookingConfirmed = { service, price, method ->
                    viewModel.bookLizService(service, price, method)
                    isBookingFlowOpen = false
                    viewModel.navigateTo("dashboard")
                }
            )
        }

        // Facebook style Photo zoom dialog overlay
        selectedZoomedJob?.let { jobName ->
            DialogPhotoZoom(
                jobName = jobName,
                onDismiss = { selectedZoomedJob = null }
            )
        }
    }
}

@Composable
fun DrawPortfolioThumbnail(name: String) {
    val imageUrl = when (name) {
        "Princess party" -> "https://images.unsplash.com/photo-1513151233558-d860c5398176?auto=format&fit=crop&q=80&w=600"
        "Masha and Bear" -> "https://images.unsplash.com/photo-1530103862676-de8c9debad1d?auto=format&fit=crop&q=80&w=600"
        "SpongeBob theme" -> "https://images.unsplash.com/photo-1464366400600-7168b8af9bc3?auto=format&fit=crop&q=80&w=600"
        "Alianza Lima football" -> "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?auto=format&fit=crop&q=80&w=600"
        "Baby Shark marine" -> "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?auto=format&fit=crop&q=80&w=600"
        "Little Mermaid tail" -> "https://images.unsplash.com/photo-1502086223501-7ea6ecd79368?auto=format&fit=crop&q=80&w=600"
        else -> null
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (imageUrl != null) {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    DrawPortfolioFallback(name)
                },
                error = {
                    DrawPortfolioFallback(name)
                }
            )
        } else {
            DrawPortfolioFallback(name)
        }
    }
}

@Composable
fun DrawPortfolioFallback(name: String) {
    when (name) {
        "Princess party" -> DrawPrincessTheme(modifier = Modifier.fillMaxSize())
        "Masha and Bear" -> DrawMashaTheme(modifier = Modifier.fillMaxSize())
        "SpongeBob theme" -> DrawSpongebobTheme(modifier = Modifier.fillMaxSize())
        "Alianza Lima football" -> DrawAlianzaTheme(modifier = Modifier.fillMaxSize())
        "Baby Shark marine" -> DrawBabySharkTheme(modifier = Modifier.fillMaxSize())
        "Little Mermaid tail" -> DrawMermaidTheme(modifier = Modifier.fillMaxSize())
    }
}

/**
 * Facebook-style Photo Zoom Overlay (Modal)
 */
@Composable
fun DialogPhotoZoom(jobName: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(350.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, Color.White, RoundedCornerShape(16.dp))
            ) {
                DrawPortfolioThumbnail(name = jobName)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Trabajo Realizado por Liz Evelyn Obregon",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Presiona en cualquier parte para cerrar",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}

/**
 * Escrow Payment Flow Dialogue featuring Yape and Plin ONLY
 * Displays dynamic pricing, built-in service commission (without breaking it down explicitly)
 */
@Composable
fun EscrowBookingDialog(
    tech: Technician,
    onDismiss: () -> Unit,
    onBookingConfirmed: (String, Double, String) -> Unit
) {
    var decorationRate by remember { mutableStateOf(350.0) } // sliding scale from 120 to 750
    var numSillas by remember { mutableStateOf(40) } // S/ 1.50 each
    var includeFurniture by remember { mutableStateOf(false) } // S/ 150 extra

    // Calculation
    val basePrice = decorationRate + (numSillas * 1.50) + (if (includeFurniture) 150.0 else 0.0)
    // 15% commission is included in the shown final price, but we don't show the exact commission amount or the percentage value as a fee.
    // Instead we just show: "Incluye comisión de servicio"
    val commissionScale = 1.15
    val totalPrice = basePrice * commissionScale

    var paymentStep by remember { mutableStateOf(1) } // 1 = details selection, 2 = select Yape/Plin, 3 = confirmation barcode/qr scan
    var selectedMethod by remember { mutableStateOf("Yape") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Reservar con Liz Evelyn",
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown,
                        fontSize = 18.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Divider(color = GrayBorder, modifier = Modifier.padding(vertical = 8.dp))

                if (paymentStep == 1) {
                    // Step 1: Select details
                    Text("Configura el presupuesto para tu evento", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Terracotta)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Slider for main decoration complex level
                    Text("Presupuesto de Decoración: S/ ${decorationRate.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkBrown)
                    Text("Nivel del arreglo: S/ 120 (Simple) - S/ 750 (Premium)", fontSize = 10.sp, color = TextLight)
                    Slider(
                        value = decorationRate.toFloat(),
                        onValueChange = { decorationRate = it.toDouble() },
                        valueRange = 120f..750f,
                        colors = SliderDefaults.colors(thumbColor = Terracotta, activeTrackColor = Terracotta)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sillas count
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Alquiler de Sillas", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarkBrown)
                            Text("S/ 1.50 por unidad", fontSize = 10.sp, color = TextLight)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { if (numSillas > 0) numSillas -= 5 }) {
                                Icon(Icons.Default.RemoveCircle, contentDescription = null, tint = Terracotta)
                            }
                            Text("$numSillas", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            IconButton(onClick = { numSillas += 5 }) {
                                Icon(Icons.Default.AddCircle, contentDescription = null, tint = Terracotta)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Furniture toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Alquiler de Mobiliario", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarkBrown)
                            Text("Fondo circular + 3 cilindros (S/ 150)", fontSize = 10.sp, color = TextLight)
                        }
                        Switch(
                            checked = includeFurniture,
                            onCheckedChange = { includeFurniture = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Terracotta, checkedTrackColor = LightTerracotta)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Total & Notice
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = LightGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total del Servicio:", fontWeight = FontWeight.Bold, color = DarkBrown)
                                Text("S/ %.2f".format(totalPrice), fontWeight = FontWeight.Bold, color = Terracotta, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            // MUST not show the commission breakdown amount, just a note
                            Text(
                                text = "* Incluye comisión por servicio",
                                fontSize = 11.sp,
                                color = TextLight,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    AyniButton(
                        text = "Continuar al Pago Escrow",
                        onClick = { paymentStep = 2 },
                        modifier = Modifier.fillMaxWidth().testTag("btn_payment_step_1")
                    )

                } else if (paymentStep == 2) {
                    // Step 2: Choose Yape/Plin
                    Text("Selecciona tu Billetera Digital de Pago", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Terracotta)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Yape choice button
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedMethod == "Yape") Color(0xFF70008F) else Color.White
                        ),
                        border = BorderStroke(2.dp, Color(0xFF70008F)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedMethod = "Yape" }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Y", color = Color(0xFF70008F), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                "Yape Seguro Ayni",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedMethod == "Yape") Color.White else Color(0xFF70008F)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Plin choice button
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedMethod == "Plin") Color(0xFF00BFA5) else Color.White
                        ),
                        border = BorderStroke(2.dp, Color(0xFF00BFA5)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedMethod = "Plin" }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("P", color = Color(0xFF00BFA5), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                "Plin Seguro Ayni",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedMethod == "Plin") Color.White else Color(0xFF00BFA5)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AyniOutlinedButton(
                            text = "Atrás",
                            onClick = { paymentStep = 1 },
                            modifier = Modifier.weight(1f)
                        )
                        AyniButton(
                            text = "Pagar con $selectedMethod",
                            onClick = { paymentStep = 3 },
                            modifier = Modifier.weight(1.5f).testTag("btn_pago_simulado")
                        )
                    }

                } else if (paymentStep == 3) {
                    // Step 3: QR simulation & Success code
                    Text("Escanea o Paga al QR de Ayni", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Terracotta)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Simulated QR code block
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .background(Color.White)
                            .border(2.dp, DarkBrown, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Drawing a fake QR pattern in Canvas
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val ws = size.width
                            val hs = size.height
                            // Corner markers
                            drawRect(Color.Black, Offset(0f, 0f), Size(ws * 0.25f, hs * 0.25f))
                            drawRect(Color.White, Offset(4f, 4f), Size(ws * 0.25f - 8f, hs * 0.25f - 8f))
                            drawRect(Color.Black, Offset(10f, 10f), Size(ws * 0.25f - 20f, hs * 0.25f - 20f))

                            drawRect(Color.Black, Offset(ws * 0.75f, 0f), Size(ws * 0.25f, hs * 0.25f))
                            drawRect(Color.White, Offset(ws * 0.75f + 4f, 4f), Size(ws * 0.25f - 8f, hs * 0.25f - 8f))
                            drawRect(Color.Black, Offset(ws * 0.75f + 10f, 10f), Size(ws * 0.25f - 20f, hs * 0.25f - 20f))

                            drawRect(Color.Black, Offset(0f, hs * 0.75f), Size(ws * 0.25f, hs * 0.25f))
                            drawRect(Color.White, Offset(4f, hs * 0.75f + 4f), Size(ws * 0.25f - 8f, hs * 0.25f - 8f))
                            drawRect(Color.Black, Offset(10f, hs * 0.75f + 10f), Size(ws * 0.25f - 20f, hs * 0.25f - 20f))

                            // Fake dots
                            for (r in 0..12) {
                                for (c in 0..12) {
                                    if ((r + c) % 3 == 0 && !(r < 4 && c < 4) && !(r > 8 && c < 4) && !(r < 4 && c > 8)) {
                                        drawRect(
                                            color = if (selectedMethod == "Yape") Color(0xFF70008F) else Color(0xFF00BFA5),
                                            topLeft = Offset(r * ws * 0.07f + 4f, c * hs * 0.07f + 4f),
                                            size = Size(8f, 8f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Total a enviar: S/ %.2f".format(totalPrice),
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Monto de Escrow seguro. Se retendrá en el sistema Ayni.pe hasta concluir el trabajo.",
                        fontSize = 11.sp,
                        color = TextLight,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AyniOutlinedButton(
                            text = "Cancelar",
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        )
                        AyniButton(
                            text = "Confirmar Pago",
                            onClick = {
                                onBookingConfirmed("Decoración de Evento", totalPrice, selectedMethod)
                            },
                            modifier = Modifier.weight(1.5f).testTag("btn_confirmar_pago_final")
                        )
                    }
                }
            }
        },
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    )
}

/**
 * Chat Screen with Liz Evelyn
 */
@Composable
fun ChatLizScreen(viewModel: AyniViewModel) {
    val messages by viewModel.lizMessages.collectAsState()
    var textInput by remember { mutableStateOf("") }
    var showFileSelector by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Chat Header
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, GrayBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(44.dp).clip(CircleShape)) {
                    DrawLizAvatar(modifier = Modifier.fillMaxSize())
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Liz Evelyn Obregon", fontWeight = FontWeight.Bold, color = DarkBrown)
                    Text("• Activo ahora (Responde rápido)", fontSize = 11.sp, color = GreenEscrow, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chat History / Bubbles
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.sender == "user"
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 2.dp,
                            bottomEnd = if (isUser) 2.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) Terracotta else Color.White
                        ),
                        border = if (isUser) null else BorderStroke(1.dp, GrayBorder),
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            val text = msg.text
                            val isSecureFile = text.startsWith("[SECURE_FILE:") && text.endsWith("]")
                            if (isSecureFile) {
                                val fileData = text.removePrefix("[SECURE_FILE:").removeSuffix("]").split("|")
                                val fileName = fileData.getOrNull(0) ?: "archivo_seguro.dat"
                                val fileSize = fileData.getOrNull(1) ?: "0 KB"
                                SecureFileBubbleContent(fileName = fileName, fileSize = fileSize, isUser = isUser, viewModel = viewModel)
                            } else {
                                Text(
                                    text = text,
                                    color = if (isUser) Color.White else TextDark,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Input Field and Attach Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { showFileSelector = true },
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LightTerracotta)
                    .testTag("chat_liz_attach_btn")
            ) {
                Icon(Icons.Default.AttachFile, contentDescription = "Adjuntar archivo seguro", tint = Terracotta)
            }

            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("Escribe o adjunta un archivo...") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Terracotta,
                    unfocusedBorderColor = GrayBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.weight(1f).testTag("chat_liz_input")
            )

            IconButton(
                onClick = {
                    if (textInput.isNotEmpty()) {
                        viewModel.sendUserMessageToLiz(textInput)
                        textInput = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Terracotta)
                    .testTag("chat_liz_send_btn")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Enviar", tint = Color.White)
            }
        }
    }

    if (showFileSelector) {
        SecureAttachmentDialog(
            onDismiss = { showFileSelector = false },
            onFileSelected = { name, size ->
                viewModel.sendSecureFileToLiz(name, size)
                showFileSelector = false
            }
        )
    }
}

@Composable
fun SecureAttachmentDialog(onDismiss: () -> Unit, onFileSelected: (String, String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Security, contentDescription = null, tint = GreenEscrow, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Bóveda de Archivos Ayni", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkBrown)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Todos los archivos compartidos en Ayni se cifran de extremo a extremo y se escanean automáticamente contra virus para proteger tu privacidad y seguridad.",
                    fontSize = 11.sp,
                    color = TextLight
                )
                Divider(color = GrayBorder)
                Text("Selecciona un archivo seguro para enviar:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarkBrown)

                val sampleFiles = listOf(
                    "Croquis_Evento_VillaSalvador.png" to "1.2 MB",
                    "Presupuesto_Decoracion_Liz.pdf" to "840 KB",
                    "Contrato_Modelo_Arrendamiento.docx" to "2.4 MB"
                )

                sampleFiles.forEach { (name, size) ->
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, GrayBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onFileSelected(name, size)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (name.endsWith(".png")) Icons.Default.Image else Icons.Default.Description,
                                    contentDescription = null,
                                    tint = Terracotta,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(name, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DarkBrown)
                                    Text(size, fontSize = 9.sp, color = TextLight)
                                }
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextLight, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextLight)
            }
        }
    )
}

@Composable
fun SecureFileBubbleContent(fileName: String, fileSize: String, isUser: Boolean, viewModel: AyniViewModel) {
    val bookings by viewModel.bookings.collectAsState()
    val hasActiveEscrow = bookings.any { it.providerName.contains("Liz") && it.status == "En Escrow" }

    Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = if (fileName.endsWith(".png") || fileName.endsWith(".jpg")) Icons.Default.Image else Icons.Default.Description,
                contentDescription = null,
                tint = if (isUser) Color.White else Terracotta,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fileName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (isUser) Color.White else DarkBrown,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = fileSize,
                    fontSize = 10.sp,
                    color = if (isUser) Color.White.copy(alpha = 0.8f) else TextLight
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Security Banner
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (isUser) Color.White.copy(alpha = 0.15f) else GreenEscrow.copy(alpha = 0.10f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = if (isUser) Color.White else GreenEscrow,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Verificado por Escrow Ayni.pe • Seguro",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUser) Color.White else GreenEscrow
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Action Button: Download/Open
        OutlinedButton(
            onClick = {},
            border = BorderStroke(1.dp, if (isUser) Color.White else Terracotta),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (isUser) Color.White else Terracotta,
                containerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Descargar seguro", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Real-time Escrow transaction triggers!
        if (!isUser && fileName.contains("Presupuesto")) {
            Spacer(modifier = Modifier.height(8.dp))
            if (hasActiveEscrow) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = LightGold),
                    border = BorderStroke(1.dp, Gold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Gold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Garantía Escrow Activa por S/ 350.00",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBrown
                        )
                    }
                }
            } else {
                Button(
                    onClick = { viewModel.bookLizService("Decoración Premium Liz", 350.0, "Yape") },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenEscrow),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Aceptar y Depositar S/ 350 en Garantía",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * Interactive Complaint Registry Chatbot Screen
 * Adheres strictly to the María Fernández dialogue script requested
 */
@Composable
fun SupportBotScreen(viewModel: AyniViewModel) {
    val messages by viewModel.supportMessages.collectAsState()
    var textInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Support Header
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkBrown),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Gold),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.SupportAgent, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Centro de Atención de Quejas (24/7)", fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Auto-asistente virtual inteligente", fontSize = 11.sp, color = Gold, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chat history
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.sender == "user"
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 2.dp,
                            bottomEnd = if (isUser) 2.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) Terracotta else LightGold
                        ),
                        border = if (isUser) null else BorderStroke(1.dp, Gold.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = msg.text,
                                color = if (isUser) Color.White else TextDark,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bot attachments or options shortcuts
        val optionsVisible = messages.lastOrNull()?.text?.contains("selecciona la categoría") == true || 
                             messages.lastOrNull()?.text?.contains("selecciona una opción") == true

        if (optionsVisible) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AyniOutlinedButton(
                    text = "Opción 1",
                    onClick = { viewModel.sendUserMessageToSupport("1") },
                    modifier = Modifier.weight(1f)
                )
                AyniOutlinedButton(
                    text = "Opción 2",
                    onClick = { viewModel.sendUserMessageToSupport("2") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Clip upload photo trigger if chatbot requested file attach
        val fileAttachRequested = messages.lastOrNull()?.text?.contains("adjunta los archivos") == true
        if (fileAttachRequested) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = LightTerracotta),
                border = BorderStroke(1.dp, Terracotta),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.simulateAttachmentInBot() }
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = null, tint = Terracotta)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Presiona aquí para Adjuntar Evidencia (Fotos)", fontWeight = FontWeight.Bold, color = Terracotta, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Input Box
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("Escribe una respuesta o selecciona opción...") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Terracotta,
                    unfocusedBorderColor = GrayBorder,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.weight(1f).testTag("support_bot_input")
            )

            IconButton(
                onClick = {
                    if (textInput.isNotEmpty()) {
                        viewModel.sendUserMessageToSupport(textInput)
                        textInput = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Terracotta)
                    .testTag("support_bot_send_btn")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Enviar", tint = Color.White)
            }
        }
    }
}

/**
 * Escrow Operations Dashboard
 * Track active contract funds, release payment after technical job verification, track complaints
 */
@Composable
fun DashboardScreen(viewModel: AyniViewModel) {
    val bookings by viewModel.bookings.collectAsState()
    val complaints by viewModel.complaints.collectAsState()

    var activeTab by remember { mutableStateOf("escrow") } // escrow, complaints

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Centro de Control del Usuario",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = DarkBrown)
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Tabs switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = activeTab == "escrow",
                onClick = { activeTab = "escrow" },
                label = { Text("Pagos en Escrow (${bookings.size})") }
            )
            FilterChip(
                selected = activeTab == "complaints",
                onClick = { activeTab = "complaints" },
                label = { Text("Mis Quejas (${complaints.size})") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (activeTab == "escrow") {
            if (bookings.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = TextLight, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No tienes transacciones activas.", color = TextLight, fontWeight = FontWeight.Bold)
                        Text("Reserva un servicio de decoración para iniciar.", fontSize = 12.sp, color = TextLight)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bookings) { booking ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, GrayBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(booking.providerName, fontWeight = FontWeight.Bold, color = DarkBrown)
                                        Text(booking.serviceName, fontSize = 12.sp, color = TextLight)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (booking.status == "En Escrow") LightGold else LightTerracotta,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            booking.status,
                                            color = if (booking.status == "En Escrow") Color(0xFFD4AF37) else Terracotta,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Total Depositado:", fontSize = 11.sp, color = TextLight)
                                        Text("S/ %.2f".format(booking.totalPaid), fontWeight = FontWeight.Bold, color = Terracotta, fontSize = 16.sp)
                                        Text("* Incluye comisión por servicio", fontSize = 9.sp, color = TextLight, fontWeight = FontWeight.Bold)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Método: ${booking.paymentMethod}", fontSize = 11.sp, color = TextLight, fontWeight = FontWeight.Bold)
                                        Text(booking.date, fontSize = 11.sp, color = TextLight)
                                    }
                                }

                                if (booking.status == "En Escrow") {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { /* Dispute simulation */ },
                                            colors = ButtonDefaults.buttonColors(containerColor = Terracotta),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Iniciar Disputa", fontSize = 11.sp, color = Color.White)
                                        }

                                        Button(
                                            onClick = { /* release funds simulation */ },
                                            colors = ButtonDefaults.buttonColors(containerColor = GreenEscrow),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.weight(1.2f)
                                        ) {
                                            Text("Liberar Pago al Técnico", fontSize = 11.sp, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Complaints
            if (complaints.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SupportAgent, contentDescription = null, tint = TextLight, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No tienes quejas registradas.", color = TextLight, fontWeight = FontWeight.Bold)
                        Text("Usa el Chatbot de Soporte para iniciar.", fontSize = 12.sp, color = TextLight)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(complaints) { comp ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, GrayBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Código: ${comp.code}", fontWeight = FontWeight.Bold, color = DarkBrown)
                                        Text("Categoría: ${comp.category}", fontSize = 12.sp, color = TextLight)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(LightTerracotta, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(comp.status, color = Terracotta, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Motivo: ${comp.motive}", fontSize = 12.sp, color = TextDark)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Fecha de Compra: ${comp.purchaseDate}", fontSize = 11.sp, color = TextLight)
                                Text("Correo de actualizaciones: ${comp.email}", fontSize = 11.sp, color = TextLight)
                                if (comp.evidenceAttached) {
                                    Text("📎 Evidencia adjunta con éxito", fontSize = 11.sp, color = GreenEscrow, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Login & Sign Up UI Simulator
 */
@Composable
fun LoginRegisterScreen(viewModel: AyniViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegister by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AyniLogo(showSlogan = true, sizeScale = 1.0f)
        
        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = if (isRegister) "Crear cuenta en Ayni.pe" else "Ingresar a Ayni.pe",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = DarkBrown)
        )
        Text(
            text = if (isRegister) "Únete hoy y brinda o solicita servicios técnicos" else "Conéctate con tu billetera Yape/Plin",
            fontSize = 12.sp,
            color = TextLight,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Correo electrónico") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().testTag("auth_email")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Contraseña") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().testTag("auth_password")
        )

        Spacer(modifier = Modifier.height(24.dp))

        AyniButton(
            text = if (isRegister) "Registrarme" else "Iniciar Sesión",
            onClick = {
                viewModel.login(email)
                viewModel.navigateTo("home")
            },
            modifier = Modifier.fillMaxWidth().testTag("auth_submit")
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isRegister) "¿Ya tienes cuenta? Iniciar Sesión" else "¿No tienes cuenta? Regístrate aquí",
            color = Terracotta,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier
                .clickable { isRegister = !isRegister }
                .padding(8.dp)
        )
    }
}

@Composable
fun NotificationBannerOverlay(viewModel: AyniViewModel) {
    val notification by viewModel.activeNotification.collectAsState()

    AnimatedVisibility(
        visible = notification != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(100f)
            .padding(16.dp)
    ) {
        notification?.let { n ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (n.isEscrow) GreenEscrow else DarkBrown
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.dismissNotification() }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (n.isEscrow) Icons.Default.Security else Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = n.title,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                        Text(
                            text = n.message,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp
                        )
                    }
                    IconButton(
                        onClick = { viewModel.dismissNotification() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun shimmerBrush(showShimmer: Boolean = true, targetValue: Float = 1000f): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f),
        )

        val transition = rememberInfiniteTransition(label = "shimmer")
        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "shimmerTranslation"
        )

        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation.value, y = translateAnimation.value)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}

@Composable
fun ShimmerSearchCard() {
    val brush = shimmerBrush()
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, GrayBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(brush)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
            }
        }
    }
}

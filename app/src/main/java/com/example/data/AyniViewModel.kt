package com.example.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Simple structures to represent technicians
data class Technician(
    val id: String,
    val name: String,
    val category: String, // "Decoración", "Electricista", "Gasfitería", "Limpieza", "Diseño Gráfico", "Programación"
    val rating: Double,
    val completedJobs: Int,
    val recommendationRate: Int,
    val locationDescription: String,
    val serviceArea: String,
    val experience: String,
    val basePriceRange: String,
    val distanceKm: Double, // dynamic simulation
    val arrivalTimeMins: Int,
    val latOffset: Float, // for custom map drawings
    val lngOffset: Float
)

sealed class ChatBotState {
    object Idle : ChatBotState()
    object WaitOption : ChatBotState()
    object WaitName : ChatBotState()
    object WaitDni : ChatBotState()
    object WaitMotive : ChatBotState()
    object WaitDate : ChatBotState()
    object WaitEvidenceAsk : ChatBotState()
    object WaitEvidenceAttach : ChatBotState()
    object WaitCategory : ChatBotState()
    object WaitEmailAsk : ChatBotState()
    object WaitEmail : ChatBotState()
    object Summary : ChatBotState()
}

class AyniViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = AyniRepository(database)

    // Flow states
    val bookings: StateFlow<List<Booking>> = repository.allBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val complaints: StateFlow<List<Complaint>> = repository.allComplaints
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI screen flow
    private val _currentScreen = MutableStateFlow("home") // home, search, profile, chat_liz, support_bot, dashboard, login
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    // Auth Simulation
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()
    fun login(email: String) { _isLoggedIn.value = true }
    fun logout() { _isLoggedIn.value = false }

    // Navigation BackStack list
    private val screenHistory = mutableListOf<String>()
    fun navigateWithHistory(screen: String) {
        screenHistory.add(_currentScreen.value)
        _currentScreen.value = screen
    }
    fun goBack() {
        if (screenHistory.isNotEmpty()) {
            _currentScreen.value = screenHistory.removeAt(screenHistory.size - 1)
        } else {
            _currentScreen.value = "home"
        }
    }

    // Search query & category
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Todos")
    val selectedCategory = _selectedCategory.asStateFlow()

    // Location state
    private val _userLocation = MutableStateFlow("Lima, Perú")
    val userLocation = _userLocation.asStateFlow()

    private val _isSimulatingLocation = MutableStateFlow(false)
    val isSimulatingLocation = _isSimulatingLocation.asStateFlow()

    private val _showNearbyProfessionals = MutableStateFlow(false)
    val showNearbyProfessionals = _showNearbyProfessionals.asStateFlow()

    // Selected technician for details
    private val _selectedTechnician = MutableStateFlow<Technician?>(null)
    val selectedTechnician = _selectedTechnician.asStateFlow()

    fun selectTechnician(tech: Technician) {
        _selectedTechnician.value = tech
    }

    val allTechniciansList: List<Technician> get() = otherTechnicians

    // Technicians list
    val lizEvelyn = Technician(
        id = "liz_evelyn",
        name = "Liz Evelyn Obregon Olivera",
        category = "Decoración",
        rating = 4.5,
        completedJobs = 537,
        recommendationRate = 98,
        locationDescription = "Av. 3 de Octubre: Villa El Salvador",
        serviceArea = "Llega a todos los distritos",
        experience = "5 años en el rubro",
        basePriceRange = "S/ 120 - 750",
        distanceKm = 1.2,
        arrivalTimeMins = 15,
        latOffset = -0.015f,
        lngOffset = 0.012f
    )

    private val otherTechnicians = listOf(
        lizEvelyn,
        Technician(
            id = "carlos_soto",
            name = "Carlos Soto Mendoza",
            category = "Electricista",
            rating = 4.8,
            completedJobs = 312,
            recommendationRate = 96,
            locationDescription = "Av. Revolución: Villa El Salvador",
            serviceArea = "Lima Sur y Centro",
            experience = "8 años en electricidad industrial y doméstica",
            basePriceRange = "S/ 50 - 200",
            distanceKm = 2.4,
            arrivalTimeMins = 25,
            latOffset = 0.025f,
            lngOffset = -0.018f
        ),
        Technician(
            id = "sofia_ruiz",
            name = "Sofia Ruiz Gonzales",
            category = "Gasfitería",
            rating = 4.7,
            completedJobs = 189,
            recommendationRate = 95,
            locationDescription = "Av. Central: Villa María del Triunfo",
            serviceArea = "Todos los distritos",
            experience = "4 años resolviendo fugas y tuberías",
            basePriceRange = "S/ 40 - 150",
            distanceKm = 3.8,
            arrivalTimeMins = 35,
            latOffset = -0.032f,
            lngOffset = -0.025f
        ),
        Technician(
            id = "mateo_quispe",
            name = "Mateo Quispe Huamán",
            category = "Programación",
            rating = 4.9,
            completedJobs = 84,
            recommendationRate = 100,
            locationDescription = "Remoto - San Juan de Miraflores",
            serviceArea = "Remoto y Presencial Lima",
            experience = "3 años desarrollando apps móviles",
            basePriceRange = "S/ 150 - 1000",
            distanceKm = 4.1,
            arrivalTimeMins = 0, // Remoto
            latOffset = 0.045f,
            lngOffset = 0.035f
        ),
        Technician(
            id = "elena_huaman",
            name = "Elena Huamán Flores",
            category = "Limpieza",
            rating = 4.6,
            completedJobs = 620,
            recommendationRate = 97,
            locationDescription = "Av. Separadora Industrial: Villa El Salvador",
            serviceArea = "Lima Metropolitana",
            experience = "6 años de confianza en hogares",
            basePriceRange = "S/ 60 - 180",
            distanceKm = 1.8,
            arrivalTimeMins = 20,
            latOffset = -0.005f,
            lngOffset = -0.008f
        ),
        Technician(
            id = "raul_carpintero",
            name = "Raúl Castro Medina",
            category = "Carpintería",
            rating = 4.4,
            completedJobs = 215,
            recommendationRate = 92,
            locationDescription = "Av. El Sol: Villa El Salvador",
            serviceArea = "Sur de Lima",
            experience = "10 años diseñando muebles a medida",
            basePriceRange = "S/ 100 - 900",
            distanceKm = 2.9,
            arrivalTimeMins = 30,
            latOffset = 0.012f,
            lngOffset = 0.028f
        ),
        Technician(
            id = "ana_diseno",
            name = "Ana Belén Prado",
            category = "Diseño gráfico",
            rating = 4.7,
            completedJobs = 143,
            recommendationRate = 98,
            locationDescription = "Miraflores, Lima",
            serviceArea = "Remoto nacional",
            experience = "5 años diseñando branding e identidad",
            basePriceRange = "S/ 80 - 500",
            distanceKm = 12.0,
            arrivalTimeMins = 0,
            latOffset = 0.110f,
            lngOffset = 0.095f
        )
    )

    // Filter logic
    private val _filteredTechnicians = MutableStateFlow<List<Technician>>(otherTechnicians)
    val filteredTechnicians = _filteredTechnicians.asStateFlow()

    init {
        // Initially fill the list
        _filteredTechnicians.value = otherTechnicians
        // Pre-populate some chat history or support messages if empty
        viewModelScope.launch {
            repository.clearChat("Liz")
            repository.clearChat("SupportBot")
            
            // Insert initial welcome message for Liz
            repository.insertMessage(
                ChatMessage(
                    sender = "provider",
                    text = "¡Hola! Bienvenido a mi perfil de servicios de decoración. Presiona aquí para enviarme un mensaje.",
                    recipient = "Liz"
                )
            )

            // Insert initial support bot message
            repository.insertMessage(
                ChatMessage(
                    sender = "bot",
                    text = "¡Hola! Bienvenido al Centro de Atención al Cliente. Estoy aquí para ayudarte. Por favor, selecciona una opción:\n\n1️⃣ Registrar una queja\n2️⃣ Consultar el estado de una queja\n3️⃣ Hablar con un asesor\n4️⃣ Salir",
                    recipient = "SupportBot"
                )
            )
        }
    }

    private val _selectedDistrict = MutableStateFlow("Todos")
    val selectedDistrict = _selectedDistrict.asStateFlow()

    private val _maxPriceFilter = MutableStateFlow(1000.0)
    val maxPriceFilter = _maxPriceFilter.asStateFlow()

    private val _minRatingFilter = MutableStateFlow(0.0)
    val minRatingFilter = _minRatingFilter.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        applyFilters()
    }

    fun selectDistrict(district: String) {
        _selectedDistrict.value = district
        applyFilters()
    }

    fun setMaxPrice(price: Double) {
        _maxPriceFilter.value = price
        applyFilters()
    }

    fun setMinRating(rating: Double) {
        _minRatingFilter.value = rating
        applyFilters()
    }

    fun resetFilters() {
        _selectedDistrict.value = "Todos"
        _selectedCategory.value = "Todos"
        _maxPriceFilter.value = 1000.0
        _minRatingFilter.value = 0.0
        _searchQuery.value = ""
        applyFilters()
    }

    private fun applyFilters() {
        val q = _searchQuery.value.lowercase()
        val cat = _selectedCategory.value
        val dist = _selectedDistrict.value
        val maxPrice = _maxPriceFilter.value
        val minRating = _minRatingFilter.value

        _filteredTechnicians.value = otherTechnicians.filter { tech ->
            val matchesCategory = (cat == "Todos") || (tech.category.lowercase() == cat.lowercase())
            val matchesSearch = tech.name.lowercase().contains(q) || 
                                tech.category.lowercase().contains(q) || 
                                tech.locationDescription.lowercase().contains(q)
            val matchesDistrict = (dist == "Todos") || tech.locationDescription.lowercase().contains(dist.lowercase())
            
            val techMaxPrice = when (tech.id) {
                "liz_evelyn" -> 750.0
                "carlos_soto" -> 200.0
                "sofia_ruiz" -> 150.0
                "mateo_quispe" -> 1000.0
                "elena_huaman" -> 180.0
                "raul_carpintero" -> 900.0
                "ana_diseno" -> 500.0
                else -> 1000.0
            }
            val matchesPrice = techMaxPrice <= maxPrice
            val matchesRating = tech.rating >= minRating

            matchesCategory && (q.isEmpty() || matchesSearch) && matchesDistrict && matchesPrice && matchesRating
        }
    }

    // Geolocation Sim
    fun simulateGeolocation() {
        viewModelScope.launch {
            _isSimulatingLocation.value = true
            _userLocation.value = "Obteniendo ubicación actual por GPS..."
            kotlinx.coroutines.delay(1800)
            _userLocation.value = "Av. 3 de Octubre, Villa El Salvador, Lima"
            _isSimulatingLocation.value = false
            _showNearbyProfessionals.value = true
            
            // Sort technicians by distance
            _filteredTechnicians.value = otherTechnicians.sortedBy { it.distanceKm }
        }
    }

    // Chat with Liz Flow State
    val lizMessages: StateFlow<List<ChatMessage>> = repository.getChatMessages("Liz")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _lizMessageCount = MutableStateFlow(0)

    fun sendUserMessageToLiz(text: String) {
        if (text.trim().isEmpty()) return
        viewModelScope.launch {
            // Save user message
            repository.insertMessage(
                ChatMessage(sender = "user", text = text, recipient = "Liz")
            )
            
            val currentCount = _lizMessageCount.value + 1
            _lizMessageCount.value = currentCount

            // Wait a bit to simulate Liz typing
            kotlinx.coroutines.delay(1200)

            // Respond based on message number
            val responseText = when (currentCount) {
                1 -> "¡Hola! Buenas tardes. Con gusto te ayudo. ¿Qué tipo de evento deseas realizar?"
                2 -> "Perfecto. ¿Tienes alguna temática o colores de preferencia?"
                3 -> "Excelente elección. ¿La decoración sería para un local cerrado o un espacio al aire libre?"
                4 -> "Muy bien. Para poder cotizarte mejor, ¿qué elementos te interesan? Por ejemplo: arco de globos, mesa principal, fondo para fotografías, centros de mesa, letras luminosas, entre otros."
                else -> "¡Excelente! Déjame preparar una cotización personalizada basada en esos detalles. Puedes presionar el botón 'Reservar' arriba para iniciar la simulación de pago seguro."
            }

            repository.insertMessage(
                ChatMessage(sender = "provider", text = responseText, recipient = "Liz")
            )
        }
    }

    fun sendSecureFileToLiz(fileName: String, fileSize: String) {
        viewModelScope.launch {
            // Save secure file as user message
            repository.insertMessage(
                ChatMessage(sender = "user", text = "[SECURE_FILE:$fileName|$fileSize]", recipient = "Liz")
            )

            val currentCount = _lizMessageCount.value + 1
            _lizMessageCount.value = currentCount

            // Wait to simulate scanning and Liz responding
            kotlinx.coroutines.delay(1500)

            val responseText = when (currentCount) {
                1 -> "¡Hola! Buenas tardes. Con gusto te ayudo. ¿Qué tipo de evento deseas realizar?"
                2 -> "Perfecto. ¿Tienes alguna temática o colores de preferencia?"
                3 -> "Excelente elección. ¿La decoración sería para un local cerrado o un espacio al aire libre?"
                4 -> "Muy bien. Para poder cotizarte mejor, ¿qué elementos te interesan? Por ejemplo: arco de globos, mesa principal, fondo para fotografías, centros de mesa, letras luminosas, entre otros."
                else -> "¡Excelente! He recibido tu archivo de forma 100% segura. Déjame preparar un presupuesto formal detallado."
            }

            repository.insertMessage(
                ChatMessage(sender = "provider", text = responseText, recipient = "Liz")
            )

            // Simulate Liz sending a counter contract/invoice securely too!
            if (currentCount >= 4) {
                kotlinx.coroutines.delay(1200)
                repository.insertMessage(
                    ChatMessage(
                        sender = "provider",
                        text = "[SECURE_FILE:Presupuesto_Liz_Decoraciones.pdf|1.4 MB]",
                        recipient = "Liz"
                    )
                )
            }
        }
    }

    // Chatbot Support Flow State
    val supportMessages: StateFlow<List<ChatMessage>> = repository.getChatMessages("SupportBot")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var botFlowState: ChatBotState = ChatBotState.WaitOption

    // Keep memory of recorded complain during flow
    private var tempCompName = ""
    private var tempCompDni = ""
    private var tempCompMotive = ""
    private var tempCompDate = ""
    private var tempCompCategory = "Producto defectuoso"
    private var tempCompEmail = ""
    private var tempEvidenceAttached = false

    fun sendUserMessageToSupport(text: String) {
        if (text.trim().isEmpty()) return
        viewModelScope.launch {
            // Save user message
            repository.insertMessage(
                ChatMessage(sender = "user", text = text, recipient = "SupportBot")
            )

            kotlinx.coroutines.delay(1000)

            when (botFlowState) {
                ChatBotState.Idle -> {
                    // Start over
                    botFlowState = ChatBotState.WaitOption
                    repository.insertMessage(
                        ChatMessage(
                            sender = "bot",
                            text = "Por favor, selecciona una opción:\n1️⃣ Registrar una queja\n2️⃣ Consultar el estado de una queja\n3️⃣ Hablar con un asesor\n4️⃣ Salir",
                            recipient = "SupportBot"
                        )
                    )
                }
                ChatBotState.WaitOption -> {
                    val opt = text.trim()
                    if (opt == "1") {
                        botFlowState = ChatBotState.WaitName
                        repository.insertMessage(
                            ChatMessage(
                                sender = "bot",
                                text = "Entendido. Para registrar tu queja, por favor indícame tu nombre completo.",
                                recipient = "SupportBot"
                            )
                        )
                    } else if (opt == "2") {
                        botFlowState = ChatBotState.Summary // we can jump or show mock
                        repository.insertMessage(
                            ChatMessage(
                                sender = "bot",
                                text = "Para consultar el estado de tu queja, por favor indícame el código (e.g. Q-2026-001245):",
                                recipient = "SupportBot"
                            )
                        )
                    } else if (opt == "3") {
                        botFlowState = ChatBotState.Idle
                        repository.insertMessage(
                            ChatMessage(
                                sender = "bot",
                                text = "Conectando con un asesor en vivo... Por favor espera en línea. (Simulación: Todos los asesores están ocupados, por favor intenta más tarde o registra tu queja con la opción 1).",
                                recipient = "SupportBot"
                            )
                        )
                    } else {
                        botFlowState = ChatBotState.Idle
                        repository.insertMessage(
                            ChatMessage(
                                sender = "bot",
                                text = "Gracias por comunicarte con nosotros. Tu caso será atendido a la brevedad. Que tengas un excelente día.",
                                recipient = "SupportBot"
                            )
                        )
                    }
                }
                ChatBotState.WaitName -> {
                    tempCompName = text.trim()
                    botFlowState = ChatBotState.WaitDni
                    repository.insertMessage(
                        ChatMessage(
                            sender = "bot",
                            text = "Gracias, $tempCompName. Ahora, por favor, proporciona tu número de DNI o documento de identidad.",
                            recipient = "SupportBot"
                        )
                    )
                }
                ChatBotState.WaitDni -> {
                    tempCompDni = text.trim()
                    botFlowState = ChatBotState.WaitMotive
                    repository.insertMessage(
                        ChatMessage(
                            sender = "bot",
                            text = "Perfecto. Describe brevemente el motivo de tu queja.",
                            recipient = "SupportBot"
                        )
                    )
                }
                ChatBotState.WaitMotive -> {
                    tempCompMotive = text.trim()
                    botFlowState = ChatBotState.WaitDate
                    repository.insertMessage(
                        ChatMessage(
                            sender = "bot",
                            text = "Lamento el inconveniente. Para poder ayudarte mejor, indícame la fecha de compra.",
                            recipient = "SupportBot"
                        )
                    )
                }
                ChatBotState.WaitDate -> {
                    tempCompDate = text.trim()
                    botFlowState = ChatBotState.WaitEvidenceAsk
                    repository.insertMessage(
                        ChatMessage(
                            sender = "bot",
                            text = "Gracias. ¿Deseas adjuntar evidencia, como fotografías o comprobantes de compra? (Escribe: Sí / No)",
                            recipient = "SupportBot"
                        )
                    )
                }
                ChatBotState.WaitEvidenceAsk -> {
                    val response = text.trim().lowercase()
                    if (response == "sí" || response == "si" || response == "yes" || response == "s") {
                        tempEvidenceAttached = true
                        botFlowState = ChatBotState.WaitEvidenceAttach
                        repository.insertMessage(
                            ChatMessage(
                                sender = "bot",
                                text = "Por favor, adjunta los archivos correspondientes. [Presiona el botón de clip arriba para simular la subida]",
                                recipient = "SupportBot"
                            )
                        )
                    } else {
                        tempEvidenceAttached = false
                        botFlowState = ChatBotState.WaitCategory
                        askCategoryMessage()
                    }
                }
                ChatBotState.WaitEvidenceAttach -> {
                    // User attached files
                    botFlowState = ChatBotState.WaitCategory
                    repository.insertMessage(
                        ChatMessage(
                            sender = "bot",
                            text = "Archivos recibidos correctamente.",
                            recipient = "SupportBot"
                        )
                    )
                    kotlinx.coroutines.delay(600)
                    askCategoryMessage()
                }
                ChatBotState.WaitCategory -> {
                    val catOpt = text.trim()
                    tempCompCategory = when (catOpt) {
                        "1" -> "Producto defectuoso"
                        "2" -> "Retraso en entrega"
                        "3" -> "Cobro indebido"
                        "4" -> "Mala atención"
                        else -> "Otro"
                    }
                    botFlowState = ChatBotState.WaitEmailAsk
                    repository.insertMessage(
                        ChatMessage(
                            sender = "bot",
                            text = "Has seleccionado \"$tempCompCategory\".\n\nTu queja ha sido registrada con el código Q-2026-001245.\nEl plazo estimado de respuesta es de 5 días hábiles.\n\n¿Deseas recibir actualizaciones por correo electrónico? (Sí / No)",
                            recipient = "SupportBot"
                        )
                    )
                }
                ChatBotState.WaitEmailAsk -> {
                    val ans = text.trim().lowercase()
                    if (ans == "sí" || ans == "si" || ans == "yes" || ans == "s") {
                        botFlowState = ChatBotState.WaitEmail
                        repository.insertMessage(
                            ChatMessage(
                                sender = "bot",
                                text = "Por favor, ingresa tu correo electrónico.",
                                recipient = "SupportBot"
                            )
                        )
                    } else {
                        tempCompEmail = "No provisto"
                        finishComplaint()
                    }
                }
                ChatBotState.WaitEmail -> {
                    tempCompEmail = text.trim()
                    finishComplaint()
                }
                ChatBotState.Summary -> {
                    val ans = text.trim()
                    if (ans == "1" || ans.lowercase() == "sí" || ans.lowercase() == "si") {
                        botFlowState = ChatBotState.WaitOption
                        repository.insertMessage(
                            ChatMessage(
                                sender = "bot",
                                text = "¡Entendido! Selecciona una opción para continuar:\n1️⃣ Registrar una queja\n2️⃣ Consultar el estado de una queja\n3️⃣ Hablar con un asesor\n4️⃣ Salir",
                                recipient = "SupportBot"
                            )
                        )
                    } else {
                        botFlowState = ChatBotState.Idle
                        repository.insertMessage(
                            ChatMessage(
                                sender = "bot",
                                text = "Gracias por comunicarte con nosotros. Tu caso será atendido a la brevedad. Que tengas un excelente día.",
                                recipient = "SupportBot"
                            )
                        )
                    }
                }
            }
        }
    }

    fun simulateAttachmentInBot() {
        if (botFlowState == ChatBotState.WaitEvidenceAttach) {
            viewModelScope.launch {
                repository.insertMessage(
                    ChatMessage(sender = "user", text = "📎 [Archivo: Evidencia_Compra.jpg]", recipient = "SupportBot")
                )
                kotlinx.coroutines.delay(1000)
                botFlowState = ChatBotState.WaitCategory
                repository.insertMessage(
                    ChatMessage(
                        sender = "bot",
                        text = "Archivos recibidos correctamente.",
                        recipient = "SupportBot"
                    )
                )
                kotlinx.coroutines.delay(800)
                askCategoryMessage()
            }
        }
    }

    private suspend fun askCategoryMessage() {
        repository.insertMessage(
            ChatMessage(
                sender = "bot",
                text = "Ahora selecciona la categoría de tu queja:\n1️⃣ Producto defectuoso\n2️⃣ Retraso en entrega\n3️⃣ Cobro indebido\n4️⃣ Mala atención\n5️⃣ Otro",
                recipient = "SupportBot"
            )
        )
    }

    private suspend fun finishComplaint() {
        botFlowState = ChatBotState.Summary
        // Insert into database
        val complaint = Complaint(
            code = "Q-2026-001245",
            name = tempCompName.ifEmpty { "María Fernández" },
            dni = tempCompDni.ifEmpty { "76543210" },
            motive = tempCompMotive.ifEmpty { "Compré un producto hace una semana y llegó dañado." },
            purchaseDate = tempCompDate.ifEmpty { "10 de junio de 2026" },
            evidenceAttached = tempEvidenceAttached,
            category = tempCompCategory,
            email = tempCompEmail.ifEmpty { "mariaf@gmail.com" },
            status = "Registrada"
        )
        repository.insertComplaint(complaint)

        repository.insertMessage(
            ChatMessage(
                sender = "bot",
                text = """
                    Perfecto. Las actualizaciones serán enviadas a ${complaint.email}.
                    
                    📋 Resumen de tu solicitud:
                    🏷️ Código: ${complaint.code}
                    📂 Categoría: ${complaint.category}
                    📅 Fecha de compra: ${complaint.purchaseDate}
                    ⚡ Estado: Registrada
                    
                    ¿Necesitas algo más?
                    1️⃣ Sí
                    2️⃣ No
                """.trimIndent(),
                recipient = "SupportBot"
            )
        )
    }

    // Booking actions
    fun bookLizService(serviceName: String, rate: Double, paymentMethod: String) {
        viewModelScope.launch {
            val booking = Booking(
                providerName = "Liz Evelyn Obregon Olivera",
                serviceName = serviceName,
                date = "Reserva en curso - " + java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date()),
                totalPaid = rate,
                paymentMethod = paymentMethod,
                status = "En Escrow"
            )
            repository.insertBooking(booking)

            // Post transaction active notification!
            postNotification("🔒 Garantía Ayni Activa", "S/ %.2f depositados de forma segura en Escrow.".format(rate), isEscrow = true)

            // Insert confirmation message to chat
            repository.insertMessage(
                ChatMessage(
                    sender = "bot",
                    text = "🔒 ¡Garantía Escrow Depositada! Se han congelado S/ %.2f de forma segura. El dinero se encuentra resguardado por Ayni y se liberará tras tu conformidad.".format(rate),
                    recipient = "Liz"
                )
            )

            // Liz replies to this confirm
            kotlinx.coroutines.delay(1500)
            repository.insertMessage(
                ChatMessage(
                    sender = "provider",
                    text = "¡Excelente! He recibido la confirmación del depósito en garantía para la '$serviceName'. Ya estoy preparando los detalles de la decoración. ¡Muchas gracias por tu confianza! 😊",
                    recipient = "Liz"
                )
            )
            postNotification("💬 Mensaje de Liz", "¡Excelente! He recibido la confirmación del depósito...")
        }
    }

    // Escrow updates
    fun updateBookingStatus(booking: Booking, newStatus: String) {
        viewModelScope.launch {
            val updated = booking.copy(status = newStatus)
            repository.insertBooking(updated)

            // If the user releases funds ("Completado"), notify in chat!
            if (newStatus == "Completado") {
                postNotification("🎉 Transacción Finalizada", "Fondos liberados con éxito.", isEscrow = true)
                repository.insertMessage(
                    ChatMessage(
                        sender = "bot",
                        text = "🎉 ¡Fondos de S/ ${booking.totalPaid} liberados con éxito a Liz! Gracias por confiar en la Garantía Escrow de Ayni.",
                        recipient = "Liz"
                    )
                )
            } else if (newStatus == "Disputa") {
                postNotification("⚠️ Disputa de Escrow", "Se ha registrado un desacuerdo.", isEscrow = true)
                repository.insertMessage(
                    ChatMessage(
                        sender = "bot",
                        text = "⚠️ Se ha abierto una disputa de Escrow por S/ ${booking.totalPaid}. El equipo de Soporte Ayni revisará los detalles del caso en un plazo de 24 horas.",
                        recipient = "Liz"
                    )
                )
            }
        }
    }

    // Push Notifications Banner Flow
    private val _activeNotification = MutableStateFlow<NotificationEvent?>(null)
    val activeNotification = _activeNotification.asStateFlow()

    fun postNotification(title: String, message: String, isEscrow: Boolean = false) {
        viewModelScope.launch {
            _activeNotification.value = NotificationEvent(title, message, isEscrow)
            kotlinx.coroutines.delay(4000)
            if (_activeNotification.value?.title == title) {
                _activeNotification.value = null
            }
        }
    }

    fun dismissNotification() {
        _activeNotification.value = null
    }

    // Calendar Availability & Agenda
    private val _selectedCalendarDate = MutableStateFlow<Int?>(null)
    val selectedCalendarDate = _selectedCalendarDate.asStateFlow()

    val calendarBookings = listOf(3, 4, 10, 11, 17, 24, 25, 31) // Friday & Saturday busy days

    fun selectCalendarDate(day: Int) {
        _selectedCalendarDate.value = day
    }

    fun requestMeetingForDate(day: Int) {
        viewModelScope.launch {
            postNotification("📅 Agenda Ayni", "Solicitud de videollamada enviada para el $day de Julio.")
            repository.insertMessage(
                ChatMessage(
                    sender = "user",
                    text = "Hola Liz, he solicitado una videollamada de coordinación para el día $day de Julio de 2026 a las 4:00 PM.",
                    recipient = "Liz"
                )
            )
            kotlinx.coroutines.delay(1500)
            repository.insertMessage(
                ChatMessage(
                    sender = "provider",
                    text = "¡Hola! Perfecto, acabo de agendar nuestra videollamada de coordinación para el día $day de Julio a las 4:00 PM. ¡Nos vemos pronto! 😊",
                    recipient = "Liz"
                )
            )
            postNotification("💬 Mensaje de Liz", "¡Hola! Perfecto, acabo de agendar nuestra videollamada...")
        }
    }

    // Reviews Portfolio Social System
    private val _lizReviews = MutableStateFlow(listOf(
        Review(1, "Milagros Torres", 5.0, "Excelente servicio de decoración. Liz diseñó un arco de globos precioso para el bautizo de mi hijo en Villa El Salvador. Super puntual y profesional.", "12/06/2026", listOf("Princess party")),
        Review(2, "Juan Carlos R.", 4.8, "Muy responsable. Contraté la decoración temática de Masha y el Oso y el resultado superó las expectativas de todos los invitados. Recomendada 100%.", "28/05/2026", listOf("Masha and Bear")),
        Review(3, "Sofía Castro", 5.0, "La garantía Escrow de Ayni me dio muchísima seguridad. El pago se liberó recién cuando terminó de colocar el último globo. El servicio de Liz de primera.", "15/05/2026", listOf("Little Mermaid tail"))
    ))
    val lizReviews = _lizReviews.asStateFlow()

    fun addReview(authorName: String, rating: Double, text: String) {
        val newRev = Review(
            id = _lizReviews.value.size + 1,
            authorName = authorName,
            rating = rating,
            text = text,
            date = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())
        )
        _lizReviews.value = listOf(newRev) + _lizReviews.value
        postNotification("⭐ Reseña Publicada", "Gracias por dejar tu opinión sobre Liz Evelyn.")
    }
}

data class NotificationEvent(
    val title: String,
    val message: String,
    val isEscrow: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class Review(
    val id: Int,
    val authorName: String,
    val rating: Double,
    val text: String,
    val date: String,
    val photos: List<String> = emptyList()
)

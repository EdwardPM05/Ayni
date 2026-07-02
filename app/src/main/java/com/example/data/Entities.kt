package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val providerName: String,
    val serviceName: String,
    val date: String,
    val totalPaid: Double,
    val paymentMethod: String, // Yape or Plin
    val status: String, // "En Escrow", "Completado", "Disputa"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "complaints")
data class Complaint(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val code: String, // e.g. Q-2026-001245
    val name: String,
    val dni: String,
    val motive: String,
    val purchaseDate: String,
    val evidenceAttached: Boolean,
    val category: String,
    val email: String,
    val status: String, // "Registrada", "En Revisión", "Resuelta"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String, // "user" or "provider" or "bot"
    val text: String,
    val recipient: String, // e.g. "Liz", "SupportBot"
    val timestamp: Long = System.currentTimeMillis()
)

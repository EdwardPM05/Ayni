package com.example.data

import kotlinx.coroutines.flow.Flow

class AyniRepository(private val database: AppDatabase) {
    val bookingDao = database.bookingDao()
    val complaintDao = database.complaintDao()
    val chatMessageDao = database.chatMessageDao()

    val allBookings: Flow<List<Booking>> = bookingDao.getAllBookings()
    val allComplaints: Flow<List<Complaint>> = complaintDao.getAllComplaints()

    suspend fun insertBooking(booking: Booking) {
        bookingDao.insertBooking(booking)
    }

    suspend fun insertComplaint(complaint: Complaint) {
        complaintDao.insertComplaint(complaint)
    }

    suspend fun getComplaintByCode(code: String): Complaint? {
        return complaintDao.getComplaintByCode(code)
    }

    fun getChatMessages(recipient: String): Flow<List<ChatMessage>> {
        return chatMessageDao.getChatMessages(recipient)
    }

    suspend fun insertMessage(message: ChatMessage) {
        chatMessageDao.insertMessage(message)
    }

    suspend fun clearChat(recipient: String) {
        chatMessageDao.clearChat(recipient)
    }
}

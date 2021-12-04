package org.relaxindia.driver.model

data class ScheduleBookingModel(
    val user_id: Int,
    val user_name: String,
    val user_phone: String,
    val user_image: String,
    val from_location: String,
    val to_location: String,
    val schedule_date_time: String,
    val user_comment: String,
    val booking_amount: Double,
    val total_amount: Double,
    val status: String,
    val date: String,
)

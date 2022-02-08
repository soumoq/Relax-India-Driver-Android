package org.relaxindia.driver.model

import com.google.gson.annotations.SerializedName

data class GetDashboard(
    val total_earning: String,
    val total_journey: String,
    val avg_rating: String,
)

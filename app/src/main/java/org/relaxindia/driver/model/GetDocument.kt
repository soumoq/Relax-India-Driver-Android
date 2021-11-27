package org.relaxindia.driver.model

import com.google.gson.annotations.SerializedName

data class GetDocument(
    val image: String,
    val driving_licence: String,
    val id_proof: String,
    val ambulance_paper: String
)

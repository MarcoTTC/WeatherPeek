package br.com.marcottc.weatherpeek.model

// One Call 4.0 error payload, e.g. {"cod": 400, "message": "Invalid date format", "parameters": ["date"]}
data class ErrorResponse(
    var cod: Int,
    var message: String,
    // Names of the missing or invalid request parameters, sent along with some 400 errors
    var parameters: List<String>? = null
)

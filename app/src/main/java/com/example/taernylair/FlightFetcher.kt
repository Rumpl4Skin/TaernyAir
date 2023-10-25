package com.example.taernylair

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val BASE_URL = "http://kotlin-book.bignerdranch.com/2e"
private const val FLIGHT_ENDPOINT = "$BASE_URL/flight"
private const val LOYALTY_ENDPOINT = "$BASE_URL/loyalty"

fun main() {

    runBlocking {
        println("Started")
        launch {
            val flight = fetchFlight("Madrigal")
            println(flight)
        }
        println("Finished")
    }

}

suspend fun fetchFlight(passengerName: String): FlightStatus = coroutineScope {
    val client = HttpClient(CIO)

    println("Started fetching flight info")
    val flightResponse = async {
        println("Started fetching flight info")

        client.get<String>(FLIGHT_ENDPOINT).also {
            println("Finished fetching flight info")

        }
    }

    val loyaltyResponse = async {
        println("Started fetching loyalty info")
        client.get<String>(LOYALTY_ENDPOINT).also {
            println("Finished fetching loyalty info")
        }
    }

    delay(500)
    println("Combining flight data")
   FlightStatus.parse(
        passengerName = passengerName,
        flightResponse = flightResponse.await(),
        loyaltyResponse = loyaltyResponse.await()
    ).takeIf { it.status!="Canceled" }?:fetchFlight(passengerName)
}
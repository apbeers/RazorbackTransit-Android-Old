package razorbacktransit.arcu.razorbacktransit.model.bus

import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.Json

data class BusJson(@field:Json(name = "id") val id: String,
                   @field:Json(name = "fleet") val fleet: String,
                   @field:Json(name = "name") val name: String,
                   @field:Json(name = "description") val description: String,
                   @field:Json(name = "zonarId") val zonarId: String,
                   @field:Json(name = "gpsId") val gpsId: String,
                   @field:Json(name = "latitude") val latitude: Double,
                   @field:Json(name = "longitude") val longitude: Double,
                   @field:Json(name = "speed") val speed: Float,
                   @field:Json(name = "heading") val heading: Float,
                   @field:Json(name = "power") val power: Boolean,
                   @field:Json(name = "date") val date: String,
                   @field:Json(name = "color") val color: String?,
                   @field:Json(name = "routeName") val routeName: String?,
                   @field:Json(name = "routeId") val routeId: String?,
                   @field:Json(name = "distance") val distance: Double?,
                   @field:Json(name = "nextStop") val nextStop: String?,
                   @field:Json(name = "nextArrival") val nextArrival: String?)
{
    fun toBus() = Bus(
            id = id,
            fleet = fleet,
            name = name,
            description = description,
            zonarId = zonarId,
            gpsId = gpsId,
            coordinates = LatLng(latitude, longitude),
            speed = speed,
            heading = heading,
            power = power,
            date = date,
            color = color,
            routeName = routeName,
            routeId = routeId,
            distance = distance,
            nextStop = nextStop,
            nextArrival = nextArrival
    )
}
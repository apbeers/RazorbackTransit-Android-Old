package razorbacktransit.arcu.razorbacktransit.model.bus

import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.Json

data class BusJson(@Json(name = "id") val id: String,
                   @Json(name = "fleet") val fleet: String,
                   @Json(name = "name") val name: String,
                   @Json(name = "description") val description: String,
                   @Json(name = "zonarId") val zonarId: String,
                   @Json(name = "gpsId") val gpsId: String,
                   @Json(name = "latitude") val latitude: Double,
                   @Json(name = "longitude") val longitude: Double,
                   @Json(name = "speed") val speed: Float,
                   @Json(name = "heading") val heading: Float,
                   @Json(name = "power") val power: Boolean,
                   @Json(name = "date") val date: String,
                   @Json(name = "color") val color: String?,
                   @Json(name = "routeName") val routeName: String?,
                   @Json(name = "routeId") val routeId: String?,
                   @Json(name = "distance") val distance: Double?,
                   @Json(name = "nextStop") val nextStop: String?,
                   @Json(name = "nextArrival") val nextArrival: String?)
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
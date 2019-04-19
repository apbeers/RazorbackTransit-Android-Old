package razorbacktransit.arcu.razorbacktransit.model.stop

import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.Json

data class StopJson(@Json(name = "id") val id: String,
                    @Json(name = "name") val name: String,
                    @Json(name = "description") val description: String,
                    @Json(name = "latitude") val latitude: String,
                    @Json(name = "longitude") val longitude: String,
                    @Json(name = "order") val order: Int,
                    @Json(name = "distance") val distance: String?,
                    @Json(name = "nextArrival") val nextArrival: String?)
{
    fun toStop() = Stop(
            id = id,
            name = name,
            description = description,
            coordinates = LatLng(latitude.toDouble(), longitude.toDouble()),
            order = order,
            distance = distance,
            nextArrival = nextArrival
    )
}
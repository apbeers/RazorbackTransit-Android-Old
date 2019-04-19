package razorbacktransit.arcu.razorbacktransit.model.route

import android.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.Json

data class RouteJson(@field:Json(name = "id") val id: Int,
                 @field:Json(name = "name") val name: String,
                 @field:Json(name = "description") val description: String,
                 @field:Json(name = "color") val color: String,
                 @field:Json(name = "shape") val shape: String,
                 @field:Json(name = "status") val status: Int,
                 @field:Json(name = "inService") val inService: Int,
                 @field:Json(name = "url") val pdfUrl: String?,
                 @field:Json(name = "nextArrival") val nextArrival: String?,
                 @field:Json(name = "length") val length: Float,
                 @field:Json(name = "departureStop") val departureStop: Int,
                 @field:Json(name = "nextDeparture") val nextDeparture: String?)
{
    fun toRoute(): Route
    {
        val coords = parseCoordinates( this.shape )
        return Route(
                id = id,
                name = name,
                description = description,
                color = Color.parseColor( color ),
                coordinates = coords,
                status = status,
                inService = inService == 1,
                pdfUrl = pdfUrl,
                nextArrival = nextArrival,
                length = length,
                departureStop = departureStop,
                nextDeparture = nextDeparture
        )
    }

    private fun parseCoordinates( shape: String ): List<LatLng>
    {
        val pairs = shape.split(",")
        val list = arrayListOf<LatLng>()
        for(pair in pairs)
        {
            val latLongPair = pair.split(" ")
            list.add(  LatLng( latLongPair[0].toDouble(), latLongPair[1].toDouble() ) )

        }
        return list
    }
}
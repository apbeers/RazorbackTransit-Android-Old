package razorbacktransit.arcu.razorbacktransit.model.route

import android.graphics.Color
import com.google.android.gms.maps.model.LatLng

data class Route(val id: String,
                 val name: String,
                 val description: String,
                 val color: Int,
                 val coordinates: List<LatLng>,
                 val status: Int,
                 val inService: Int,
                 val pdfUrl: String?,
                 val nextArrival: String?,
                 val length: Float,
                 val departureStop: Int,
                 val nextDeparture: String?)
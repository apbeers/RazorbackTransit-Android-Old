package razorbacktransit.arcu.razorbacktransit.model.bus

import com.google.android.gms.maps.model.LatLng

data class Bus(val id: String,
               val fleet: String,
               val name: String,
               val description: String,
               val zonarId: String,
               val gpsId: String,
               val coordinates: LatLng,
               val speed: Float,
               val heading: Float,
               val power: Boolean,
               val date: String,
               val color: String?,
               val routeName: String?,
               val routeId: String?,
               val distance: Double?,
               val nextStop: String?,
               val nextArrival: String?)
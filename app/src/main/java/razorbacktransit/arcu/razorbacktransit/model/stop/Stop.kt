package razorbacktransit.arcu.razorbacktransit.model.stop

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng

class Stop(val id: String,
           val name: String,
           val description: String,
           val coordinates: LatLng,
           val order: Int,
           val distance: String?,
           val nextArrival: String?)
{
    @Transient var icon: BitmapDescriptor? = null
    @Transient var routeIds: String? = null
}
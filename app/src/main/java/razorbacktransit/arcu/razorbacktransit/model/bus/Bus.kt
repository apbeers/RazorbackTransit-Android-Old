package razorbacktransit.arcu.razorbacktransit.model.bus

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import com.squareup.picasso.Target
import razorbacktransit.arcu.razorbacktransit.model.busimage.BusMarker
import java.lang.Exception

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
               val nextArrival: String?): Target
{
    override fun onPrepareLoad(placeHolderDrawable: Drawable?)
    {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?)
    {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?)
    {
        icon = BitmapDescriptorFactory.fromBitmap( bitmap )
    }

    @Transient var requestCreator: RequestCreator? = null
    @Transient var icon: BitmapDescriptor? = null
}
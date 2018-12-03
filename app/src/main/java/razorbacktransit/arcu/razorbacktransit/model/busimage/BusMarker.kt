package razorbacktransit.arcu.razorbacktransit.model.busimage

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

class BusMarker( val marker: MarkerOptions ): Target
{
    override fun onPrepareLoad(placeHolderDrawable: Drawable?)
    {

    }

    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?)
    {

    }

    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?)
    {
        marker.icon( BitmapDescriptorFactory.fromBitmap( bitmap ) )
    }

    override fun hashCode(): Int
    {
        return marker.hashCode()
    }

    override fun equals(other: Any?): Boolean
    {
        return if(other is BusMarker)
        {
            val otherMarker = other.marker
            return marker == otherMarker
        }
        else
            false
    }
}
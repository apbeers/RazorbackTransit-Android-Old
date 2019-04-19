package razorbacktransit.arcu.razorbacktransit.utils

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import io.reactivex.Flowable
import razorbacktransit.arcu.razorbacktransit.model.route.Route
import razorbacktransit.arcu.razorbacktransit.network.NetworkState

fun Flowable<NetworkState>.logNetworkState( methodName: String ): Flowable<NetworkState>
{
    return this.doOnNext {
        when(it)
        {
            is NetworkState.Failure ->
            {
                Log.d("NETWORKDEBUGGING", "$methodName: FAILURE, ${it.t.localizedMessage}")
                it.t.printStackTrace()
            }
            is NetworkState.InTransit -> Log.d("NETWORKDEBUGGING", "$methodName: IN TRANSIT")
            is NetworkState.Success -> Log.d("NETWORKDEBUGGING", "$methodName: SUCCESS")
        }
    }

}

fun ArrayList<Marker>.clearMarkers()
{
    for(marker in this)
    {
        marker.remove()
    }
    this.clear()
}

fun Drawable.toBitMapDescriptor(width: Int, height: Int): BitmapDescriptor
{
    return BitmapDescriptorFactory.fromBitmap(this.toBitmap(width, height))
}

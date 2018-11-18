package razorbacktransit.arcu.razorbacktransit

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import razorbacktransit.arcu.razorbacktransit.model.bus.Bus
import razorbacktransit.arcu.razorbacktransit.model.route.Route
import razorbacktransit.arcu.razorbacktransit.network.TransitStream
import razorbacktransit.arcu.razorbacktransit.utils.clearMarkers
import java.util.*

class LiveMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener
{
    private var mListener: OnFragmentInteractionListener? = null
    private lateinit var googleMap: GoogleMap
    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    private val stopMarkerHashMap = HashMap<Marker, String>()
    private val markers = arrayListOf<Marker>()
    private val routeIds = arrayListOf<String>()

    private var stopImageWidth: Int = 0
    private var stopImageHeight: Int = 0
    private var busImageWidth: Int = 0
    private var busImageHeight: Int = 0
    private val disposables = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        sharedPreferences = activity!!.getPreferences(Context.MODE_PRIVATE)
        editor = sharedPreferences!!.edit()

        val widthPixels = activity!!.resources.displayMetrics.widthPixels

        stopImageWidth = (widthPixels * 0.02638888889).toInt()
        stopImageHeight = (widthPixels * 0.02638888889).toInt()

        busImageWidth = (widthPixels * 0.05833333333).toInt()
        busImageHeight = (widthPixels.toDouble() * 0.05833333333 * 1.6153846154).toInt()

        // Update the UI for the routes
        disposables += TransitStream.routes
                .map { it.filter { it.name == "green" } }
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( this::updateRoutes )

        // Update the UI for the buses
        disposables += TransitStream.buses
                .map { buses -> buses.filter { bus -> routeIds.contains( bus.id ) } }
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( this::updateBusses )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {

        val view = inflater.inflate(R.layout.fragment_maps, container, false)
        val mapFragment = this.childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        return view
    }

    override fun onResume()
    {
        super.onResume()

        if( this::googleMap.isInitialized )
        {
            googleMap.clear()
        }
    }

    override fun onPause()
    {
        super.onPause()
        editor!!.apply()
    }

    override fun onAttach( context: Context? )
    {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener)
        {
            mListener = context
        }
        else
        {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach()
    {
        super.onDetach()
        mListener = null
        disposables.dispose()
    }

    override fun onMapReady( map: GoogleMap )
    {
        googleMap = map
        googleMap.apply {
            moveCamera( CameraUpdateFactory.newLatLng( LatLng(36.09, -94.1785) ) )
            moveCamera(CameraUpdateFactory.zoomTo(12.0f))
            setMinZoomPreference(10f)
            setOnMarkerClickListener(this@LiveMapFragment)
            uiSettings.isRotateGesturesEnabled = true
            uiSettings.isTiltGesturesEnabled = false
        }
    }

    @MainThread
    private fun updateRoutes( routes: List<Route> )
    {
        for (route in routes)
        {
            if (route.coordinates.size > 1)
            {
                val polylineOptions = PolylineOptions().color( route.color )
                val polyline = googleMap.addPolyline(polylineOptions)
                polyline.points = route.coordinates
            }
            if (!routeIds.contains(route.id))
            {
                routeIds.add( route.id )
            }
        }
    }

    @MainThread
    private fun updateBusses( busses: List<Bus> )
    {
        markers.clearMarkers()
        for( bus in busses )
        {
            // This section is all about refreshing the markers on the google map
            val markerOptions = MarkerOptions()
                    .position(bus.coordinates)
                    .flat(true)
                    .alpha(0f)
                    .title(bus.routeName)

            val marker = googleMap.addMarker(markerOptions)
            markers += marker

            // This section is about refreshing the image
            val key = getBusImageKey( bus.heading.toString(), bus.name )
            val encodedImage = sharedPreferences?.getString(key, "") ?: ""

            // If we already have the image, no need for a network request
            if (encodedImage != "")
            {
                val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap)
                marker.setIcon(bitmapDescriptor)
                marker.alpha = 1f
            }
            // If we do NOT already have the image, fire off another network request to get it
            else
            {
//                disposables += TransitStream.getBusImagesStream( bus.color!!, bus.heading.toString() )
//                        .subscribe()
//                            var color = ""
//                            var heading = ""
//                            val options = mapOf("color" to color, "heading" to heading)
//                            Flowable.just(StartEvent())
//                                    .flatMap {
//                                        campusAPI.getBusImages(options)
//                                                .map<NetworkState> { list -> NetworkState.Success.BusImages(list) }
//                                                .onErrorReturn { t -> NetworkState.Failure(t) }
//                                                .observeOn(AndroidSchedulers.mainThread())
//                                                .startWith(NetworkState.InTransit())
//                                    }
//                                    .logNetworkState("loading bus images in loadBusses()")
//                                    .ofType(NetworkState.Success.BusImages::class.java)
//                                    .subscribe {
//
//                                    }
            }
        }
    }

    private fun getBusImageKey( heading: String, name: String ): String
    {
        val x: Double = heading.toDouble()
        return name + Integer.toString(( x.toInt() + 29 ) / 30 * 30)
    }

    override fun onMarkerClick( marker: Marker ): Boolean = false

    interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object
    {

        fun newInstance(param1: String, param2: String): LiveMapFragment
        {
            return LiveMapFragment()
        }
    }
}
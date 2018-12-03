package razorbacktransit.arcu.razorbacktransit

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import razorbacktransit.arcu.razorbacktransit.model.bus.Bus
import razorbacktransit.arcu.razorbacktransit.model.route.Route
import razorbacktransit.arcu.razorbacktransit.network.TransitStream
import razorbacktransit.arcu.razorbacktransit.utils.clearMarkers
import java.util.concurrent.TimeUnit


class LiveMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener
{
    private var mListener: OnFragmentInteractionListener? = null
    private lateinit var googleMap: GoogleMap
    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    private val markers = arrayListOf<Marker>()

    private var stopImageWidth: Int = 0
    private var stopImageHeight: Int = 0
    private val disposables = CompositeDisposable()

    private val updateBusesNotification = Flowable.interval(5, TimeUnit.SECONDS, Schedulers.io()).startWith(0).publish().refCount()


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        sharedPreferences = activity!!.getPreferences(Context.MODE_PRIVATE)
        editor = sharedPreferences!!.edit()

        val widthPixels = activity!!.resources.displayMetrics.widthPixels

        stopImageWidth = (widthPixels * 0.02638888889).toInt()
        stopImageHeight = (widthPixels * 0.02638888889).toInt()


        disposables += TransitStream.routes
                .flatMapIterable { it }
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( this::updateRoutes )

        disposables += updateBusesNotification
                .flatMap { TransitStream.getBusses( context!! ) }
                .observeOn( AndroidSchedulers.mainThread() )
                .doOnNext { markers.clearMarkers() }
                .observeOn( Schedulers.computation() )
                .flatMapIterable { it }
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

        if (this::googleMap.isInitialized)
        {
            markers.clearMarkers()
            googleMap.clear()
        }
    }

    override fun onPause()
    {
        super.onPause()
        editor!!.apply()
    }

    override fun onAttach(context: Context?)
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

    override fun onMapReady(map: GoogleMap)
    {
        googleMap = map
        googleMap.apply {
            moveCamera(CameraUpdateFactory.newLatLng(LatLng(36.09, -94.1785)))
            moveCamera(CameraUpdateFactory.zoomTo(12.0f))
            setMinZoomPreference(10f)
            setOnMarkerClickListener(this@LiveMapFragment)
            uiSettings.isRotateGesturesEnabled = true
            uiSettings.isTiltGesturesEnabled = false
        }
    }

    @MainThread
    private fun updateRoutes(route: Route)
    {
        if (route.coordinates.size > 1)
        {
            val polylineOptions = PolylineOptions().color(route.color)
            val polyline = googleMap.addPolyline(polylineOptions)
            polyline.points = route.coordinates
        }
    }

    @MainThread
    private fun updateBusses(bus: Bus)
    {
        // if two markers have the same id but there has been an update, remove the old one and replace it with the new one
        val markerOptions: MarkerOptions = MarkerOptions()
                .title( bus.routeName )
                .position( bus.coordinates )
                .icon( bus.icon )
                .flat(true)
                .alpha(0f)
        markers += googleMap.addMarker( markerOptions ).apply { alpha = 1f }
    }

    override fun onMarkerClick(marker: Marker): Boolean = false

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
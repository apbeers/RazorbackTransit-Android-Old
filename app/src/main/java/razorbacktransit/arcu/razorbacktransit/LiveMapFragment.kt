package razorbacktransit.arcu.razorbacktransit

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import razorbacktransit.arcu.razorbacktransit.model.route.Route
import razorbacktransit.arcu.razorbacktransit.model.stop.Stop
import razorbacktransit.arcu.razorbacktransit.network.LiveMapViewModel
import razorbacktransit.arcu.razorbacktransit.utils.clearMarkers


class LiveMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener
{
    private var mListener: OnFragmentInteractionListener? = null
    private var googleMap: GoogleMap? = null

    private var busMarkers = arrayListOf<Marker>()
    private val stopMarkers = arrayListOf<Marker>()

    private val disposables = CompositeDisposable()

    private lateinit var viewModel: LiveMapViewModel

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LiveMapViewModel::class.java)
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
        Log.d("DEBUGGING", "onResume()")

        disposables += viewModel.routes
                .flatMapIterable { it }
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( this::updateRoutes )

        disposables += viewModel.getStops()
                .flatMapIterable { it }
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( this::updateStops )

        disposables += viewModel.getBusses()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( this::updateBusses )

        if (googleMap != null)
        {
            busMarkers.clearMarkers()
            googleMap?.clear()
        }
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
        disposables.clear()
    }

    override fun onMapReady(map: GoogleMap)
    {
        googleMap = map
        googleMap?.apply {
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
            val polyline = googleMap!!.addPolyline(polylineOptions)
            polyline.points = route.coordinates
        }
    }

    @MainThread
    private fun updateBusses(markers: List<MarkerOptions>)
    {
        val newMarkers = ArrayList<Marker>()
        for(marker in markers)
        {
            newMarkers += googleMap!!.addMarker(marker).apply { alpha = 1f }
        }
        busMarkers.clearMarkers()
        busMarkers = newMarkers
    }

    @MainThread
    private fun updateStops(stop: Stop)
    {
        val markerOptions: MarkerOptions = MarkerOptions()
                .snippet( stop.name )
                .title( stop.nextArrival )
                .position( stop.coordinates )
                .icon( stop.icon )
                .flat(true)
                .alpha(0f)

        stopMarkers += googleMap!!.addMarker( markerOptions ).apply { alpha = 1f }
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
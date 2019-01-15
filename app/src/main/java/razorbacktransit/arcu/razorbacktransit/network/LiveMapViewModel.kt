package razorbacktransit.arcu.razorbacktransit.network

import android.app.Application
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.picasso.Picasso
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.HttpUrl
import razorbacktransit.arcu.razorbacktransit.model.bus.Bus
import razorbacktransit.arcu.razorbacktransit.model.bus.BusJsonAdapter
import razorbacktransit.arcu.razorbacktransit.model.route.Route
import razorbacktransit.arcu.razorbacktransit.model.route.RouteJsonAdapter
import razorbacktransit.arcu.razorbacktransit.model.stop.Stop
import razorbacktransit.arcu.razorbacktransit.model.stop.StopJsonAdapter
import razorbacktransit.arcu.razorbacktransit.utils.buildBusIdsString
import razorbacktransit.arcu.razorbacktransit.utils.buildStopIdsString
import razorbacktransit.arcu.razorbacktransit.utils.logNetworkState
import razorbacktransit.arcu.razorbacktransit.utils.toBitMapDescriptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class LiveMapViewModel(application: Application): AndroidViewModel(application)
{
    private val applicationContext = application.applicationContext
    private val moshiAdapter = Moshi.Builder()
            .add(RouteJsonAdapter())
            .add(BusJsonAdapter())
            .add(StopJsonAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()

    private val campusAPI = Retrofit.Builder()
            .baseUrl("https://campusdata.uark.edu/api/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .addConverterFactory(MoshiConverterFactory.create(moshiAdapter).asLenient())
            .build()
            .create(CampusService::class.java)

    val routes: Flowable<List<Route>> = Flowable.just(StartEvent())
            .flatMap {
                campusAPI.getRoutes()
                        .map<NetworkState> { routes: List<Route> -> NetworkState.Success.Routes(routes) }
                        .onErrorReturn { t -> NetworkState.Failure(t) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .startWith(NetworkState.InTransit())
                        .logNetworkState("loadRoutes()")
            }
            .observeOn(Schedulers.computation())
            .ofType(NetworkState.Success.Routes::class.java)
            .map { it.busRoutes.filter { bus -> bus.inService } }
            .share()

    fun getBusses(): Flowable<List<Bus>>
    {
        val widthPixels = applicationContext.resources.displayMetrics.widthPixels
        return routes.observeOn( Schedulers.computation() )
                .doOnNext { Log.d("NETWORKDEBUGGING", "ATTEMPTING TO BUILD ID'S") }
                .buildBusIdsString()
                .doOnNext{ Log.d("NETWORKDEBUGGING", it) }
                .observeOn( Schedulers.io() )
                // Load the busses
                .flatMap<NetworkState> { ids: String ->
                    campusAPI.getBuses(ids)
                            .map<NetworkState> { busses: List<Bus> -> NetworkState.Success.Busses(busses) }
                            .onErrorReturn { t -> NetworkState.Failure(t) }
                            .observeOn(AndroidSchedulers.mainThread())
                            .startWith(NetworkState.InTransit())
                }
                .logNetworkState("getBusses(): Loading from /stops")
                .observeOn( Schedulers.computation() )
                .ofType(NetworkState.Success.Busses::class.java)
                .map { it.busses }
                .observeOn( Schedulers.io() )
                // Load the images into the busses
                .flatMap { busses: List<Bus> ->
                    Flowable.fromIterable( busses )
                            .map {
                                it.apply {
                                    Log.d("NETWORKDEBUGGING", "${it.name}, Color: ${it.color}")
                                    val busImageWidth = (widthPixels * 0.05833333333).toInt()
                                    val busImageHeight = (widthPixels.toDouble() * 0.05833333333 * 1.6153846154).toInt()
                                    Log.d("NETWORKDEBUGGING", getBusImageUrl( color!!, heading.toString() ))
                                    this.icon = Glide.with(applicationContext).load(getBusImageUrl(color, heading.toString())).submit().get().toBitMapDescriptor(busImageWidth, busImageHeight)
                                }
                            }
                            .toList()
                            .toFlowable()
                            .map <NetworkState> { NetworkState.Success.Busses( it ) }
                            .onErrorReturn { NetworkState.Failure(it) }
                            .startWith( NetworkState.InTransit() )
                }
                .logNetworkState("getStops(): Loading from /stopimages")
                .ofType( NetworkState.Success.Busses::class.java )
                .map { it.busses }
                .share()
    }

    fun getStops(): Flowable<List<Stop>>
    {
        val widthPixels = applicationContext.resources.displayMetrics.widthPixels
        return routes.observeOn( Schedulers.computation() )
                .buildStopIdsString()
                .observeOn( Schedulers.io() )
                .flatMap { ids: String ->
                    campusAPI.getStops( ids )
                            .map<NetworkState> { stops: List<Stop> -> NetworkState.Success.Stops( stops.onEach { it.routeIds = ids } ) }
                            .onErrorReturn{ t -> NetworkState.Failure( t ) }
                            .observeOn( AndroidSchedulers.mainThread() )
                            .startWith( NetworkState.InTransit() )
                }
                .logNetworkState("getStops()")
                .observeOn(Schedulers.computation())
                .ofType(NetworkState.Success.Stops::class.java)
                .map { it.stops }
                .observeOn( Schedulers.io() )
                .switchMap {stops: List<Stop> ->
                    Flowable.fromIterable( stops )
                            .map {
                                it.apply {
                                    val stopImageWidth = (widthPixels * 0.02638888889).toInt()
                                    val stopImageHeight = (widthPixels * 0.02638888889).toInt()
                                    this.icon = Glide.with(applicationContext).load(getStopImageUrl(this.id, this.routeIds!!)).submit().get().toBitMapDescriptor(stopImageWidth, stopImageHeight)
                                }
                            }
                            .toList()
                            .toFlowable()
                }
                .share()
    }

    private fun getBusImageUrl(color: String, heading: String): String = HttpUrl.Builder()
            .scheme("https")
            .host("campusdata.uark.edu")
            .addPathSegment("api")
            .addPathSegment("busimages")
            .addQueryParameter("color", color.replace("#", ""))
            .addQueryParameter("heading", heading)
            .build()
            .uri()
            .toASCIIString()

    private fun getStopImageUrl( stopId: String, routeIds: String ): String = HttpUrl.Builder()
            .scheme("https")
            .host("campusdata.uark.edu")
            .addPathSegment("api")
            .addPathSegment("stopimages")
            .addQueryParameter("stopId", stopId)
            .addQueryParameter("routeIds", routeIds)
            .build()
            .uri()
            .toASCIIString()
}
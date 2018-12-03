package razorbacktransit.arcu.razorbacktransit.network

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.model.BitmapDescriptor
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
import razorbacktransit.arcu.razorbacktransit.utils.logNetworkState
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object TransitStream
{
    private val moshiAdapter = Moshi.Builder()
            .add(RouteJsonAdapter())
            .add(BusJsonAdapter())
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

    fun getBusses(context: Context): Flowable<List<Bus>>
    {
        val widthPixels = context.resources.displayMetrics.widthPixels
        val picasso = Picasso.Builder(context).build()
        return routes.observeOn( Schedulers.computation() )
                .map<List<String>> { it.map { bus -> bus.id } }
                .map<String> { ids: List<String> ->
                    var idString = ""
                    for (id in ids)
                    {
                        idString += "$id-"
                    }
                    Log.d("DEBUGGING", "Request String ${idString.substring(0, idString.lastIndex - 1)}")
                    return@map idString.substring(0, idString.lastIndex - 1)
                }
                .observeOn( Schedulers.io() )
                // Load the busses
                .flatMap<NetworkState> { ids: String ->
                    campusAPI.getBuses(ids)
                            .map<NetworkState> { busses: List<Bus> -> NetworkState.Success.Busses(busses) }
                            .onErrorReturn { t -> NetworkState.Failure(t) }
                            .observeOn(AndroidSchedulers.mainThread())
                            .startWith(NetworkState.InTransit())
                }
                .logNetworkState("getBusses()")
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
                                    Log.d("NETWORKDEBUGGING", getImageUrl( color!!, heading.toString() ))
                                    this.icon = BitmapDescriptorFactory.fromBitmap( picasso.load( getImageUrl( color!!, heading.toString() ) ).resize(busImageWidth, busImageHeight).get() )
                                }
                            }
                            .toList()
                            .toFlowable()
                }
                .share()
    }
    private fun getImageUrl(color: String, heading: String) = HttpUrl.Builder()
            .scheme("https")
            .host("campusdata.uark.edu")
            .addPathSegment("api")
            .addPathSegment("busimages")
            .addQueryParameter("color", color)
            .addQueryParameter("heading", heading)
            .build()
            .uri()
            .toASCIIString()
            .replace("%23", "")
            .replace("#", "")
}
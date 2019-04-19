package razorbacktransit.arcu.razorbacktransit.network

import android.content.Context
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import razorbacktransit.arcu.razorbacktransit.model.bus.Bus
import razorbacktransit.arcu.razorbacktransit.model.bus.BusJsonAdapter
import razorbacktransit.arcu.razorbacktransit.model.route.Route
import razorbacktransit.arcu.razorbacktransit.model.route.RouteJsonAdapter
import razorbacktransit.arcu.razorbacktransit.model.stop.Stop
import razorbacktransit.arcu.razorbacktransit.model.stop.StopJsonAdapter
import razorbacktransit.arcu.razorbacktransit.utils.toBitMapDescriptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class RouteNetworking(private val applicationContext: Context)
{
    private val widthPixels: Int = applicationContext.resources.displayMetrics.widthPixels

    private val moshiAdapter = Moshi.Builder()
            .add(RouteJsonAdapter())
            .add(BusJsonAdapter())
            .add(StopJsonAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()

    private val campusAPI: CampusService = Retrofit.Builder()
            .baseUrl("https://campusdata.uark.edu/api/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .addConverterFactory(MoshiConverterFactory.create(moshiAdapter).asLenient())
            .build()
            .create(CampusService::class.java)

    val routesEndpoint: FlowableTransformer<Unit, NetworkState> = FlowableTransformer {
        it.observeOn(Schedulers.io())
                .flatMap {
                    campusAPI.getRoutes()
                            .map<NetworkState> { routes: List<Route> -> NetworkState.Success.Routes(routes) }
                            .onErrorReturn { t -> NetworkState.Failure(t) }
                            .observeOn(AndroidSchedulers.mainThread())
                            .startWith(NetworkState.InTransit())
                }
    }

    val buses: FlowableTransformer<NetworkState.Success.Routes, SubmitUiModel.SubmitBuses> = FlowableTransformer {
        it.compose(busesEndpoint)
                .ofType(NetworkState.Success.Buses::class.java)
                .compose(busImagesEndpoint)
    }

    private val busesEndpoint: FlowableTransformer<NetworkState.Success.Routes, NetworkState> = FlowableTransformer {
        it.map { it.busRoutes }
                .buildBusIdsString()
                .flatMap<NetworkState> { busIds: String ->
                    campusAPI.getBuses(busIds)
                            .map<NetworkState> { busses: List<Bus> -> NetworkState.Success.Buses(busses) }
                            .onErrorReturn { t -> NetworkState.Failure(t) }
                            .observeOn(AndroidSchedulers.mainThread())
                            .startWith(NetworkState.InTransit())
                }
    }

    private val busImagesEndpoint: FlowableTransformer<NetworkState.Success.Buses, SubmitUiModel.SubmitBuses> = FlowableTransformer {
        it.observeOn( Schedulers.computation() )
                .map { it.buses }
                .observeOn( Schedulers.io() )
                // Load the images into the busses
                .flatMap { busses: List<Bus> ->
                    Flowable.fromIterable( busses )
                            .map {
                                it.apply {
                                    if(it.color != null)
                                    {
                                        val busImageWidth = (widthPixels * 0.05833333333).toInt()
                                        val busImageHeight = (widthPixels.toDouble() * 0.05833333333 * 1.6153846154).toInt()
                                        val myIcon = Glide.with(applicationContext).load(getBusImageUrl(it.color, heading.toString())).submit().get().toBitMapDescriptor(busImageWidth, busImageHeight)
                                        it.icon = MarkerOptions()
                                                .title( it.routeName )
                                                .position( it.coordinates )
                                                .icon( myIcon )
                                                .flat(true)
                                                .alpha(0f)
                                    }
                                }
                            }
                            .observeOn(Schedulers.computation())
                            .toList().toFlowable()
                            .map { buses -> return@map SubmitUiModel.SubmitBuses(buses) }
                }
    }

    val stops: FlowableTransformer<NetworkState.Success.Routes, SubmitUiModel.SubmitStops> = FlowableTransformer {
        it.compose(loadStops)
                .ofType(NetworkState.Success.Stops::class.java)
                .compose(stopImages)
    }

    private val loadStops: FlowableTransformer<NetworkState.Success.Routes, NetworkState> = FlowableTransformer {
        it.map { it.busRoutes }
                .buildStopIdsString()
                .observeOn( Schedulers.io() )
                .flatMap { ids: String ->
                    campusAPI.getStops( ids )
                            .map<NetworkState> { stops: List<Stop> -> NetworkState.Success.Stops( stops.onEach { it.routeIds = ids } ) }
                            .onErrorReturn{ t -> NetworkState.Failure( t ) }
                            .observeOn( AndroidSchedulers.mainThread() )
                            .startWith( NetworkState.InTransit() )
                }
    }

    private val stopImages: FlowableTransformer<NetworkState.Success.Stops, SubmitUiModel.SubmitStops> = FlowableTransformer {
        it.observeOn(Schedulers.computation())
                .map { it.stops }
                .observeOn( Schedulers.computation() )
                .switchMap {stops: List<Stop> ->
                    Flowable.fromIterable( stops )
                            .map {
                                val stopImageWidth = (widthPixels * 0.02638888889).toInt()
                                val stopImageHeight = (widthPixels * 0.02638888889).toInt()
                                val myIcon = Glide.with(applicationContext).load(getStopImageUrl(it.id, it.routeIds!!)).submit().get().toBitMapDescriptor(stopImageWidth, stopImageHeight)
                                it.icon = MarkerOptions()
                                        .snippet( it.name )
                                        .title( it.nextArrival )
                                        .position( it.coordinates )
                                        .icon( myIcon )
                                        .flat(true)
                                        .alpha(0f)
                                return@map it
                            }
                            .toList()
                            .toFlowable()
                            .map { return@map SubmitUiModel.SubmitStops(it) }
                }
    }

}
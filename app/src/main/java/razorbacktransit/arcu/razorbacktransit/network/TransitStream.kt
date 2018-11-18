package razorbacktransit.arcu.razorbacktransit.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import razorbacktransit.arcu.razorbacktransit.model.bus.Bus
import razorbacktransit.arcu.razorbacktransit.model.bus.BusJsonAdapter
import razorbacktransit.arcu.razorbacktransit.model.busimage.BusImage
import razorbacktransit.arcu.razorbacktransit.model.route.Route
import razorbacktransit.arcu.razorbacktransit.model.route.RouteJsonAdapter
import razorbacktransit.arcu.razorbacktransit.utils.logNetworkState
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object TransitStream
{
    private val moshiAdapter = Moshi.Builder()
            .add( RouteJsonAdapter() )
            .add( BusJsonAdapter() )
            .add( KotlinJsonAdapterFactory() )
            .build()

    private val campusAPI = Retrofit.Builder()
            .baseUrl( "https://campusdata.uark.edu/api/" )
            .addCallAdapterFactory( RxJava2CallAdapterFactory.createWithScheduler( Schedulers.io() ) )
            .addConverterFactory( MoshiConverterFactory.create( moshiAdapter ).asLenient() )
            .build()
            .create( CampusService::class.java )

    val routes: Flowable<List<Route>> = Flowable.just( StartEvent() )
            .flatMap {
                campusAPI.getRoutes()
                        .map<NetworkState> { routes: List<Route> -> NetworkState.Success.Routes(routes) }
                        .onErrorReturn { t -> NetworkState.Failure(t) }
                        .observeOn( AndroidSchedulers.mainThread() )
                        .startWith( NetworkState.InTransit() )
            }
            .logNetworkState("loadRoutes()")
            .ofType( NetworkState.Success.Routes::class.java )
            .map { it.busRoutes }

    val buses: Flowable<List<Bus>> = Flowable.interval(5, TimeUnit.SECONDS)
            .flatMap {
                campusAPI.getAllBuses()
                        .map<NetworkState> { busses: List<Bus> -> NetworkState.Success.Busses(busses) }
                        .onErrorReturn { t -> NetworkState.Failure(t) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .startWith( NetworkState.InTransit() )
            }
            .logNetworkState("loadBusses()")
            .ofType( NetworkState.Success.Busses::class.java )
            .map { it.busses }
            .filter { it.isNotEmpty() }
            .doOnNext {  }
            .publish()
            .refCount()

    fun getBusImagesStream( color: String, heading: String ): Flowable<BusImage>
    {
        val map = HashMap<String, String>()
        map["color"] = color
        map["heading"] = heading
        return Flowable.just( StartEvent() )
                .flatMap {
                    campusAPI.getBusImages( map )
                            .map<NetworkState> { routes: List<BusImage> -> NetworkState.Success.BusImages(routes) }
                            .onErrorReturn { t -> NetworkState.Failure(t) }
                            .observeOn( AndroidSchedulers.mainThread() )
                            .startWith( NetworkState.InTransit() )
                }
                .map { BusImage() }
    }
}
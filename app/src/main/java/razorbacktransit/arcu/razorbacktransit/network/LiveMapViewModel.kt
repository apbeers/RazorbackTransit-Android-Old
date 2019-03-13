package razorbacktransit.arcu.razorbacktransit.network

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class LiveMapViewModel(application: Application): AndroidViewModel(application)
{
    private val applicationContext = application.applicationContext
    private val routeNetworking = RouteNetworking(applicationContext)
    private val updateBusesNotification = Flowable.interval(5, TimeUnit.SECONDS, Schedulers.io())
            .map { Unit }
            .share()

    private val routes: Flowable<NetworkState> = updateBusesNotification
            .compose(routeNetworking.routesEndpoint)
            .map<NetworkState> {
                if(it is NetworkState.Success.Routes)
                {
                    it.busRoutes = it.busRoutes.filter{ route -> listOf(221, 223, 227).contains(route.id) }
                }
                return@map it
            }
            .share()

    private val routesUi: Flowable<SubmitUiModel.SubmitRoute> = routes
            .ofType(NetworkState.Success.Routes::class.java)
            .map { return@map SubmitUiModel.SubmitRoute(it.busRoutes) }
            .distinctUntilChanged()

    private val buses: Flowable<SubmitUiModel.SubmitBuses> = routes
            .ofType(NetworkState.Success.Routes::class.java)
            .compose(routeNetworking.buses)
            .distinctUntilChanged()

    private val stops: Flowable<SubmitUiModel.SubmitStop> = routes
            .ofType(NetworkState.Success.Routes::class.java)
            .compose(routeNetworking.stops)
            .distinctUntilChanged { old, new -> old.stops != new.stops }
            .flatMapIterable { it.stops }
            .map { SubmitUiModel.SubmitStop(it) }

    val observeAll: Flowable<SubmitUiModel> = Flowable.merge(routesUi, buses, stops)
            .onBackpressureDrop()
            .share()
}
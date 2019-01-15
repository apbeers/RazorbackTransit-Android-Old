package razorbacktransit.arcu.razorbacktransit.network

import okhttp3.ResponseBody
import razorbacktransit.arcu.razorbacktransit.model.bus.Bus
import razorbacktransit.arcu.razorbacktransit.model.route.Route
import razorbacktransit.arcu.razorbacktransit.model.stop.Stop

sealed class NetworkState
{
    class InTransit(val message: String = ""): NetworkState()
    sealed class Success: NetworkState()
    {
        class Routes(val busRoutes: List<Route>): Success()
        class Busses(val busses: List<Bus>): Success()
        class Stops(val stops: List<Stop>): Success()
        class StopImages(val stopImages: ResponseBody): Success()
        class BusImages(val busImages: Bus): Success()
    }
    class Failure(val t: Throwable): NetworkState()
}
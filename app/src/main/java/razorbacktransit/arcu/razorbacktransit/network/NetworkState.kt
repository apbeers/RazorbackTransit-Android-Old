package razorbacktransit.arcu.razorbacktransit.network

import razorbacktransit.arcu.razorbacktransit.model.bus.Bus
import razorbacktransit.arcu.razorbacktransit.model.route.Route
import razorbacktransit.arcu.razorbacktransit.model.stop.Stop

sealed class NetworkState
{
    class InTransit(val message: String = ""): NetworkState()
    sealed class Success: NetworkState()
    {
        class Routes(var busRoutes: List<Route>): Success()
        class Buses(val buses: List<Bus>): Success()
        class Stops(val stops: List<Stop>): Success()
    }
    class Failure(val t: Throwable): NetworkState()
}
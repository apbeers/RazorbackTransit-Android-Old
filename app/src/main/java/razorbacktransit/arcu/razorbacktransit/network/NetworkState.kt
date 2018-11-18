package razorbacktransit.arcu.razorbacktransit.network

import razorbacktransit.arcu.razorbacktransit.model.bus.Bus
import razorbacktransit.arcu.razorbacktransit.model.busimage.BusImage
import razorbacktransit.arcu.razorbacktransit.model.route.Route
import razorbacktransit.arcu.razorbacktransit.model.stopimage.StopImage

sealed class NetworkState
{
    class InTransit(val message: String = ""): NetworkState()
    sealed class Success: NetworkState()
    {
        class Routes(val busRoutes: List<Route>): Success()
        class Busses(val busses: List<Bus>): Success()
        class StopImages(val stopImages: List<StopImage>): Success()
        class BusImages(val busImages: List<BusImage>): Success()
    }
    class Failure(val t: Throwable): NetworkState()
}
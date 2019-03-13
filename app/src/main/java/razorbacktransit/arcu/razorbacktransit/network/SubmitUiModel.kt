package razorbacktransit.arcu.razorbacktransit.network

import razorbacktransit.arcu.razorbacktransit.model.bus.Bus
import razorbacktransit.arcu.razorbacktransit.model.route.Route
import razorbacktransit.arcu.razorbacktransit.model.stop.Stop

sealed class SubmitUiModel
{
    class SubmitBuses(val bus: List<Bus>): SubmitUiModel()
    class SubmitStops(val stops: List<Stop>): SubmitUiModel()
    class SubmitStop(val stop: Stop): SubmitUiModel()
    class SubmitRoute(val route: List<Route>): SubmitUiModel()
    class InProgress(): SubmitUiModel()
}
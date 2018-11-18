package razorbacktransit.arcu.razorbacktransit.model.route

import com.squareup.moshi.FromJson

class RouteJsonAdapter
{
    @FromJson fun routeFromJson( routeJson: RouteJson ): Route = routeJson.toRoute()
}
package razorbacktransit.arcu.razorbacktransit.network

import io.reactivex.Flowable
import okhttp3.ResponseBody
import razorbacktransit.arcu.razorbacktransit.model.bus.Bus
import razorbacktransit.arcu.razorbacktransit.model.route.Route
import razorbacktransit.arcu.razorbacktransit.model.stop.Stop
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

// Base
// https://campusdata.uark.edu/api/
interface CampusService
{
    @GET("routes")
    fun getRoutes(): Flowable<List<Route>>

    @GET("buses")
    fun getBuses(@Query("routeIds") ids: String ): Flowable<List<Bus>>

    @GET("stops")
    fun getStops(@Query("routeIds") ids: String ): Flowable<List<Stop>>
}
// Buildings
// https://campusdata.uark.edu/api/buildings?callback=Buildings

// Routes
// https://campusdata.uark.edu/api/routes?callback=Routes
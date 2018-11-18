package razorbacktransit.arcu.razorbacktransit.network

import io.reactivex.Flowable
import okhttp3.ResponseBody
import razorbacktransit.arcu.razorbacktransit.model.bus.Bus
import razorbacktransit.arcu.razorbacktransit.model.busimage.BusImage
import razorbacktransit.arcu.razorbacktransit.model.route.Route
import razorbacktransit.arcu.razorbacktransit.model.stopimage.StopImage
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
    fun getAllBuses(): Flowable<List<Bus>>

    @GET("busimages")
    fun getBusImages(@QueryMap colorAndHeading: Map<String, String>): Flowable<List<BusImage>>

    @GET("stops")
    fun getStops(@Query("routeIds") routeId: String): Flowable<ResponseBody>

    @GET("stopimages")
    fun getStopImages(@QueryMap options: Map<String, String>): Flowable<List<StopImage>>
}
// Buildings
// https://campusdata.uark.edu/api/buildings?callback=Buildings

// Routes
// https://campusdata.uark.edu/api/routes?callback=Routes
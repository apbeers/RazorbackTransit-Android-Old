package razorbacktransit.arcu.razorbacktransit.network

import android.util.Log
import io.reactivex.Flowable
import okhttp3.HttpUrl
import razorbacktransit.arcu.razorbacktransit.model.route.Route

fun getBusImageUrl(color: String, heading: String): String = HttpUrl.Builder()
        .scheme("https")
        .host("campusdata.uark.edu")
        .addPathSegment("api")
        .addPathSegment("busimages")
        .addQueryParameter("color", color.replace("#", ""))
        .addQueryParameter("heading", heading)
        .build()
        .uri()
        .toASCIIString()

fun getStopImageUrl(stopId: String, routeIds: String): String = HttpUrl.Builder()
        .scheme("https")
        .host("campusdata.uark.edu")
        .addPathSegment("api")
        .addPathSegment("stopimages")
        .addQueryParameter("stopId", stopId)
        .addQueryParameter("routeIds", routeIds)
        .build()
        .uri()
        .toASCIIString()

fun Flowable<List<Route>>.buildBusIdsString(): Flowable<String>
{
    return this.map { it.map { route -> route.id } }
            .filter { it.isNotEmpty() }
            .map { ids: List<Int> ->
                var idString = ""
                for (id in ids)
                {
                    idString += "$id-"
                }
                if (idString[idString.lastIndex] == '-')
                {
                    return@map idString.substring(0, idString.lastIndex)
                }
                return@map idString
            }
}

fun Flowable<List<Route>>.buildStopIdsString(): Flowable<String>
{
    return this.map { it.map { route -> route.id } }
            .filter { it.isNotEmpty() }
            .map { ids: List<Int> ->
                var idString = ""
                for (id in ids)
                {
                    idString += "$id-"
                }
                if (idString[idString.lastIndex] == '-')
                {
                    return@map idString.substring(0, idString.lastIndex)
                }
                return@map idString
            }
}
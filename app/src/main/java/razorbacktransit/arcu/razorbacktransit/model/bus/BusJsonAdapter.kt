package razorbacktransit.arcu.razorbacktransit.model.bus

import com.squareup.moshi.FromJson

class BusJsonAdapter
{
    @FromJson fun busFromJson(busJson: BusJson): Bus = busJson.toBus()
}
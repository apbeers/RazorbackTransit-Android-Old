package razorbacktransit.arcu.razorbacktransit.model.stop

import com.squareup.moshi.FromJson

class StopJsonAdapter
{
    @FromJson fun stopFromJson(stopJson: StopJson): Stop = stopJson.toStop()
}
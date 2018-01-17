# RazorbackTransit-iOS

<img src="https://github.com/apbeers/RazorbackTransit-iOS/blob/develop/assets/FeatureGraphic.png" width="35%">

## Welcome to the Razorback Transit repository!

This app provides live bus data to over 400 people. It is powered by the official University Campus API and uses Alamofire, SwiftyJSON, Google Maps SDK, and Google Firebase libraries.

This project is open source so that anyone who finds it can learn from the code and make contributions. As a bonus, if you make any contributions you can show future employers you're work here, and GitHub will show the exact lines of code you wrote. You can also do a live demo showing the that you shipped a feature that hundreds of people actually use.

## Contibuting

If you would like to contribute but don't know where to start, here are a few features I've heard people ask for or thought of:

- Filter routes by color (mentioned in an app store review)
- Show users location on the map (mentioned by friends)
- Add on offline screen, so if that app can't get data it will show the user an error instead of a blank map (personal idea)
- iPad support
- Performance improvements or improved error handling
- Bug fixes
- Unit/UI tests
- Types or code style cleanup

Feel free to reach out to me  at [apbeers@gmail.com](mailto:apbeers@gmail.com) about any ideas you have for the app, or questions about getting into app development/technology. The reason I'm open sourcing is to give you a chance to learn and have an opportunity to build something that people will use.

## Releases

I'll do a release whenever a new feature or noticable performance improvement is finished. We will test the app using Apple's TestFlight app for a few days to make sure it behaves correctly (I can add your friends to the beta as well, since the more testers the better). People rely on it every day it's up to us not to cause an outage.

Download for iPhone here:
https://itunes.apple.com/us/app/razorback-transit-live-maps/id1286547241?mt=8

## Running the project

```bash
git clone https://github.com/apbeers/RazorbackTransit-iOS.git
cd RazorbackTransit-iOS
pod install
```

Then open the .xcworkpsace file to start making changes


# AdMediator
Android Ad mediator SDK.
# Getting Started
## Initialization
First you should initialize the SDK before trying to launch the Ad.

    val mediator = AdMediator()
    mediator.initialize(application: Application,
				     appId: String,
				     listener: InitializeListener)
By implementing `InitializeListener` you can run your own code on different callbacks of this method.
|Method|Description  |
|--|--|
| `onSuccess` | All available networks successfully initialized |
| `onError` | An error occurred. See it's description through `message`  |

## Requesting Ad
Next you should request an Ad before showing it. SDK will search different Ad networks and cache the first available Ad.

    mediator.requestAd(context: Context,
		       zoneId: String,
		       listener: AdRequestListener)
By implementing `AdRequestListener` you can run your own code on different callbacks of this method.
|Method|Description  |
|--|--|
| `onAdAvailable` | Ad is ready to show. It's ID is stored in `adId` variable |
| `onError` | An error occurred. See it's description through `message`  |
## Showing ad
Finally you can show the Ad using this method.

    mediator.showAd(activity: Activity,
			      zoneId: String,
			      listener: AdShowListener)
By implementing `AdShowListener` you can run your own code on different callbacks of this method.
|Method|Description  |
|--|--|
| `onOpened` | Ad is opened |
| `onClosed` | Ad is closed |
| `onError` | An error occurred. See it's description through `message`  |
| `onRewarded` | Ad is finished and `completed` boolean indicates the status of the award|

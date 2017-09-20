Grace Period  [![Build Status](https://travis-ci.org/orionhealth/grace-period-android.svg?branch=master)](https://travis-ci.org/orionhealth/grace-period-android)
[![MIT License](http://img.shields.io/badge/license-MIT-blue.svg?style=flat)](https://github.com/bassaer/ChatMessageView/blob/master/LICENSE)
=====================
*A library to kick users out*

When developing an app that deals with sensitive information, such as banking or health related apps, we might want to restrict the user's session time and kick them out of the app when this time expires.
Depending on your architecture, this usually involves a lot of thinking, refactoring and dealing with edge or weird cases. It is easy to end up with unclean code.

With Grace Period library, we aim to simplify this and provide an easy way to integrate this functionality within any app, with reduced hassle.

### How does it work?

Grace Period offers the following functionality:

Once enabled, it will allow the users during the specified period of time to use the app as long as they are interacting with it.
In case the user no longer interacts with the app anymore, whether it is by going to a different app, or by just leaving their phone inactive (even when your app is in foreground), they will be kicked out of the app after the specified time expires.
By default, Grace Period library will restart the app, but this behavior can be changed by adding your own callback to react to  the Grace Period expired event. It can also notify the user about what just happened by setting the proper dialog configuration.

The following diagram illustrates the flow:
![graceperiod](https://user-images.githubusercontent.com/7469647/30615610-892a795e-9de3-11e7-9db2-84f778e8c916.png)

It is worth mentioning that what is described above as *Grace Period Timeout*, is an irreversible state that is handled internally so your app receives the appropriate *Grace Period Expired* callback only once it is in foreground.

### Integration

First, add the dependency to your gradle file:

```compile 'com.orchestral.graceperiod:grace-period:1.0.0'```

Then you'll need to initialize the library with your desired configuration in your Application class:
```
val gracePeriodConfig =
                GracePeriodConfig.Builder()
                        .application(this)
                        .expiryTime(10)
                        .dialogConfig(GracePeriodDialogConfig.Builder().showDialogWhenExpired(true).build())
                        .gracePeriodCallback(yourClassImplementingCallbackInterface)
                        .activityCallbacks(yourCustomActivityLifecycleCallbacks)
                        .build()

        GracePeriod.init(gracePeriodConfig)
```
It is worth noticing that the only mandatory configuration field is Application. We need this to register activity lifecycle callbacks that will help control the app state.

The parameter Grace Period Callback defaults to a built-in `RestartAppCallback`, that will automatically restart the app when the Grace Period time is expired. 
If you would like to change this behaviour, you can do so simply by providing your own callback. Be aware that if you provide a custom callback, you will have to implement the restart mechanism yourself.

You can also provide your dialog configuration: by enabling it, a dialog will be displayed when the expiry time is reached. You can customize the message and even display your custom dialog.
It is disabled by default.

The rest of the parameters are self-explanatory: Expiry time in seconds and your custom implementation of the `ActivityLifecycleCallbacks` (if any).

The next step is to enable and disable Grace Period when needed. This will usually happen after login and after logout, respectively:
```
// User is logged in to our app
GracePeriod.enable()
...
// User is logged out of our app
GracePeriod.disable()
```

It is important that you disable Grace Period once you don't want it to be active anymore, otherwise this will naturally lead to unwanted behavior.

The last step is to provide feedback on the user's interaction. 
This will involve most of the time just overriding `onUserInteraction()` method in each of your activities, but you can notify Grace Period of any other interactions you consider necessary.
```
override fun onUserInteraction() {
        super.onUserInteraction()
        GracePeriod.notifyInteraction()
    }
```

Another option, if you prefer so, is to extend `GracePeriodActivity`, that will implement this code for you.

Note: To call it from Java, use `GracePeriod.INSTANCE.methodName()`

### Dependencies

It is important to note that Grace Period library currently depends on [RxJava 1.x](https://github.com/ReactiveX/RxJava/tree/1.x). There are plans to update to [RxJava 2.x](https://github.com/ReactiveX/RxJava/tree/2.x) and it is also a possibility to remove this dependency altogether. This would require though to re-write some of the core logic of the library.

### Communication Channels

If you have anything to say, you want to contribute in any way or just get in touch, reach us at:

* Daniel Ocampo - [daniel.ocampo@orionhealth.com](mailto:daniel.ocampo@orionhealth.com) - [@d_ofield](https://twitter.com/d_ofield)
* Nader Ayyad - [nader.ayyad@orionhealth.com](mailto:nader.ayyad@orionhealth.com)

### License

[MIT](https://github.com/orionhealth/grace-period-android/blob/master/LICENSE)

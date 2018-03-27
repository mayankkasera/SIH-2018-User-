package dynamicdrillers.sih2018user;

import android.app.Application;

import com.onesignal.OneSignal;

//import com.onesignal.OneSignal;

/**
 * Created by Happy-Singh on 3/26/2018.
 */

public class sih2018user extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }
}

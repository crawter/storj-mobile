package mio.storj.mobile;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.facebook.react.ReactApplication;
import com.reactnative.photoview.PhotoViewPackage;
import com.reactnativecomponent.barcode.RCTCapturePackage;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;

import java.util.Arrays;
import java.util.List;

import mio.storj.mobile.dataprovider.Database;
import mio.storj.mobile.storjlibmodule.interfaces.NotificationResolver;
import mio.storj.mobile.storjlibmodule.StorjLibPackage;
import mio.storj.mobile.storjlibmodule.services.NotificationService;
import mio.storj.mobile.storjlibmodule.services.SyncQueueService;
import mio.storj.mobile.storjlibmodule.interfaces.NotificationResolver;
import mio.storj.mobile.storjlibmodule.services.NotificationService;
import mio.storj.mobile.storjlibmodule.services.SyncQueueService;

public class MainApplication extends Application implements ReactApplication, Application.ActivityLifecycleCallbacks, NotificationResolver {

    private boolean mIsForeground;

    private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
        @Override
        public boolean getUseDeveloperSupport() {
          return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
              return Arrays.<ReactPackage>asList(
                  new MainReactPackage(),
                    new PhotoViewPackage(),
                  new RCTCapturePackage(),
                  new StorjLibPackage()
              );
        }

        @Override
        protected String getJSMainModuleName() {
          return "index";
        }
    };

    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }

    @Override
    public void onCreate() {
        try {
            Database.setInstance(this);
        } catch (InterruptedException e) {
            // TODO: close app
        }

        SyncQueueService.clean(this);
        super.onCreate();
        NotificationService.Init(this);
        SoLoader.init(this, /* native exopackage */ false);
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onTerminate() {
        Database.getInstance().uploadingFiles().deleteAll();
        super.onTerminate();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        mIsForeground = true;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        mIsForeground = false;
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public boolean shouldShowNotification() {
        return !mIsForeground;
    }
}

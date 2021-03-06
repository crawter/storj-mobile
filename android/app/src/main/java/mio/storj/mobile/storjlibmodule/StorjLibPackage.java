package mio.storj.mobile.storjlibmodule;

import android.util.Log;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.storj.libstorj.Storj;
import io.storj.libstorj.android.StorjAndroid;
import mio.storj.mobile.dataprovider.Database;
import mio.storj.mobile.domain.IDatabase;
import mio.storj.mobile.service.SyncService;
import mio.storj.mobile.service.storj.StorjService;
import mio.storj.mobile.storjlibmodule.rnmodules.FilePickerModule;
import mio.storj.mobile.storjlibmodule.rnmodules.StorjModule;
import mio.storj.mobile.storjlibmodule.rnmodules.SyncModule;
import mio.storj.mobile.storjlibmodule.rnmodules.CameraModule;
import mio.storj.mobile.storjlibmodule.rnmodules.OpenFileModule;
import mio.storj.mobile.storjlibmodule.rnmodules.Sha256Module;
import mio.storj.mobile.storjlibmodule.rnmodules.ServiceModule;
import mio.storj.mobile.dataprovider.Database;
import mio.storj.mobile.service.SyncService;
import mio.storj.mobile.storjlibmodule.rnmodules.CameraModule;
import mio.storj.mobile.storjlibmodule.rnmodules.FilePickerModule;
import mio.storj.mobile.storjlibmodule.rnmodules.OpenFileModule;
import mio.storj.mobile.storjlibmodule.rnmodules.ServiceModule;
import mio.storj.mobile.storjlibmodule.rnmodules.Sha256Module;
import mio.storj.mobile.storjlibmodule.rnmodules.StorjModule;
import mio.storj.mobile.storjlibmodule.rnmodules.SyncModule;

public class StorjLibPackage implements ReactPackage {

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        Storj storj = null;
        try {
            storj = StorjAndroid.getInstance(reactContext, "https://api.storj.io");
        } catch (MalformedURLException e) {
            Log.e("StorjModule", "getStorj: ", e);
            // TODO: 06.02.19 Handle NPE corner case
        }

        List<NativeModule> modules = new ArrayList<>();

        modules.add(new StorjModule(reactContext, new StorjService(storj)));
        modules.add(new Sha256Module(reactContext));
        modules.add(new FilePickerModule(reactContext));
        modules.add(new ServiceModule(reactContext));
        modules.add(new SyncModule(reactContext, new SyncService(Database.getInstance())));
        modules.add(new CameraModule(reactContext));
        modules.add(new OpenFileModule(reactContext));

        return modules;
    }

}
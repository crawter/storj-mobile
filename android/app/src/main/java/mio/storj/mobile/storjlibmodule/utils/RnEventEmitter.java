package mio.storj.mobile.storjlibmodule.utils;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import mio.storj.mobile.service.IEventEmitter;

public class RnEventEmitter implements IEventEmitter {
    private final ReactContext mContext;

    public  RnEventEmitter(ReactContext context) {
        mContext = context;
    }

    @Override
    public final void sendEvent(String eventName, String result) {
        if(mContext != null) {
            mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, result);
        }
    }
}

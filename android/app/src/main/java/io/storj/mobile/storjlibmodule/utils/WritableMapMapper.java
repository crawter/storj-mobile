package io.storj.mobile.storjlibmodule.utils;

import android.content.ContentValues;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

import io.storj.mobile.dataprovider.uploading.UploadingFileContract;
import io.storj.mobile.storjlibmodule.services.UploadService;
import io.storj.mobile.storjlibmodule.services.eventemitters.UploadEventEmitter;

public class WritableMapMapper {

    public static WritableMap get(ContentValues values) {
        WritableMap map = new WritableNativeMap();

        if(values == null) {
            return map;
        }

        for (String key : values.keySet()) {
            switch(key) {
                case "fileHandle":
                case UploadingFileContract._PROGRESS:
                case UploadingFileContract._SIZE:
                case UploadingFileContract._UPLOADED:
                    map.putDouble(key, values.getAsDouble(key));
                    break;
                case "fileId":
                case UploadEventEmitter.ERROR_MESSAGE:
                    map.putString(key, values.getAsString(key));
                    break;
                case UploadEventEmitter.ERROR_CODE:
                case UploadService.PARAM_SYNC_ENTRY_ID:
                    map.putInt(key, values.getAsInteger(key));
                    break;
            }
        }

        return map;
    }
}

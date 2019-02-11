package io.storj.mobile.storjlibmodule.rnmodules;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

import io.storj.mobile.storjlibmodule.utils.FileUtils;
/**
 * {@link ReactContextBaseJavaModule}, that creates and opens FilePicker activity.
 *
 * @author Bogdan Artemenko artemenkobogdan@gmail.com
 */
public class FilePickerModule extends ReactContextBaseJavaModule implements ActivityEventListener {
    private static final String MODULE_NAME = "FilePickerAndroid";

    private static final String KEY_PATH = "path";
    private static final String KEY_NAME = "name";
    private static final String KEY_RESULT = "result";
    private static final String KEY_ERROR_MESSAGE = "message";
    private static final String KEY_SUCCESS = "isSuccess";

    private static final String OPTIONS_KEY_MIME_TYPE = "mimeType";
    private static final String OPTIONS_KEY_FILE_PICKER_TITLE = "pickerTitle";

    private static final String[] mPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE_FILE_PICKER = 346374337;

    private final ReactApplicationContext mReactContext;
    private Promise mPromise;

    /**
     * public constructor required by {@link ReactContextBaseJavaModule}
     *
     * @param reactContext
     */
    public FilePickerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        reactContext.addActivityEventListener(this);
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    /**
     * Public {@link ReactMethod} that opens Android built-it file browser to pick single file.
     *
     * @param params  in parameters such as MIME_TYPE and PICKER_TITLE.
     * @param promise
     */
    @ReactMethod
    public void show(final ReadableMap params, Promise promise) {
        mPromise = promise;
        WritableMap responseMap = new WritableNativeMap();
        Activity currentActivity = getCurrentActivity();

        if (currentActivity == null) {
            responseMap.putString(KEY_ERROR_MESSAGE, "Cannot retrieve activity");
            mPromise.resolve(responseMap);
            return;
        }

        if (!checkPermissionsGranted(currentActivity)) {
            ActivityCompat.requestPermissions(currentActivity, mPermissions, Integer.MAX_VALUE);
            mPromise.reject("permissionNotGranted", "Permissions not granted");
            return;
        }

        String mimeType = params.hasKey(OPTIONS_KEY_MIME_TYPE) ?
                params.getString(OPTIONS_KEY_MIME_TYPE) : "*/*";

        String pickerTitle = params.hasKey(OPTIONS_KEY_FILE_PICKER_TITLE) ?
                params.getString(OPTIONS_KEY_FILE_PICKER_TITLE) : "Select file";

        Intent pickerIntent = FileUtils.createGetContentIntent(mimeType);

        if (pickerIntent.resolveActivity(mReactContext.getPackageManager()) == null) {
            responseMap.putString(KEY_ERROR_MESSAGE, "Unable to launch file picker");
            mPromise.resolve(responseMap);
            return;
        }

        try {
            currentActivity.startActivityForResult(Intent.createChooser(pickerIntent, pickerTitle),
                    REQUEST_CODE_FILE_PICKER);
        } catch (ActivityNotFoundException exception) {
            mPromise.reject(exception);
        }
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (REQUEST_CODE_FILE_PICKER != requestCode) {
            return;
        }

        WritableMap responseMap = new WritableNativeMap();
        WritableArray resultMap = new WritableNativeArray();

        if (Activity.RESULT_OK != resultCode) {
            responseMap.putString(KEY_ERROR_MESSAGE, "Canceled by user");
            responseMap.putBoolean(KEY_SUCCESS, false);
            mPromise.resolve(responseMap);
            return;
        }

        ClipData clipData = data.getClipData();

        if(clipData != null) {
            _handleClipData(clipData, responseMap, resultMap);
        } else {
            _handleSingleFile(data, resultMap);
        }

        responseMap.putNull(KEY_ERROR_MESSAGE);
        responseMap.putBoolean(KEY_SUCCESS, true);
        responseMap.putArray(KEY_RESULT, resultMap);
        mPromise.resolve(responseMap);
    }

    /**
     * Required for RN 0.30+ modules which implements ActivityEventListener
     */
    @Override
    public void onNewIntent(Intent intent) {
    }

    /**
     * @return true if has at least one permission granted, false otherwise
     */
    private boolean checkPermissionsGranted(Activity activity) {
        for (String permissionToCheck : mPermissions) {
            if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(activity,
                    permissionToCheck)) {
                return true;
            }
        }

        return false;
    }

    private void _handleClipData(ClipData clipData, WritableMap responseMap, WritableArray resultMap) {
        if (clipData.getItemCount() == 0) {
            responseMap.putString(KEY_ERROR_MESSAGE, "no files selected");
            responseMap.putBoolean(KEY_SUCCESS, false);
            mPromise.resolve(responseMap);
            return;
        }

        for(int i = 0; i < clipData.getItemCount(); i++) {
            WritableMap uriMap = new WritableNativeMap();
            ClipData.Item item = clipData.getItemAt(i);
            Uri uri = item.getUri();

            if(_checkNullUri(uri, uriMap, resultMap)) continue;

            if(_checkIsLocal(uri, uriMap, resultMap)) continue;

            String path = FileUtils.getPath(mReactContext, uri);

            if (_checkPath(path, uriMap, resultMap)) continue;

            String name = "";
            int cut = path.lastIndexOf('/');
            if (cut != -1) {
                name = path.substring(cut + 1);
            }

            uriMap.putString(KEY_NAME, name);
            uriMap.putString(KEY_PATH, path);
            resultMap.pushMap(uriMap);
        }
    }

    private void _handleSingleFile(Intent data, WritableArray resultMap) {
        WritableMap uriMap = new WritableNativeMap();
        Uri uri = data.getData();

        if(_checkNullUri(uri, uriMap, resultMap)) return;

        if(_checkIsLocal(uri, uriMap, resultMap)) return;

        String path = FileUtils.getPath(mReactContext, uri);

        if (_checkPath(path, uriMap, resultMap)) return;

        String name = "";
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            name = path.substring(cut + 1);
        }

        uriMap.putString(KEY_NAME, name);
        uriMap.putString(KEY_PATH, path);
        resultMap.pushMap(uriMap);
    }

    private boolean _checkNullUri(Uri uri, WritableMap uriMap, WritableArray resultMap) {
        boolean isNull = uri == null;

        if (isNull) {
            uriMap.putNull(KEY_PATH);
            uriMap.putString(KEY_ERROR_MESSAGE, "Cannot retrieve chosen file URI");
            resultMap.pushMap(uriMap);
        }

        return isNull;
    }

    private boolean _checkIsLocal(Uri uri, WritableMap uriMap, WritableArray resultMap) {
        boolean isNotLocal = !FileUtils.isLocal(uri.toString());

        if (isNotLocal) {
            uriMap.putString(KEY_ERROR_MESSAGE, "Selected file is not located locally");
            uriMap.putNull(KEY_PATH);
            resultMap.pushMap(uriMap);
        }

        return isNotLocal;
    }

    private boolean _checkPath(String path, WritableMap uriMap, WritableArray resultMap) {
        boolean isNull = path == null;

        if (isNull) {
            uriMap.putString(KEY_ERROR_MESSAGE, "Cannot retrieve chosen file path");
            uriMap.putNull(KEY_PATH);
            resultMap.pushMap(uriMap);
        }

        return isNull;
    }
}
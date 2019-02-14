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

import java.util.ArrayList;
import java.util.List;

import io.storj.mobile.common.responses.ListResponse;
import io.storj.mobile.common.responses.Response;
import io.storj.mobile.storjlibmodule.models.FilePathModel;
import io.storj.mobile.storjlibmodule.utils.FileUtils;

public class FilePickerModule extends ReactContextBaseJavaModule implements ActivityEventListener {
    private static final String MODULE_NAME = "FilePickerAndroid";

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
        Activity currentActivity = getCurrentActivity();

        if (currentActivity == null) {
            mPromise.resolve(new Response(false, "Cannot retrieve activity").toJson());
            return;
        }

        if (!checkPermissionsGranted(currentActivity)) {
            ActivityCompat.requestPermissions(currentActivity, mPermissions, Integer.MAX_VALUE);
            mPromise.resolve(new Response(false, "Permissions not granted").toJson());
            return;
        }

        String mimeType = params.hasKey(OPTIONS_KEY_MIME_TYPE) ?
                params.getString(OPTIONS_KEY_MIME_TYPE) : "*/*";

        String pickerTitle = params.hasKey(OPTIONS_KEY_FILE_PICKER_TITLE) ?
                params.getString(OPTIONS_KEY_FILE_PICKER_TITLE) : "Select file";

        Intent pickerIntent = FileUtils.createGetContentIntent(mimeType);

        if (pickerIntent.resolveActivity(mReactContext.getPackageManager()) == null) {
            mPromise.resolve(new Response(false, "Unable to launch file picker").toJson());
            return;
        }

        try {
            currentActivity.startActivityForResult(Intent.createChooser(pickerIntent, pickerTitle),
                    REQUEST_CODE_FILE_PICKER);
        } catch (ActivityNotFoundException ex) {
            mPromise.resolve(new Response(false, ex.getMessage()).toJson());
        }
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (REQUEST_CODE_FILE_PICKER != requestCode) {
            return;
        }

        ListResponse<FilePathModel> result = null;

        if (Activity.RESULT_OK != resultCode) {
            mPromise.resolve(new Response(false, "Canceled by user").toJson());
            return;
        }

        ClipData clipData = data.getClipData();

        try {
            if(clipData != null) {
                result = _handleClipData(clipData);
            } else {
                result = _handleSingleFile(data);
            }
        } catch(Exception ex) {
            mPromise.resolve(new Response(false, ex.getMessage()).toJson());
            return;
        }

        mPromise.resolve(result.toJson());
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

    private ListResponse<FilePathModel> _handleClipData(ClipData clipData) {
        if (clipData.getItemCount() == 0) {
            return new ListResponse<>(null, false,"no files selected");
        }

        List<FilePathModel> res = new ArrayList<FilePathModel>();

        for(int i = 0; i < clipData.getItemCount(); i++) {
            ClipData.Item item = clipData.getItemAt(i);
            Uri uri = item.getUri();

            if(uri == null) continue;

            if(!FileUtils.isLocal(uri.toString())) continue;

            String path = FileUtils.getPath(mReactContext, uri);

            if (path == null) continue;

            String name = "";
            int cut = path.lastIndexOf('/');
            if (cut != -1) {
                name = path.substring(cut + 1);
            }

            res.add(new FilePathModel(name, path));
        }

        return new ListResponse<>(res, true, null);
    }

    private ListResponse<FilePathModel> _handleSingleFile(Intent data) {
        Uri uri = data.getData();

        if(uri == null) {
            return new ListResponse<>(
                    null,
                    false,
                    "Cannot retrieve chosen file URI");
        }

        if(!FileUtils.isLocal(uri.toString())) {
            return new ListResponse<>(
                    null,
                    false,
                    "Selected file is not located locally");
        }

        String path = FileUtils.getPath(mReactContext, uri);

        if (path == null) {
            return new ListResponse<>(
                    null,
                    false,
                    "Cannot retrieve chosen file path");
        }

        String name = "";
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            name = path.substring(cut + 1);
        }

        List<FilePathModel> res = new ArrayList<FilePathModel>();
        res.add(new FilePathModel(name, path));
        return new ListResponse<>(res, true, null);
    }
}
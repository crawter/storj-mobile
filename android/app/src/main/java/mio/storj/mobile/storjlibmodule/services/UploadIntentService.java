package mio.storj.mobile.storjlibmodule.services;

import android.content.Intent;
import android.support.annotation.Nullable;

import io.storj.libstorj.Storj;
import io.storj.libstorj.android.StorjAndroid;
import mio.storj.mobile.dataprovider.Database;
import mio.storj.mobile.service.storj.StorjService;
import mio.storj.mobile.service.upload.UploadingService;
import mio.storj.mobile.storjlibmodule.rnmodules.BaseReactService;

public class UploadIntentService extends BaseReactService {
    private final static String SERVICE_NAME_SHORT = "UploadIntentService";
    private final static String ACTION_UPLOAD_FILE = "ACTION_UPLOAD_FILE";

    private final static String PARAM_BUCKET_ID = "bucketId";
    private final static String PARAM_FILE_NAME = "fileName";
    private final static String PARAM_LOCAL_PATH = "localPath";

    private UploadingService mUploadService;
    private Storj storj;

    public UploadIntentService() {
        super(SERVICE_NAME_SHORT);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        try {
            storj = StorjAndroid.getInstance(this, "https://api.storj.io");
        } catch(Exception e) {
            this.stopSelf();
            return;
        }

        mUploadService = new UploadingService(
                Database.getInstance(),
                new StorjService(storj),
                this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent == null) {
            return;
        }

        String fileName = intent.getStringExtra(PARAM_FILE_NAME);
        String localPath = intent.getStringExtra(PARAM_LOCAL_PATH);
        String bucketId = intent.getStringExtra(PARAM_BUCKET_ID);

        try {
            switch(intent.getAction()) {
                case ACTION_UPLOAD_FILE:
                    mUploadService.upload(bucketId, fileName, localPath);
                    break;
            }
        } catch (InterruptedException ex) {
            // TODO: handle exceptions
        } catch (Exception ex) {
            // TODO: handle exceptions
        }
    }
}

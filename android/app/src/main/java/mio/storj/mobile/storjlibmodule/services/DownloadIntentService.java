package mio.storj.mobile.storjlibmodule.services;

import android.content.Intent;
import android.support.annotation.Nullable;

import io.storj.libstorj.Storj;
import io.storj.libstorj.android.StorjAndroid;
import mio.storj.mobile.dataprovider.Database;
import mio.storj.mobile.domain.IDatabase;
import mio.storj.mobile.service.download.DownloadService;
import mio.storj.mobile.service.storj.StorjService;
import mio.storj.mobile.storjlibmodule.rnmodules.BaseReactService;
import mio.storj.mobile.storjlibmodule.rnmodules.BaseReactService;

public class DownloadIntentService extends BaseReactService {
    private final static String SERVICE_NAME_SHORT = "DownloadIntentService";
    public final static String SERVICE_NAME = "mio.storj.mobile.storjlibmodule.services.DownloadIntentService";

    private final static String ACTION_DOWNLOAD_FILE = "ACTION_DOWNLOAD_FILE";
    private final static String ACTION_COPY_FILE = "ACTION_COPY_FILE";

    private final static String PARAMS_BUCKET_ID = "bucketId";
    private final static String PARAMS_TARGET_BUCKET_ID = "targetBucketId";
    private final static String PARAMS_FILE_ID = "fileId";
    private final static String PARAMS_LOCAL_PATH = "localPath";

    private IDatabase mStore;
    private Storj storj;
    private DownloadService mDownloadService;

    public DownloadIntentService() {
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

        mStore = Database.getInstance();

        mDownloadService = new DownloadService(mStore, new StorjService(storj),this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent == null) {
            return;
        }

        String action = intent.getAction();
        String bucketId = intent.getStringExtra(PARAMS_BUCKET_ID);
        String fileId = intent.getStringExtra(PARAMS_FILE_ID);
        String localPath = intent.getStringExtra(PARAMS_LOCAL_PATH);

        try {
            switch (action) {
                case ACTION_DOWNLOAD_FILE:
                    mDownloadService.download(bucketId, fileId, localPath);
                    break;
                case ACTION_COPY_FILE:
                    String targetBucketId = intent.getStringExtra(PARAMS_TARGET_BUCKET_ID);

                    if(mDownloadService.download(bucketId, fileId, localPath)) {
                        uploadFile(targetBucketId, localPath);
                    }

                    break;
            }
        } catch (InterruptedException ex) {
            // TODO: handle exceptions
        } catch (Exception ex) {
            // TODO: handle exceptions
        }
    }

    private void uploadFile(String bucketId, String localPath) {
        Intent uploadIntent = new Intent(this, UploadService.class);

        uploadIntent.setAction(UploadService.ACTION_UPLOAD_FILE);
        uploadIntent.putExtra(UploadService.PARAM_BUCKET_ID, bucketId);
        uploadIntent.putExtra(UploadService.PARAM_LOCAL_PATH, localPath);

        this.startService(uploadIntent);
    }
}

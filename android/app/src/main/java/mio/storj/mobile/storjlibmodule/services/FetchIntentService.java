package mio.storj.mobile.storjlibmodule.services;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import io.storj.libstorj.KeysNotFoundException;
import io.storj.libstorj.Storj;
import io.storj.libstorj.android.StorjAndroid;
import mio.storj.mobile.common.responses.Response;
import mio.storj.mobile.common.responses.SingleResponse;
import mio.storj.mobile.dataprovider.Database;
import mio.storj.mobile.service.FetchService;
import mio.storj.mobile.service.storj.StorjService;
import mio.storj.mobile.storjlibmodule.models.FileDeleteModel;
import mio.storj.mobile.storjlibmodule.rnmodules.BaseReactService;
import mio.storj.mobile.storjlibmodule.models.FileDeleteModel;
import mio.storj.mobile.storjlibmodule.rnmodules.BaseReactService;
import mio.storj.mobile.storjlibmodule.rnmodules.ServiceModule;

import static mio.storj.mobile.storjlibmodule.rnmodules.ServiceModule.BUCKET_CREATED;
import static mio.storj.mobile.storjlibmodule.rnmodules.ServiceModule.BUCKET_DELETED;
import static mio.storj.mobile.storjlibmodule.rnmodules.ServiceModule.FILE_DELETED;
import static mio.storj.mobile.storjlibmodule.rnmodules.ServiceModule.GET_BUCKETS;
import static mio.storj.mobile.storjlibmodule.rnmodules.ServiceModule.GET_FILES;

public class FetchIntentService extends BaseReactService {
    public final static String SERVICE_NAME = "mio.storj.mobile.storjlibmodule.services.FetchIntentService";

    private final static String EVENT_BUCKETS_UPDATED = "EVENT_BUCKETS_UPDATED";
    private final static String EVENT_FILES_UPDATED = "EVENT_FILES_UPDATED";
    private final static String EVENT_BUCKET_CREATED = "EVENT_BUCKET_CREATED";
    private final static String EVENT_BUCKET_DELETED = "EVENT_BUCKET_DELETED";
    private final static String EVENT_FILE_DELETED = "EVENT_FILE_DELETED";

    private static final String TAG = "FetchIntentService";

    private FetchService mService;

    public FetchIntentService() {
        super("FetchIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Storj storj;
        try {
            storj = StorjAndroid.getInstance(this, "https://api.storj.io");
        } catch(Exception e) {
            this.stopSelf();
            return;
        }

        StorjService service = new StorjService(storj);
        mService = new FetchService(service, Database.getInstance());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent == null) {
            Log.d(TAG, "onHandleIntent: Intent is null");
        }

        String action = intent.getAction();
        Log.d(TAG, "onHandleIntent: " + action);

        // TODO: move exception handling here
        // as we don't care which method throw
        switch(action) {
            case ServiceModule.GET_BUCKETS:
                getBuckets();
                break;
            case ServiceModule.GET_FILES:
                getFiles(intent.getStringExtra("bucketId"));
                break;
            case ServiceModule.BUCKET_CREATED:
                createBucket(intent.getStringExtra("bucketName"));
                break;
            case ServiceModule.BUCKET_DELETED:
                deleteBucket(intent.getStringExtra("bucketId"));
                break;
            case ServiceModule.FILE_DELETED:
                deleteFile(intent.getStringExtra("bucketId"), intent.getStringExtra("fileId"));
                break;
        }

        Log.d(TAG, "onHandleIntentEND: " + action);
    }

    private void getBuckets() {
        try {
            sendEvent(EVENT_BUCKETS_UPDATED, mService.getBuckets().isSuccess());
        } catch (KeysNotFoundException ex) {
            // TODO: finish handling keys not found exception, send
            // a toast that will ask user to sign out and sign in again
            sendEvent(EVENT_BUCKETS_UPDATED, false);
        } catch (InterruptedException ex) {
            //TODO: handle InterruptedException, no need to send event
        }
    }

    private void getFiles(final String bucketId) {
        try {
            Response getFileResponse = mService.getFiles(bucketId);
            SingleResponse<String> result = new SingleResponse<String>(
                    bucketId,
                    getFileResponse.isSuccess(),
                    getFileResponse.getError().getMessage()
            );

            sendEvent(EVENT_FILES_UPDATED, result.toJson());
        } catch (KeysNotFoundException ex) {
            // TODO: finish handling keys not found exception, send
            // a toast that will ask user to sign out and sign in again
            sendEvent(EVENT_FILES_UPDATED, new SingleResponse<String>(null, false, "keys not found").toJson());
        } catch (InterruptedException ex) {
            //TODO: handle InterruptedException, no need to send event
        }
    }

    private void createBucket(final String bucketName) {
        try {
            sendEvent(EVENT_BUCKET_CREATED, mService.createBucket(bucketName).toJson());
        } catch (KeysNotFoundException ex) {
            // TODO: finish handling keys not found exception, send
            // a toast that will ask user to sign out and sign in again
            sendEvent(EVENT_BUCKET_CREATED, new Response(false, "keys not found").toJson());
        } catch (InterruptedException ex) {
            //TODO: handle InterruptedException, no need to send event
        }
    }

    private void deleteBucket(final String bucketId) {
        try {
            Response deleteBucketResponse = mService.deleteBucket(bucketId);
            if (!deleteBucketResponse.isSuccess()) {
                sendEvent(EVENT_BUCKET_DELETED, deleteBucketResponse.toJson());
            }

            sendEvent(EVENT_BUCKET_DELETED, new SingleResponse<>(bucketId, true, null).toJson());
        } catch (KeysNotFoundException ex) {
            // TODO: finish handling keys not found exception, send
            // a toast that will ask user to sign out and sign in again
            sendEvent(EVENT_BUCKET_DELETED, new Response(false, "keys not found").toJson());
        } catch (InterruptedException ex) {
            //TODO: handle InterruptedException, no need to send event
        }
    }

    private void deleteFile(final String bucketId, final String fileId) {
        try {
            Response fileDeleteResponse = mService.deleteFile(bucketId, fileId);
            if (!fileDeleteResponse.isSuccess()) {
                sendEvent(EVENT_FILE_DELETED, fileDeleteResponse.toJson());
            }

            sendEvent(EVENT_FILE_DELETED, new SingleResponse<>(
                    new FileDeleteModel(bucketId, fileId),
                    true,
                    null
            ).toJson());
        } catch (KeysNotFoundException ex) {
            // TODO: finish handling keys not found exception, send
            // a toast that will ask user to sign out and sign in again
            sendEvent(EVENT_BUCKET_DELETED, new Response(false, "keys not found").toJson());
        } catch (InterruptedException ex) {
            //TODO: handle InterruptedException, no need to send event
        }
    }
}

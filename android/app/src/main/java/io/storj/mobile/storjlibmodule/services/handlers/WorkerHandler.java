package io.storj.mobile.storjlibmodule.services.handlers;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import io.storj.mobile.dataprovider.Database;
import io.storj.mobile.storjlibmodule.services.callbacks.WorkerUploaderCallback;
import io.storj.mobile.storjlibmodule.services.eventemitters.UploadEventEmitter;
import io.storj.mobile.storjlibmodule.utils.Uploader;

public class WorkerHandler extends Handler {

    private Context mContext;

    public final static String PARAM_BUCKET_ID = "bucketId";
    public final static String PARAM_FILE_NAME = "fileName";
    public final static String PARAM_LOCAL_PATH = "localPath";

    public WorkerHandler(Looper looper, Context context) {
        super(looper);
        mContext = context;
    }

    @Override
    public void handleMessage(Message msg) {
        Bundle data = msg.getData();
        String fileName = data.getString(PARAM_FILE_NAME);
        String localPath = data.getString(PARAM_LOCAL_PATH);
        String bucketId = data.getString(PARAM_BUCKET_ID);

        if(fileName == null) {
            int cut = localPath.lastIndexOf('/');
            if (cut != -1) {
                fileName = localPath.substring(cut + 1);
            }
        }

        Uploader uploader = new Uploader(mContext, new WorkerUploaderCallback(Database.getInstance(), new UploadEventEmitter(mContext), false));
        try {
            uploader.uploadFile(bucketId, fileName, localPath);
        } catch (Exception e) {

        }
    }
}

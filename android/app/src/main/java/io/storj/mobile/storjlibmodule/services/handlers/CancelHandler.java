package io.storj.mobile.storjlibmodule.services.handlers;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import io.storj.mobile.common.responses.SingleResponse;
import io.storj.mobile.dataprovider.Database;
import io.storj.mobile.domain.uploading.UploadingFile;
import io.storj.mobile.storjlibmodule.utils.Uploader;

public class CancelHandler extends Handler {

    private Context mContext;

    public final static String PARAM_FILE_HANDLE = "fileHandle";

    public CancelHandler(Looper looper, Context context) {
        super(looper);
        mContext = context;
    }

    @Override
    public void handleMessage(Message msg) {
        Bundle data = msg.getData();
        long fileHandle = data.getLong(PARAM_FILE_HANDLE);

        SingleResponse<UploadingFile> model =
                Database.getInstance().uploadingFiles().get(String.valueOf(fileHandle));

        if(model.isSuccess()) {
            return;
        }

        Uploader uploader = new Uploader(mContext, null);
        uploader.cancelFileUpload(fileHandle);
    }
}

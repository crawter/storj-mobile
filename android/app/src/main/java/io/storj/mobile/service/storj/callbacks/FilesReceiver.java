package io.storj.mobile.service.storj.callbacks;

import java.util.ArrayList;
import java.util.List;


import io.storj.libstorj.ListFilesCallback;
import io.storj.mobile.common.Converters;
import io.storj.mobile.common.responses.ListResponse;
import io.storj.mobile.domain.files.File;

public class FilesReceiver implements ListFilesCallback {
    private ListResponse<File> mResult;

    @Override
    public void onFilesReceived(String bucketId, io.storj.libstorj.File[] files) {
        List<File> filesList = new ArrayList<>();
        int length = files.length;

        for (int i = 0; i < length; i++) {
            filesList.add(Converters.toDomain(files[i]));
        }

        mResult = new ListResponse<>(filesList, true, null);
    }

    @Override
    public void onError(String bucketId, int code, String message) {
        mResult = new ListResponse<>(null, false, message, code);
    }

    public ListResponse<File> getResult() {
        return mResult;
    }
}

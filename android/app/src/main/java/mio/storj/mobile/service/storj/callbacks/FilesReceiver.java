package mio.storj.mobile.service.storj.callbacks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import io.storj.libstorj.ListFilesCallback;
import mio.storj.mobile.service.storj.Converters;
import mio.storj.mobile.common.responses.ListResponse;
import mio.storj.mobile.domain.files.File;
import mio.storj.mobile.common.responses.ListResponse;
import mio.storj.mobile.domain.files.File;
import mio.storj.mobile.service.storj.Converters;

public class FilesReceiver implements ListFilesCallback {
    private ListResponse<File> mResult;
    private CountDownLatch mCounter;

    public FilesReceiver(CountDownLatch counter) {
        mCounter = counter;
    }

    @Override
    public void onFilesReceived(String bucketId, io.storj.libstorj.File[] files) {
        List<File> filesList = new ArrayList<>();
        int length = files.length;

        for (int i = 0; i < length; i++) {
            filesList.add(Converters.toDomain(files[i]));
        }

        mResult = new ListResponse<>(filesList, true, null);
        mCounter.countDown();
    }

    @Override
    public void onError(String bucketId, int code, String message) {
        mResult = new ListResponse<>(null, false, message, code);
        mCounter.countDown();
    }

    public ListResponse<File> getResult() {
        return mResult;
    }
}

package io.storj.mobile.service.storj;

import java.util.concurrent.CountDownLatch;

import io.storj.libstorj.DownloadFileCallback;
import io.storj.libstorj.Keys;
import io.storj.libstorj.KeysNotFoundException;
import io.storj.libstorj.Storj;
import io.storj.mobile.common.responses.ListResponse;
import io.storj.mobile.common.responses.Response;
import io.storj.mobile.common.responses.SingleResponse;
import io.storj.mobile.domain.buckets.Bucket;
import io.storj.mobile.domain.files.File;
import io.storj.mobile.service.storj.callbacks.BucketCreator;
import io.storj.mobile.service.storj.callbacks.BucketDeleter;
import io.storj.mobile.service.storj.callbacks.BucketsReceiver;
import io.storj.mobile.service.storj.callbacks.FileDeleter;
import io.storj.mobile.service.storj.callbacks.FilesReceiver;
import io.storj.mobile.service.storj.callbacks.Registrator;

public class StorjService {
    private final Storj mInstance;
    private final int MNEMONIC_STRENGTH = 256;

    public StorjService(Storj storj) {
        mInstance = storj;
    }

    //--- AUTH ---//

    public String generateMnemonic() {
        return Storj.generateMnemonic(this.MNEMONIC_STRENGTH);
    }

    public boolean checkMnemonic(final String mnemonic) {
        return Storj.checkMnemonic(mnemonic);
    }

    public int verifyKeys(final String email, final String password) throws InterruptedException {
            return mInstance.verifyKeys(email, password);
    }

    public boolean keysExist() {
        return mInstance.keysExist();
    }

    public boolean importKeys(final String email, final String password, final String mnemonic, final String passcode) {
        return mInstance.importKeys(new Keys(email, password, mnemonic), passcode);
    }

    public boolean deleteKeys() {
        return mInstance.deleteKeys();
    }

    public SingleResponse<KeyModel> getKeys(final String passcode) {
        Keys keys = mInstance.getKeys(passcode);
        if(keys == null) {
            return new SingleResponse<>(null, false, "keys not found");
        }

        return new SingleResponse<>(new KeyModel(keys), true, null);
    }

    public boolean register(final String login, final String password) {
        Registrator reg = new Registrator();

        mInstance.register(login, password, reg);

        return reg.getResult();
    }


    //--- BUCKETS ---//

    public ListResponse<Bucket> getBuckets() throws InterruptedException, KeysNotFoundException {
        CountDownLatch latch = new CountDownLatch(1);
        BucketsReceiver bReceiver = new BucketsReceiver(latch);

        mInstance.getBuckets(bReceiver);

        latch.await();
        return bReceiver.getResult();
    }

    public SingleResponse<Bucket> createBucket(final String bucketName) throws InterruptedException, KeysNotFoundException {
        CountDownLatch latch = new CountDownLatch(1);
        BucketCreator bCreator = new BucketCreator(latch);

        mInstance.createBucket(bucketName, bCreator);

        latch.await();
        return bCreator.getResult();
    }

    public Response deleteBucket(final String bucketId) throws InterruptedException, KeysNotFoundException {
        CountDownLatch latch = new CountDownLatch(1);
        BucketDeleter bDeleter = new BucketDeleter(latch);

        mInstance.deleteBucket(bucketId, bDeleter);

        latch.await();
        return bDeleter.getResult();
    }

    //--- FILES ---//

    public ListResponse<File> getFiles(final String bucketId) throws InterruptedException, KeysNotFoundException {
        CountDownLatch latch = new CountDownLatch(1);
        FilesReceiver fReceiver = new FilesReceiver(latch);

        mInstance.listFiles(bucketId, fReceiver);

        latch.await();
        return fReceiver.getResult();
    }

    public Response deleteFile(final String bucketId, final String fileId) throws InterruptedException, KeysNotFoundException {
        CountDownLatch latch = new CountDownLatch(1);
        FileDeleter fDeleter = new FileDeleter(latch);

        mInstance.deleteFile(bucketId, fileId, fDeleter);

        latch.await();
        return fDeleter.getResult();
    }

    public long downloadFile(String bucketId, String fileId, String localPath, final DownloadFileCallback callback) throws Exception {
        return mInstance.downloadFile(bucketId, fileId, localPath, new DownloadFileCallback() {
            @Override
            public void onProgress(String fileId, double progress, long downloadedBytes, long totalBytes) {
                callback.onProgress(fileId, progress, downloadedBytes, totalBytes);
            }

            @Override
            public void onComplete(String fileId, String localPath) {
                callback.onComplete(fileId, localPath);
            }

            @Override
            public void onError(String fileId, int code, String message) {
                callback.onError(fileId, code, message);
            }
        });
    }

    // TODO: check behavior with zero value
    public boolean cancelDownload(final long fileRef) {
        return mInstance.cancelDownload(fileRef);
    }

    // TODO: check behavior with zero value
    public boolean cancelUpload(final long fileRef) {
        return mInstance.cancelUpload(fileRef);
    }
}

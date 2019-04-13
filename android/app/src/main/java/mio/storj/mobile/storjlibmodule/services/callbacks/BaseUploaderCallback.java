package mio.storj.mobile.storjlibmodule.services.callbacks;

import io.storj.libstorj.File;
import mio.storj.mobile.domain.IDatabase;
import mio.storj.mobile.domain.uploading.UploadingFile;
import mio.storj.mobile.storjlibmodule.utils.ProgressResolver;
import mio.storj.mobile.storjlibmodule.utils.Uploader;
import mio.storj.mobile.storjlibmodule.utils.ProgressResolver;
import mio.storj.mobile.storjlibmodule.utils.Uploader;

public class BaseUploaderCallback implements Uploader.Callback {
    protected final IDatabase mStore;
    private final ProgressResolver mProgressResolver;
    private UploadingFile mUploadingFile;
    private final Object lock = new Object();

    protected long mFileHandle;

    public BaseUploaderCallback(IDatabase db) {
        mStore = db;
        mProgressResolver = new ProgressResolver();
    }

    @Override
    public void onStart(long fileHandle, String bucketId, String fileName, String localPath) {
        synchronized (lock) {
            mUploadingFile = new UploadingFile(fileHandle, 0, 0, 0, fileName, localPath, bucketId);
        }

        mFileHandle = fileHandle;
        mStore.uploadingFiles().insert(mUploadingFile);
    }

    @Override
    public boolean onProgress(String localPath, double progress, long uploadedBytes, long totalBytes) {
        synchronized (lock) {
            if(mUploadingFile == null || !mUploadingFile.isIdSet()) {
                return false;
            }
        }

        synchronized (mProgressResolver) {
            mProgressResolver.setMProgress(progress);

            if(mProgressResolver.getMProgress() != progress) {
                return false;
            }

            mUploadingFile.setProgress(mProgressResolver.getMProgress());
            mUploadingFile.setUploaded(uploadedBytes);
            mUploadingFile.setSize(totalBytes);
        }

        mStore.uploadingFiles().update(mUploadingFile);
        return true;
    }

    @Override
    public void onComplete(String localPath, File file) {
        mStore.uploadingFiles().delete(mUploadingFile.getFileHandle());
    }

    @Override
    public void onError(String localPath, int code, String message) {
        mStore.uploadingFiles().delete(mUploadingFile.getFileHandle());
    }
}

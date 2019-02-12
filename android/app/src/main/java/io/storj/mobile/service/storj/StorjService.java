package io.storj.mobile.service.storj;

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

    public int verifyKeys(final String email, final String password) {
        try {
            return mInstance.verifyKeys(email, password);
            //return error == Storj.NO_ERROR;
        } catch (InterruptedException ex) {
            return 1;
        }
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

    public ListResponse<Bucket> getBuckets() {
        BucketsReceiver bReceiver = new BucketsReceiver();

        try {
            mInstance.getBuckets(bReceiver);
        } catch (KeysNotFoundException ex) {
            return new ListResponse<>(null, false, ex.getMessage());
        }

        while(bReceiver.getResult() == null) {

        }

        return bReceiver.getResult();
    }

    public SingleResponse<Bucket> createBucket(final String bucketName) {
        BucketCreator bCreator = new BucketCreator();

        try {
            mInstance.createBucket(bucketName, bCreator);
        } catch (KeysNotFoundException ex) {
            return new SingleResponse<>(null, false, ex.getMessage());
        }

        while(bCreator.getResult() == null) {

        }

        return bCreator.getResult();
    }

    public Response deleteBucket(final String bucketId) {
        BucketDeleter bDeleter = new BucketDeleter();

        try {
            mInstance.deleteBucket(bucketId, bDeleter);
        } catch (KeysNotFoundException ex) {
            return new Response(false, ex.getMessage());
        }

        while(bDeleter.getResult() == null) {

        }

        return bDeleter.getResult();
    }

    //--- FILES ---//

    public ListResponse<File> getFiles(final String bucketId) {
        FilesReceiver fReceiver = new FilesReceiver();

        try {
            mInstance.listFiles(bucketId, fReceiver);
        } catch (KeysNotFoundException ex) {
            return new ListResponse<>(null,false, ex.getMessage());
        }

        while(fReceiver.getResult() == null) {

        }

        return fReceiver.getResult();
    }

    public Response deleteFile(final String bucketId, final String fileId) {
        FileDeleter fdeleter = new FileDeleter();

        try {
            mInstance.deleteFile(bucketId, fileId, null);
        } catch (KeysNotFoundException ex) {
            return new ListResponse<>(null,false, ex.getMessage());
        }

        while(fdeleter.getResult() == null) {

        }

        return fdeleter.getResult();
    }

    public boolean cancelDownload(final long fileRef) {
        return fileRef != 0 && mInstance.cancelDownload(fileRef);
    }

    public boolean cancelUpload(final double fileRef) {
        return !(fileRef == 0) && mInstance.cancelUpload((long) fileRef);
    }
}

package mio.storj.mobile.service;

import java.util.List;

import io.storj.libstorj.KeysNotFoundException;
import mio.storj.mobile.common.responses.ListResponse;
import mio.storj.mobile.common.responses.Response;
import mio.storj.mobile.common.responses.SingleResponse;
import mio.storj.mobile.domain.IDatabase;
import mio.storj.mobile.domain.buckets.Bucket;
import mio.storj.mobile.domain.files.File;
import mio.storj.mobile.service.storj.StorjService;
import mio.storj.mobile.domain.IDatabase;

public class FetchService {

    private final StorjService mStorj;
    private final IDatabase mStore;

    public FetchService(StorjService storj, IDatabase db) {
        mStorj = storj;
        mStore = db;
    }

    public Response getBuckets() throws InterruptedException, KeysNotFoundException {
        ListResponse<Bucket> bucketResponse = mStorj.getBuckets();
        if (!bucketResponse.isSuccess()) {
            return bucketResponse;
        }

        List<Bucket> buckets = bucketResponse.getResult();

        if (buckets.size() == 0) {
            Response deleteBucketsResponse = mStore.buckets().deleteAll();
            if (!deleteBucketsResponse.isSuccess()) {
                return deleteBucketsResponse;
            }

            return new Response(true, null);
        }

        mStore.beginTransaction();

        ListResponse<Bucket> dbBucketResponse = mStore.buckets().getAll();
        if (!dbBucketResponse.isSuccess()) {
            mStore.rollbackTransaction();
            return dbBucketResponse;
        }

        List<Bucket> dbBuckets = dbBucketResponse.getResult();

        int length = buckets.size();

        outer:
        for (Bucket dbBucket : dbBuckets) {
            int i = 0;
            String dbBucketId = dbBucket.id;

            do {
                Bucket bucket = buckets.get(i);
                String id = bucket.id;

                if (dbBucketId.equals(id)) {
                    bucket.isStarred = dbBucket.isStarred;
                    Response updateResponse = mStore.buckets().update(bucket);
                    if (!updateResponse.isSuccess()) {
                        // TODO: log?
                        //return updateResponse;
                    }

                    listShift(buckets, i, length);
                    length--;
                    continue outer;
                }

                i++;
            } while (i < length);

            Response deleteResponse = mStore.buckets().delete(dbBucketId);
            if (!deleteResponse.isSuccess()) {
                // TODO: log?
                //return deleteResponse;
            }
        }

        for (int i = 0; i < length; i++) {
            Response bucketInsertResponse = mStore.buckets().insert(buckets.get(i));
            if (!bucketInsertResponse.isSuccess()) {
                // TODO: log?
                //return bucketInsertResponse;
            }
        }

        mStore.commitTransaction();

        return new Response(true, null);
    }

    public Response getFiles(final String bucketId) throws InterruptedException, KeysNotFoundException {
        ListResponse<File> fileResponse = mStorj.getFiles(bucketId);
        if (!fileResponse.isSuccess()) {
            return fileResponse;
        }

        List<File> files = fileResponse.getResult();
        if(files.size() == 0) {
            Response deleteAllResponse = mStore.files().deleteAll(bucketId);
            if (!deleteAllResponse.isSuccess()) {
                return deleteAllResponse;
            }

            return new Response(true, null);
        }

        mStore.beginTransaction();

        ListResponse<File> dbFileResponse = mStore.files().getAll(bucketId);
        if (!dbFileResponse.isSuccess()) {
            mStore.rollbackTransaction();
            return dbFileResponse;
        }

        List<File> dbFiles = dbFileResponse.getResult();

        int length = files.size();
        boolean[] isUpdate = new boolean[files.size()];

        outer:
        for(File dbFile : dbFiles) {
            int i = 0;
            String dbFileId = dbFile.fileId;

            do {
                File file = files.get(i);
                String id = file.fileId;

                if(dbFileId.equals(id)) {
                    file.isStarred = dbFile.isStarred;
                    file.thumbnail = dbFile.thumbnail;
                    Response updateFileResponse = mStore.files().update(file);
                    if (!updateFileResponse.isSuccess()) {
                        // TODO: log?
                    }

                    listShift(files, i, length);
                    length--;
                    continue outer;
                }

                i++;
            } while(i < length);

            Response deleteFileResponse = mStore.files().delete(dbFileId);
            if (!deleteFileResponse.isSuccess()) {
                // TODO: log?
            }
        }

        for(int i = 0; i < length; i ++) {
            Response isnertFileResponse =mStore.files().insert(files.get(i));
            if (!isnertFileResponse.isSuccess()) {
                // TODO: log?
            }
        }

        mStore.commitTransaction();

        return new Response(true, null);
    }

    public SingleResponse<Bucket> createBucket(final String bucketName) throws InterruptedException, KeysNotFoundException {
        SingleResponse<Bucket> createBucketResponse = mStorj.createBucket(bucketName);
        if (!createBucketResponse.isSuccess()) {
            return createBucketResponse;
        }

        Response insertBucketResponse = mStore.buckets().insert(createBucketResponse.getResult());
        if (!insertBucketResponse.isSuccess()) {
            return new SingleResponse<>(null, false, null);
        }

        return createBucketResponse;
    }

    public Response deleteBucket(final String bucketId) throws InterruptedException, KeysNotFoundException {
        Response deleteBucketResponse = mStorj.deleteBucket(bucketId);
        if (!deleteBucketResponse.isSuccess()) {
            return deleteBucketResponse;
        }

        return mStore.buckets().delete(bucketId);
    }

    public Response deleteFile(final String bucketId, final String fileId) throws InterruptedException, KeysNotFoundException {
        Response deleteFileResponse = mStorj.deleteFile(bucketId, fileId);
        if (!deleteFileResponse.isSuccess()) {
            return deleteFileResponse;
        }

        return mStore.files().delete(fileId);
    }

    private <T> void listShift(List<T> array, int pos, int length)  {
        while(pos < length - 1) {
            array.set(pos, array.get(pos + 1));
            pos++;
        }
    }
}

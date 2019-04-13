package mio.storj.mobile.service.storj;

import mio.storj.mobile.domain.buckets.Bucket;
import mio.storj.mobile.domain.files.File;
import mio.storj.mobile.domain.buckets.Bucket;
import mio.storj.mobile.domain.files.File;

public class Converters {
    public static Bucket toDomain(io.storj.libstorj.Bucket bucket) {
        Bucket result = new Bucket();

        result.id = bucket.getId();
        result.name = bucket.getName();
        result.created = bucket.getCreated();
        result.hash = bucket.hashCode();
        result.isDecrypted = bucket.isDecrypted();

        return result;
    }

    public static File toDomain(io.storj.libstorj.File file) {
        File result = new File();

        result.bucketId = file.getBucketId();
        result.created = file.getCreated();
        result.erasure = file.getErasure();
        result.hmac = file.getHMAC();
        result.fileId = file.getId();
        result.index = file.getIndex();
        result.mimeType = file.getMimeType();
        result.name = file.getName();
        result.fileUri = null;
        result.thumbnail = null;
        result.downloadState = 0;
        result.fileHandle = 0;
        result.size = file.getSize();
        result.isDecrypted = file.isDecrypted();
        result.isSynced = false;
        result.isStarred = false;

        return result;
    }
}

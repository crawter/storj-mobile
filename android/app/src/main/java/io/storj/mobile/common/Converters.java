package io.storj.mobile.common;

import io.storj.mobile.domain.buckets.Bucket;
import io.storj.mobile.domain.files.File;

public class Converters {
    public static Bucket toDomain(io.storj.libstorj.Bucket bucket) {
        return new Bucket(bucket.getId(), bucket.getName(), bucket.getCreated(), bucket.hashCode(), bucket.isDecrypted(), false);
    }

    public static File toDomain(io.storj.libstorj.File file) {
        return new File(file.getBucketId(), file.getCreated(), file.getErasure(), file.getHMAC(), file.getId(),
                file.getIndex(), file.getMimeType(), file.getName(), null, null, 0,
                0, file.getSize(), file.isDecrypted(), false, false);
    }
}

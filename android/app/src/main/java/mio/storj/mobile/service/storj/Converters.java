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
        return new File(file.getBucketId(), file.getCreated(), file.getErasure(), file.getHMAC(), file.getId(),
                file.getIndex(), file.getMimeType(), file.getName(), null, null, 0,
                0, file.getSize(), file.isDecrypted(), false, false);
    }
}

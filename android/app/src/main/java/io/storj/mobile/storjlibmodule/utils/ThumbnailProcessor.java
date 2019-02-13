package io.storj.mobile.storjlibmodule.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

import io.storj.mobile.common.responses.SingleResponse;


public class ThumbnailProcessor {
    private final int THUMBNAIL_SIZE = 64;

    public SingleResponse<String> getThumbnail(String filePath) {
        byte[] imageData = null;
        SingleResponse<String> errorResult = new SingleResponse<>(null, false, "Unable to process thumbnail");

        try(FileInputStream fis = new FileInputStream(filePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            Bitmap imageBitmap = BitmapFactory.decodeStream(fis);

            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            imageData = baos.toByteArray();

            String encoded = Base64.encodeToString(imageData, Base64.DEFAULT);

            return new SingleResponse<>(encoded, true, null);
        }
        catch(Exception ex) {
            Log.d("THUMBNAIL_PROCESS_ERROR", ex.getMessage());
        }

        return errorResult;
    }
}

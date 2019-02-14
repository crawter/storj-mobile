package io.storj.mobile.service.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

public class ThumbnailProcessor {
    private final int THUMBNAIL_SIZE = 64;

    public String getThumbnail(String filePath) {
        byte[] imageData = null;

        try(FileInputStream fis = new FileInputStream(filePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            Bitmap imageBitmap = BitmapFactory.decodeStream(fis);

            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            imageData = baos.toByteArray();
        } catch(Exception ex) {
            Log.d("THUMBNAIL_PROCESS_ERROR", ex.getMessage());
        }

        return Base64.encodeToString(imageData, Base64.DEFAULT);
    }
}

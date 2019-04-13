package mio.storj.mobile.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonSingle {
    private static Gson sInstanse;

    public static Gson getInstanse() {
        if(sInstanse == null) {
            sInstanse = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        }

        return sInstanse;
    }
}

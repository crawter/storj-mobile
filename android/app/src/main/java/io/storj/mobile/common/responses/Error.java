package io.storj.mobile.common.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Error {
    @Expose
    @SerializedName("message")
    private String _message;
    @Expose
    @SerializedName("code")
    private int _code;

    public Error(String message, int code) {
        _message = message;
        _code = code;
    }

    public String getMessage() {
        return _message;
    }

    public int getCode() {
        return _code;
    }
}

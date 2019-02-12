package io.storj.mobile.common.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SingleResponse<T> extends Response {
    @Expose
    @SerializedName("result")
    private T _result;

    public SingleResponse(T result, boolean isSuccess, String errorMessage) {
        super(isSuccess, errorMessage);
        _result = result;
    }

    public SingleResponse(T result, boolean isSuccess, String errorMessage, int errorCode) {
        super(isSuccess, errorMessage, errorCode);
        _result = result;
    }

    public T getResult() {
        return _result;
    }
}


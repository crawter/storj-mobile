package io.storj.mobile.common.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListResponse<T> extends Response {
    @Expose
    @SerializedName("result")
    private List<T> _result;

    public ListResponse(List<T> result, boolean isSuccess, String errorMessage) {
        super(isSuccess, errorMessage);
        _result = result;
    }

    public ListResponse(List<T> result, boolean isSuccess, String errorMessage, int errorCode) {
        super(isSuccess, errorMessage, errorCode);
        _result = result;
    }

    public List<T> getResult() {
        return _result;
    }
}

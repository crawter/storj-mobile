package mio.storj.mobile.common.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import mio.storj.mobile.common.Convertible;

public class Response extends Convertible {
    @Expose
    @SerializedName("isSuccess")
    private boolean _isSuccess = false;
    @Expose
    @SerializedName("error")
    private Error _error = null;

    public Response(boolean isSuccess, String errorMessage) {
        _isSuccess = isSuccess;
        _error = new Error(errorMessage, 0);
    }

    public Response(boolean isSuccess, String errorMessage, int errorCode) {
        _isSuccess = isSuccess;
        _error = new Error(errorMessage, errorCode);
    }


    public boolean isSuccess() {
        return _isSuccess;
    }

    public Error getError() {
        return _error;
    }
}


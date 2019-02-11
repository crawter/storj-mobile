package io.storj.mobile.common.responses;

public class Response {
    private boolean _isSuccess = false;
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
    public Error getError() { return _error; }
}


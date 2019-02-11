package io.storj.mobile.common.responses;

public class SingleResponse<T> extends Response {
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


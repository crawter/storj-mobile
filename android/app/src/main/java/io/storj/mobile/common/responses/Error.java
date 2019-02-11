package io.storj.mobile.common.responses;

public class Error {
    private String _message;
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

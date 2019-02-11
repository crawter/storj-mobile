package io.storj.mobile.common.responses;

import java.util.List;

public class ListResponse<T> extends Response {
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

package io.storj.mobile.service.download;

public enum DownloadStateEnum {
    DEFAULT(0),
    DOWNLOADING(1),
    DOWNLOADED(2);

    private int _value;

    DownloadStateEnum(int value) {
        _value = value;
    }

    public int getValue() {
        return _value;
    }
}

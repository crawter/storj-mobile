package mio.storj.mobile.service;

public interface IEventEmitter {
    void sendEvent(String eventName, String result);
}

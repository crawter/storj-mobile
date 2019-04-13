package mio.storj.mobile.storjlibmodule.services.eventemitters;

import android.content.ContentValues;

import mio.storj.mobile.storjlibmodule.services.SyncQueueService;
import mio.storj.mobile.storjlibmodule.services.UploadService;
import mio.storj.mobile.storjlibmodule.services.SyncQueueService;
import mio.storj.mobile.storjlibmodule.services.UploadService;

public class SynchronizationEventEmitter {
    private BaseEventEmitter mEventEmitter;

    public SynchronizationEventEmitter(BaseEventEmitter eventEmitter) {
        mEventEmitter = eventEmitter;
    }

    public void SyncStarted() {
        emit(SyncQueueService.EVENT_SYNC_STARTED, null);
    }

    public void SyncEntryUpdated(int id) {
        ContentValues map = new ContentValues();
        map.put(UploadService.PARAM_SYNC_ENTRY_ID, id);

        emit(SyncQueueService.EVENT_SYNC_ENTRY_UPDATED, map);
    }

    private void emit(String eventName, ContentValues map) {
        if(mEventEmitter == null)
            return;

        mEventEmitter.Emit(eventName, map);
    }
}

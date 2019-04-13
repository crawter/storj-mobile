package mio.storj.mobile.storjlibmodule.rnmodules;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import mio.storj.mobile.common.responses.Response;
import mio.storj.mobile.common.responses.SingleResponse;
import mio.storj.mobile.dataprovider.settings.SettingsContract;
import mio.storj.mobile.domain.settings.Settings;
import mio.storj.mobile.service.SyncService;
import mio.storj.mobile.service.download.DownloadStateEnum;
import mio.storj.mobile.storjlibmodule.enums.SyncSettingsEnum;
import mio.storj.mobile.storjlibmodule.services.SyncSchedulerJobService;
import mio.storj.mobile.storjlibmodule.services.SyncQueueService;
import mio.storj.mobile.storjlibmodule.enums.SyncSettingsEnum;

public class SyncModule extends ReactContextBaseJavaModule {
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private static int NUMBER_OF_CORES =
            Runtime.getRuntime().availableProcessors();

    private final BlockingDeque<Runnable> mWorkQueue;
    private final ThreadPoolExecutor mThreadPool;

    private final SyncService mSyncService;
    
    public SyncModule(ReactApplicationContext reactContext, SyncService syncService) {
        
        super(reactContext);
        mWorkQueue = new LinkedBlockingDeque<>();
        mThreadPool = new ThreadPoolExecutor(
                NUMBER_OF_CORES,
                NUMBER_OF_CORES,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                mWorkQueue
        );
        mSyncService = syncService;
    }

    @Override
    public String getName() {
        return "SyncModule";
    }

    @ReactMethod
    public void listBuckets(final String sortingMode, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(mSyncService.listBuckets(sortingMode).toJson());
            }
        }).run();
    }

    @ReactMethod
    public void listFiles(final String bucketId, final String sortingMode, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(bucketId == null || bucketId.isEmpty()) {
                    promise.resolve(new Response(false,  "bucket id is not valid").toJson());
                    return;
                }

                promise.resolve(mSyncService.listFiles(bucketId, sortingMode).toJson());
            }
        }).run();
    }

    @ReactMethod
    public void listAllFiles(final String sortingMode, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(mSyncService.listAllFiles(sortingMode).toJson());
            }
        }).run();
    }

    @ReactMethod
    public void listUploadingFiles(final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(mSyncService.listUploadingFiles().toJson());
            }
        }).run();
    }

    @ReactMethod
    public void getUploadingFile(final String fileHandle, final Promise promise) {
        if (fileHandle == null) {
            promise.resolve(new Response(false, "invalid file handle").toJson());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(mSyncService.getUploadingFile(fileHandle).toJson());
            }
        }).run();
    }

    @ReactMethod
    public void getFile(final String fileId, final Promise promise) {
        if (fileId == null || fileId.isEmpty()) {
            promise.resolve(new Response(false, "invalid file id").toJson());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(mSyncService.getFile(fileId).toJson());
            }
        }).run();
    }

    @ReactMethod
    public void updateBucketStarred(final String bucketId, final boolean isStarred, final Promise promise) {
        if (bucketId == null || bucketId.isEmpty()) {
            promise.resolve(new Response(false, "invalid bucket id").toJson());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(mSyncService.updateBucketStarred(bucketId, isStarred).toJson());
            }
        }).run();
    }

    @ReactMethod
    public void updateFileStarred(final String fileId, final boolean isStarred, final Promise promise) {
        if (fileId == null || fileId.isEmpty()) {
            promise.resolve(new Response(false, "invalid file id").toJson());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(mSyncService.updateFileStarred(fileId, isStarred).toJson());
            }
        }).run();
    }

    @ReactMethod
    public void listSettings(final String id, final Promise promise) {
        if (id == null || id.isEmpty()) {
            promise.resolve(new Response(false, "invalid setting id").toJson());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(mSyncService.listSettings(id).toJson());
            }
        }).run();
    }

    @ReactMethod
    public void insertSyncSetting(final String id, final Promise promise) {
        if (id == null || id.isEmpty()) {
            promise.resolve(new Response(false, "invalid setting id").toJson());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(mSyncService.insertSyncSetting(id).toJson());
            }
        }).run();
    }

    @ReactMethod
    public void updateSettings(final String id, final int syncSettings, final Promise promise) {
        if (id == null || id.isEmpty()) {
            promise.resolve(new Response(false, "invalid setting id").toJson());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                SingleResponse<Settings> settingResponse = mSyncService.getSetings(id);
                if (!settingResponse.isSuccess()) {
                    promise.resolve(settingResponse.toJson());
                }

                Settings settingToUpdate = settingResponse.getResult();
                settingToUpdate.setSyncSettings(syncSettings);

                promise.resolve(mSyncService.updateSettings(settingToUpdate).toJson());
            }
        }).run();
    }

    @ReactMethod
    public void setFirstSignIn(final String id, final int syncSettings, final Promise promise) {
        if (id == null || id.isEmpty()) {
            promise.resolve(new Response(false, "invalid setting id").toJson());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                SingleResponse<Settings> settingResponse = mSyncService.getSetings(id);
                if (!settingResponse.isSuccess()) {
                    promise.resolve(settingResponse.toJson());
                }

                Settings settingToUpdate = settingResponse.getResult();
                settingToUpdate.setSyncSettings(syncSettings);
                settingToUpdate.setFirstSignIn(false);

                promise.resolve(mSyncService.updateSettings(settingToUpdate).toJson());
            }
        }).run();
    }

    @ReactMethod
    public void changeSyncStatus(final String id, final boolean syncStatus, final Promise promise) {
        if(id == null || id.isEmpty()) {
            promise.resolve(new Response(false, "setting id is not specified!"));
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Driver driver = new GooglePlayDriver(getReactApplicationContext());
                FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
                dispatcher.cancelAll();
                cancelSync();

                SingleResponse<Settings> changeSyncStatusResponse = mSyncService.changeSyncStatus(id, syncStatus);
                promise.resolve(changeSyncStatusResponse.toJson());

                if(changeSyncStatusResponse.isSuccess() && syncStatus) {
                    scheduleSync(changeSyncStatusResponse.getResult(), dispatcher);
                }
            }
        }).run();
    }

    private void cancelSync() {
        Intent cancelSyncIntent = new Intent(getReactApplicationContext(), SyncQueueService.class);
        cancelSyncIntent.setAction(SyncQueueService.ACTION_SYNC_CANCEL);

        getReactApplicationContext().startService(cancelSyncIntent);
    }

    @ReactMethod
    public void getSyncQueue(final Promise promise) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                promise.resolve(mSyncService.getSyncQueue().toJson());
            }
        });
    }

    @ReactMethod
    public void getSyncQueueEntry(final int id, final Promise promise) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                promise.resolve(mSyncService.getSyncQueueEntry(id).toJson());
            }
        });
    }

    @ReactMethod
    public void updateSyncQueueEntryFileName(final int id, final String newFileName, final Promise promise) {
        if(newFileName == null) {
            promise.resolve(new Response(false, "File name can't be null!").toJson());
        }
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                promise.resolve(mSyncService.updateSyncEntryName(id, newFileName).toJson());
            }});
    }

    @ReactMethod
    public void updateSyncQueueEntryStatus(final int id, final int newStatus, final Promise promise) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                promise.resolve(mSyncService.updateSyncEntryStatus(id, newStatus).toJson());
            }});
    }


    @ReactMethod
    public void checkFile(final String fileId, final String localPath, final Promise promise) {
        if(localPath == null) {
            promise.resolve(new Response(false, "localPath is null!").toJson());
            return;
        }

        File file = new File(localPath);

        if(!file.exists() || file.isDirectory()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Response fileUpdateResponse = mSyncService.updateFileState(
                            fileId,
                            null,
                            DownloadStateEnum.DEFAULT.getValue(),
                            0);

                    if (!fileUpdateResponse.isSuccess()) {
                        promise.resolve(new Response(false, "File has been removed from file System!").toJson());
                    }
                }
            }).run();

            return;
        }

        promise.resolve(new Response(true, null).toJson());
    }

    private void scheduleSync(Settings settings, FirebaseJobDispatcher dispatcher) {
        //dispatcher.cancelAll();

        Bundle bundle = new Bundle();
        bundle.putString(SettingsContract._SETTINGS_ID, settings.getId());

        List<Integer> constraints = new ArrayList<Integer>();
        int syncSettings = settings.getSyncSettings();

        if(checkSyncSettings(syncSettings, SyncSettingsEnum.ON_WIFI.getValue())) {
            constraints.add(Constraint.ON_UNMETERED_NETWORK);
        }

        if(checkSyncSettings(syncSettings, SyncSettingsEnum.ON_CHARGING.getValue())) {
            constraints.add(Constraint.DEVICE_CHARGING);
        }

        Job myJob = getJobBuilder(dispatcher, bundle, constraints).build();
        dispatcher.schedule(myJob);
    }

    private boolean checkSyncSettings(int syncSettings, int syncValue) {
        return (syncSettings & syncValue) == syncValue;
    }

    private Job.Builder getJobBuilder(FirebaseJobDispatcher dispatcher, Bundle bundle, List<Integer> constaraints) {
        Job.Builder myJobBuilder = dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(SyncSchedulerJobService.class)
                // uniquely identifies the job
                .setTag("sync-job")
                // one-off job
                .setRecurring(false)
                // don't persist past a device reboot
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                // start between 0 and 15 minutes (900 seconds)
                .setTrigger(Trigger.executionWindow(60, 120))
                // overwrite an existing job with the same tag
                .setReplaceCurrent(true)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setExtras(bundle);

        int constraintSize = constaraints.size();

        if(constraintSize > 0) {
            //Integer[] intArray = (Integer[])constaraints.toArray();
            int[] constArray = new int[constraintSize];

            for(int i = 0; i < constraintSize; i++) {
                constArray[i] = constaraints.get(i);
            }

            myJobBuilder.setConstraints(constArray);
        }

        return myJobBuilder;
    }
}

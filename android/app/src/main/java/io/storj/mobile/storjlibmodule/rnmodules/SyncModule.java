package io.storj.mobile.storjlibmodule.rnmodules;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.common.internal.Objects;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.storj.mobile.common.responses.Response;
import io.storj.mobile.common.responses.SingleResponse;
import io.storj.mobile.dataprovider.settings.SettingsContract;
import io.storj.mobile.domain.settings.Settings;
import io.storj.mobile.service.SyncService;
import io.storj.mobile.storjlibmodule.GsonSingle;
import io.storj.mobile.storjlibmodule.enums.DownloadStateEnum;
import io.storj.mobile.storjlibmodule.enums.SyncSettingsEnum;
import io.storj.mobile.storjlibmodule.services.SynchronizationSchedulerJobService;
import io.storj.mobile.storjlibmodule.services.SynchronizationService;

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
                String wer = toJson(mSyncService.listBuckets(sortingMode));
                promise.resolve(wer);
            }
        }).run();
    }

    @ReactMethod
    public void listFiles(final String bucketId, final String sortingMode, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(bucketId == null || bucketId.isEmpty()) {
                    promise.resolve(toJson(new Response(false,  "bucket id is not valid")));
                    return;
                }

                promise.resolve(toJson(mSyncService.listFiles(bucketId, sortingMode)));
            }
        }).run();
    }

    @ReactMethod
    public void listAllFiles(final String sortingMode, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(toJson(mSyncService.listAllFiles(sortingMode)));
            }
        }).run();
    }

    @ReactMethod
    public void listUploadingFiles(final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(toJson(mSyncService.listUploadingFiles()));
            }
        }).run();
    }

    @ReactMethod
    public void getUploadingFile(final String fileHandle, final Promise promise) {
        if (fileHandle == null) {
            promise.resolve(toJson(new Response(false, "invalid file handle")));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(toJson(mSyncService.getUploadingFile(fileHandle)));
            }
        }).run();
    }

    @ReactMethod
    public void getFile(final String fileId, final Promise promise) {
        if (fileId == null || fileId.isEmpty()) {
            promise.resolve(toJson(new Response(false, "invalid file id")));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(toJson(mSyncService.getFile(fileId)));
            }
        }).run();
    }

    @ReactMethod
    public void updateBucketStarred(final String bucketId, final boolean isStarred, final Promise promise) {
        if (bucketId == null || bucketId.isEmpty()) {
            promise.resolve(toJson(new Response(false, "invalid bucket id")));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(toJson(mSyncService.updateBucketStarred(bucketId, isStarred)));
            }
        }).run();
    }

    @ReactMethod
    public void updateFileStarred(final String fileId, final boolean isStarred, final Promise promise) {
        if (fileId == null || fileId.isEmpty()) {
            promise.resolve(toJson(new Response(false, "invalid file id")));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(toJson(mSyncService.updateFileStarred(fileId, isStarred)));
            }
        }).run();
    }

    @ReactMethod
    public void listSettings(final String id, final Promise promise) {
        if (id == null || id.isEmpty()) {
            promise.resolve(toJson(new Response(false, "invalid setting id")));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(toJson(mSyncService.listSettings(id)));
            }
        }).run();
    }

    @ReactMethod
    public void insertSyncSetting(final String id, final Promise promise) {
        if (id == null || id.isEmpty()) {
            promise.resolve(toJson(new Response(false, "invalid setting id")));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(toJson(mSyncService.insertSyncSetting(id)));
            }
        }).run();
    }

    @ReactMethod
    public void updateSettings(final String id, final int syncSettings, final Promise promise) {
        if (id == null || id.isEmpty()) {
            promise.resolve(toJson(new Response(false, "invalid setting id")));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                SingleResponse<Settings> settingResponse = mSyncService.getSetings(id);
                if (!settingResponse.isSuccess()) {
                    promise.resolve(toJson(settingResponse));
                }

                Settings settingToUpdate = settingResponse.getResult();
                settingToUpdate.setSyncSettings(syncSettings);

                promise.resolve(toJson(mSyncService.updateSettings(settingToUpdate)));
            }
        }).run();
    }

    @ReactMethod
    public void setFirstSignIn(final String id, final int syncSettings, final Promise promise) {
        if (id == null || id.isEmpty()) {
            promise.resolve(toJson(new Response(false, "invalid setting id")));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                SingleResponse<Settings> settingResponse = mSyncService.getSetings(id);
                if (!settingResponse.isSuccess()) {
                    promise.resolve(toJson(settingResponse));
                }

                Settings settingToUpdate = settingResponse.getResult();
                settingToUpdate.setSyncSettings(syncSettings);
                settingToUpdate.setFirstSignIn(false);

                promise.resolve(toJson(mSyncService.updateSettings(settingToUpdate)));
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
                promise.resolve(toJson(changeSyncStatusResponse));

                if(changeSyncStatusResponse.isSuccess() && syncStatus) {
                    scheduleSync(changeSyncStatusResponse.getResult(), dispatcher);
                }
            }
        }).run();
    }

    private void cancelSync() {
        Intent cancelSyncIntent = new Intent(getReactApplicationContext(), SynchronizationService.class);
        cancelSyncIntent.setAction(SynchronizationService.ACTION_SYNC_CANCEL);

        getReactApplicationContext().startService(cancelSyncIntent);
    }

    @ReactMethod
    public void getSyncQueue(final Promise promise) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                promise.resolve(toJson(mSyncService.getSyncQueue()));
            }
        });
    }

    @ReactMethod
    public void getSyncQueueEntry(final int id, final Promise promise) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                promise.resolve(toJson(mSyncService.getSyncQueueEntry(id)));
            }
        });
    }

    @ReactMethod
    public void updateSyncQueueEntryFileName(final int id, final String newFileName, final Promise promise) {
        if(newFileName == null) {
            promise.resolve(toJson(new Response(false, "File name can't be null!")));
        }
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                promise.resolve(toJson(mSyncService.updateSyncEntryName(id, newFileName)));
            }});
    }

    @ReactMethod
    public void updateSyncQueueEntryStatus(final int id, final int newStatus, final Promise promise) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                promise.resolve(toJson(mSyncService.updateSyncEntryStatus(id, newStatus)));
            }});
    }


    @ReactMethod
    public void checkFile(final String fileId, final String localPath, final Promise promise) {
        if(localPath == null) {
            promise.resolve(toJson(new Response(false, "localPath is null!")));
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
                        promise.resolve(toJson(new Response(false, "File has been removed from file System!")));
                    }
                }
            }).run();

            return;
        }

        promise.resolve(toJson(new Response(true, null)));
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
                .setService(SynchronizationSchedulerJobService.class)
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

    private <T> String toJson(T convertible) {
        return GsonSingle.getInstanse().toJson(convertible);
    }
}

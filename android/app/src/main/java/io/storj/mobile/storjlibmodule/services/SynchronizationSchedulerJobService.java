package io.storj.mobile.storjlibmodule.services;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.storj.mobile.common.responses.SingleResponse;
import io.storj.mobile.dataprovider.Database;
import io.storj.mobile.dataprovider.buckets.BucketContract;
import io.storj.mobile.dataprovider.files.FileContract;
import io.storj.mobile.dataprovider.settings.SettingsContract;
import io.storj.mobile.domain.IDatabase;
import io.storj.mobile.domain.buckets.Bucket;
import io.storj.mobile.domain.settings.Settings;
import io.storj.mobile.domain.syncqueue.SyncQueueEntry;
import io.storj.mobile.storjlibmodule.enums.SyncSettingsEnum;

public class SynchronizationSchedulerJobService extends JobService {
    private AsyncTask mBackgroundTask;

    @Override
    public boolean onStartJob(final JobParameters job) {
        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                IDatabase db = Database.getInstance();
                String settingsId = job.getExtras().getString(SettingsContract._SETTINGS_ID);

                if(settingsId == null) {
                    Log.d(DEBUG_TAG, "sync: " + "No settings Id! Aborting!");
                    return null;
                }

                SingleResponse<Settings> getSettingsResponse = db.settings().get(settingsId);
                if (!getSettingsResponse.isSuccess()) {
                    return  null;
                }

                Settings settings = getSettingsResponse.getResult();
                int syncSettings = settings.getSyncSettings();

                syncFolder(settingsId, syncSettings, SyncSettingsEnum.SYNC_PHOTOS);
                syncFolder(settingsId, syncSettings, SyncSettingsEnum.SYNC_MOVIES);
                syncFolder(settingsId, syncSettings, SyncSettingsEnum.SYNC_DOCUMENTS);
                syncFolder(settingsId, syncSettings, SyncSettingsEnum.SYNC_MUSIC);

                db.settings().update(settings);

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                Intent syncIntent = new Intent(SynchronizationSchedulerJobService.this, SynchronizationService.class);
                syncIntent.setAction(SynchronizationService.ACTION_SYNC);
                SynchronizationSchedulerJobService.this.startService(syncIntent);
                jobFinished(job, true);
            }
        };

        mBackgroundTask.execute();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if(mBackgroundTask != null)
            mBackgroundTask.cancel(true);
        return true;
    }

    private final static String DEBUG_TAG = "SYNCHRONIZATION DEBUG";

    private void syncFolder(String settingsId, int syncSettings, SyncSettingsEnum syncEnum) {
        IDatabase db = Database.getInstance();

        if(syncEnum != SyncSettingsEnum.SYNC_DOCUMENTS
                && syncEnum != SyncSettingsEnum.SYNC_MUSIC
                && syncEnum != SyncSettingsEnum.SYNC_MOVIES
                && syncEnum != SyncSettingsEnum.SYNC_PHOTOS) {
            return;
        }

        int syncValue = syncEnum.getValue();
        String bucketName = syncEnum.getBucketName();

        SingleResponse<Bucket> getBucketResponse = db.buckets().get(BucketContract._NAME, bucketName);
        if (!getBucketResponse.isSuccess()) {
            return;
        }

        boolean isSyncOn = (syncSettings & syncValue) == syncValue;

        if(isSyncOn) {
            _syncFolder(syncEnum.geetFolderUri(), getBucketResponse.getResult().getId(), settingsId, syncSettings);
        }

    }

    private void _syncFolder(String folderUri, String bucketId, String settingsId, int syncSettings) {
        IDatabase db = Database.getInstance();

        Log.d(DEBUG_TAG, "sync: " + "Start sync of " + folderUri + " and " + bucketId);
        File folder = new File(folderUri);

        if(!folder.exists() || !folder.isDirectory()) {
            Log.d(DEBUG_TAG, "sync: " + "File not exist or is not a directory");
            return;
        }

        File[] files = folder.listFiles();

        for(File file : files) {
            Log.d(DEBUG_TAG, "sync: " + "File, name: " + file.getName());
            if (file.isDirectory()) {
                Log.d(DEBUG_TAG, "sync: " + "File is directory continue");
                continue;
            }

            SingleResponse<io.storj.mobile.domain.files.File> getFileResponse = db.files().get(file.getName(), FileContract._NAME, bucketId);
            SyncQueueEntry syncEntry = db.syncQueueEntries().get(file.getPath(), bucketId);

            if (!getFileResponse.isSuccess() && syncEntry == null) {
                SyncQueueEntry dbo = new SyncQueueEntry(0, file.getName(), file.getPath(), 0, 0, 0, 0, null, bucketId, 0);
                db.syncQueueEntries().insert(dbo);
            }
        }
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}

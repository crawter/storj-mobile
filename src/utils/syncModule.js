import { NativeModules, Platform } from 'react-native';

const SyncModule = (() => {
    let instance = null;
    const syncModule = Platform.OS ==="android" ? NativeModules.SyncModule : NativeModules.SyncModuleIOS;

    class SyncModule {
        constructor() {

        }

        async listBuckets(sortingMode = "date") {
            return JSON.parse(await syncModule.listBuckets(String(sortingMode)));
        }

        async listFiles(bucketId, sortingMode) {
            return JSON.parse(await syncModule.listFiles(bucketId, sortingMode));
        }

        async listAllFiles(sortingMode) {
            return JSON.parse(await syncModule.listAllFiles(sortingMode));
        }

        async listUploadingFiles(bucketId) {
            return JSON.parse(await syncModule.listUploadingFiles());
        }

        async getUploadingFile(fileHandle) {
            return JSON.parse(await syncModule.getUploadingFile(String(fileHandle)));
        }

        async getFile(fileId) {
            return JSON.parse(await syncModule.getFile(fileId));
        }

        async updateBucketStarred(bucketId, isStarred) {
            return JSON.parse(await syncModule.updateBucketStarred(bucketId, isStarred));
        }

        async updateFileStarred(fileId, isStarred) {
            return JSON.parse(await syncModule.updateFileStarred(fileId, isStarred));
        }

        async listSettings(settingsId) {
            return JSON.parse(await syncModule.listSettings(String(settingsId)));
        }

        async insertSyncSetting(settingsId) {
            return JSON.parse(await syncModule.insertSyncSetting(String(settingsId)));
        }
        
        async updateSyncSettings(settingsId, syncSettings) {
            return JSON.parse(await syncModule.updateSyncSettings(String(settingsId), Number(syncSettings)));
        }

        async setFirstSignIn(settingsId, syncSettings) {
            return JSON.parse(await syncModule.setFirstSignIn(String(settingsId), Number(syncSettings)));
        }

        async changeSyncStatus(settingsId, value) {
            return JSON.parse(await syncModule.changeSyncStatus(String(settingsId), Boolean(value)));
        }

        async getSyncQueue() {
            return JSON.parse(await syncModule.getSyncQueue());
        }

        async getSyncQueueEntry(id) {
            return JSON.parse(await syncModule.getSyncQueueEntry(Number(id)));
        }

        async updateSyncQueueEntryFileName(id, newFileName) {
            return JSON.parse(await syncModule.updateSyncQueueEntryFileName(Number(id), String(newFileName)));
        }

        async updateSyncQueueEntryStatus(id, newStatus) {
            return JSON.parse(await syncModule.updateSyncQueueEntryStatus(Number(id), Number(newStatus)));
        }

        async checkFile(fileId, localPath) {
            return JSON.parse(await syncModule.checkFile(String(fileId), String(localPath)));
        }
    }  

    return {
        /**
         * @returns {SyncModule}
         */
        getInstance: function() {
            if(!instance) {
                instance = new SyncModule();
            }

            return instance;
        }
    };

})();

export default SyncModule.getInstance();
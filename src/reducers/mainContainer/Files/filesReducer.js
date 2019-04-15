import FileListManager from '../../../utils/itemManagers/fileListManager';
import { FILE_ACTIONS, MAIN_ACTIONS } from '../../../utils/constants/actionConstants';

const { DISABLE_SELECTION_MODE } = MAIN_ACTIONS;

const { 
    LIST_FILES,
    UPLOAD_FILE_START,
    UPLOAD_FILE_SUCCESS,
    UPLOAD_FILE_ERROR,
    DOWNLOAD_FILE_SUCCESS,
    DOWNLOAD_FILE_ERROR,
    DELETE_FILE, 
    SELECT_FILE,
    SELECT_FILES, 
    DESELECT_FILE,
    DESELECT_FILES,
    UPDATE_FILE_UPLOAD_PROGRESS,
    UPDATE_FILE_DOWNLOAD_PROGRESS,
    FILE_DOWNLOAD_CANCELED,
    FILE_UPLOAD_CANCELED,
    LIST_UPLOADING_FILES,
    FILE_UPDATE_FAVOURITE
} = FILE_ACTIONS;

/**
 * InitialState
 * @property {FileListModel[]} fileListModels
 */
const initialState = {
    fileListModels: [],
    uploadingFileListModels: [],
    uploadingFiles: []
};

export default function filesReducer(state = initialState, action) {
    let newState = Object.assign({}, state);
    let filesManager = new FileListManager(newState.fileListModels, newState.uploadingFileListModels);
    
    switch(action.type) {
        case LIST_FILES:
            newState.fileListModels = filesManager.listFiles(action.payload.bucketId, action.payload.files);
            newState.uploadingFileListModels = filesManager.listUploadingFiles();
            break;
        case UPLOAD_FILE_START:
            newState.uploadingFileListModels = filesManager.addFileEntryU(action.payload.file);
            break;
        case UPLOAD_FILE_SUCCESS:
            newState.uploadingFileListModels = filesManager.deleteFileEntryU(action.payload.bucketId, action.payload.filePath);
            newState.fileListModels = filesManager.addFileEntry(action.payload.file);
            break;
        case UPLOAD_FILE_ERROR:
            newState.uploadingFileListModels = filesManager.delete(action.payload.fileHandle);
            break;
        case UPDATE_FILE_UPLOAD_PROGRESS: 
            newState.uploadingFileListModels = filesManager.update(action.payload.fileHandle, action.payload.progress, action.payload.uploaded);
            break;
        case DOWNLOAD_FILE_SUCCESS:
            newState.fileListModels = filesManager.fileDownloaded(action.payload.fileId, action.payload.filePath, action.payload.thumbnail);            
            break;
        case DOWNLOAD_FILE_ERROR:
            newState.fileListModels = filesManager.fileDownloaded(action.payload.fileId, null, null);
            break;
        case UPDATE_FILE_DOWNLOAD_PROGRESS:             
            newState.fileListModels = filesManager.updateFileDownloadingProgress(action.payload.fileId, action.payload.progress, action.payload.fileRef);
            break;
        case FILE_UPLOAD_CANCELED:
            newState.uploadingFileListModels = filesManager.delete(action.payload.fileId);
            break;
        case FILE_DOWNLOAD_CANCELED:
            newState.fileListModels = filesManager.cancelDownload(action.payload.fileId);
            break;
        case DELETE_FILE:
            newState.fileListModels = filesManager.deleteFileEntry(action.payload.bucketId, action.payload.fileId);
           break;
        case SELECT_FILE: 
            newState.fileListModels = filesManager.selectFile(action.payload.file);
            break;
        case SELECT_FILES: 
            newState.fileListModels = filesManager.selectFiles(action.payload.filteredFiles); 
            break;
        case DESELECT_FILE:
            newState.fileListModels = filesManager.deselectFile(action.payload.file);
            break;
        case DESELECT_FILES:
            newState.fileListModels = filesManager.clearSelection();
            break;
        case DISABLE_SELECTION_MODE:
            newState.fileListModels = filesManager.clearSelection();
            break;
        case LIST_UPLOADING_FILES:
            newState.uploadingFileListModels = action.payload.uploadingFiles;
            break;
        case FILE_UPDATE_FAVOURITE:
            newState.fileListModels = filesManager.updateFileStarred(action.payload.files, action.payload.starredStatus);
            break;  
        default:
            return state || initialState;
    }

    return newState;
}

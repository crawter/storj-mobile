export const ACTIONS = {
    //register constants
    REGISTER_SUCCESS: 'REGISTER_SUCCESS',
    REGISTER_ERROR: 'REGISTER_ERROR',
    REGISTER: 'REGISTER',

    //login constans
    LOGIN_SUCCESS: "LOGIN_SUCCESS",
    LOGIN_ERROR: "LOGIN_ERROR",
    LOGIN: "LOGIN",

    CLEAR: 'CLEAR',
    SAVE_MNEMONIC: 'SAVE_MNEMONIC'
};

//main reducer action constants
export const MAIN_ACTIONS = {
    SET_EMAIL: "SET_EMAIL",
    SHOW_ACTION_BAR: 'SHOW_ACTION_BAR',
    HIDE_ACTION_BAR: 'HIDE_ACTION_BAR',
    ENABLE_SELECTION_MODE: 'ENABLE_SELECTION_MODE',
    DISABLE_SELECTION_MODE: 'DISABLE_SELECTION_MODE',
    SINGLE_ITEM_ACTIONS_SELECTED: 'SINGLE_ITEM_ACTIONS_SELECTED',
    SHOW_CREATE_BUCKET_INPUT: 'SHOW_CREATE_BUCKET_INPUT',
    HIDE_CREATE_BUCKET_INPUT: 'HIDE_CREATE_BUCKET_INPUT',
    SET_FIRST_SIGN_IN: 'SET_FIRST_SIGN_IN',
    REMOVE_FIRST_SIGN_IN: 'REMOVE_FIRST_SIGN_IN',
    SET_LOADING: 'SET_LOADING',
    UNSET_LOADING: 'UNSET_LOADING',
    OPEN_BUCKET: "OPEN_BUCKET",
    CLOSE_BUCKET: "CLOSE_BUCKET",
    SET_GRID_VIEW: "SET_GRID_VIEW",
    SET_LIST_VIEW: "SET_LIST_VIEW",
    CLEAR_SELECTION: "CLEAR_SELECTION",
    SET_SELECTION_ID: "SET_SELECTION_ID",
    SET_PHOTOS_BUCKET_ID: "SET_PHOTOS_BUCKET_ID",
    SET_DASHBOARD_BUCKET_ID: "SET_DASHBOARD_BUCKET_ID",
    SET_SORTING: "SET_SORTING",
    PUSH_LOADING: "PUSH_LOADING",
    POP_LOADING: "POP_LOADING",
    SET_SEARCH: "SET_SEARCH",
    CLEAR_SEARCH: "CLEAR_SEARCH",
    CHANGE_PIN_OPTIONS_STATUS: "CHANGE_PIN_OPTIONS_STATUS",
    CHANGE_PASSWORD_POPUP_STATUS: 'CHANGE_PASSWORD_POPUP_STATUS',
    SET_BUCKETID_TO_COPY: "SET_BUCKETID_TO_COPY",
    CHANGE_SYNC_WINDOW_POPUP_STATUS: "CHANGE_SYNC_WINDOW_POPUP_STATUS"
};

export const FILE_ACTIONS = {
    LIST_FILES: "LIST_FILES",
    UPLOAD_FILE_START: "UPLOAD_FILE_START",
    UPLOAD_FILE_SUCCESS: "UPLOAD_FILE_SUCCESS",
    UPLOAD_FILE_ERROR: "UPLOAD_FILE_ERROR",
    DOWNLOAD_FILE_SUCCESS: "DOWNLOAD_FILE_SUCCESS",
    DOWNLOAD_FILE_ERROR: "DOWNLOAD_FILE_ERROR",
    DELETE_FILE: "DELETE_FILE",
    SELECT_FILE: "SELECT_FILE",
    SELECT_FILES: "SELECT_FILES",
    DESELECT_FILE: "DESELECT_FILE",
    DESELECT_FILES: "DESELECT_FILES",
    UPDATE_FILE_UPLOAD_PROGRESS: "UPDATE_FILE_UPLOAD_PROGRESS",
    UPDATE_FILE_DOWNLOAD_PROGRESS: "UPDATE_FILE_DOWNLOAD_PROGRESS",
    FILE_DOWNLOAD_CANCELED: "FILE_DOWNLOAD_CANCELED",
    FILE_UPLOAD_CANCELED: "FILE_UPLOAD_CANCELED",
    LIST_UPLOADING_FILES: "LIST_UPLOADING_FILES",
    FILE_UPDATE_FAVOURITE: "FILE_UPDATE_FAVOURITE"
};

export const BUCKET_ACTIONS = {
    SELECT_BUCKET: "SELECT_BUCKET", 
    SELECT_BUCKETS: "SELECT_BUCKETS",
    DESELECT_BUCKET: "DESELECT_BUCKET", 
    DESELECT_BUCKETS: "DESELECT_BUCKETS",
    CREATE_BUCKET: "CREATE_BUCKET",
    DELETE_BUCKET: "DELETE_BUCKET",
    GET_BUCKETS: "GET_BUCKETS", 
    UPDATE_FAVOURITE: "UPDATE_FAVOURITE",
};

export const BILLING_CONSTANTS = {
    SET_CREDITS: "SET_CREDITS",
    SET_DEBITS: "SET_DEBITS",
    GET_DEBITS_FAILED: "GET_DEBITS_FAILED",
    CREATE_WALLET: "CREATE_WALLET",
    SET_WALLETS: "SET_WALLETS"
};

export const SETTINGS_ACTIONS = {
    LIST_SETTINGS: "LIST_SETTINGS",
    SYNC_ON: "SYNC_ON",
    SYNC_OFF: "SYNC_OFF",
    SET_WIFI_CONSTRAINT: "SET_WIFI_CONSTRAINT",
    SET_CHARGING_CONSTRAINT: "SET_CHARGING_CONSTRAINT",
    PHOTOS_SYNC: "PHOTOS_SYNC",
    MOVIES_SYNC: "MOVIES_SYNC",
    DOCUMENTS_SYNC: "DOCUMENTS_SYNC",
    MUSIC_SYNC: "MUSIC_SYNC"
};

export const SYNC_QUEUE_ACTIONS = {
    LIST_SYNC_QUEUE: "LIST_SYNC_QUEUE",
    UPDATE_SYNC_QUEUE_ENTRY: "UPDATE_SYNC_QUEUE_ENTRY"
};

export const NOTIFICATIONS_ACTIONS = {
    ADD: 'ADD_NOTIFICATION',
    DELETE: 'DELETE_NOTIFICATION',
    PAUSE: 'PAUSE_NOTIFICATION',
    RESUME: 'RESUME_NOTIFICATION',
}

export default ACTIONS;
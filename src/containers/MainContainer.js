import {Alert, Animated, BackHandler, Easing, Keyboard, Platform} from 'react-native';
import React, {Component} from 'react';
import {bindActionCreators} from 'redux';
import {connect} from 'react-redux';
import {
    changePasswordPopupStatus,
    changePINOptionStatus,
    disableSelectionMode,
    enableSelectionMode,
    hideActionBar,
    hideCreateBucketInput,
    openBucket,
    setGridView,
    setListView,
    setSorting,
    showActionBar,
    showCreateBucketInput,
    toggleSyncWindow
} from '../reducers/mainContainer/mainReducerActions';
import {
    createBucket,
    deleteBucket,
    deselectBuckets,
    getBuckets,
    selectBuckets,
    updateFavourite
} from '../reducers/mainContainer/Buckets/bucketReducerActions';
import {
    deleteFile,
    deselectFiles,
    listFiles,
    selectFiles,
    updateFavouriteFiles
} from '../reducers/mainContainer/Files/filesReducerActions';
import {
    bucketNavigateBack,
    dashboardNavigateBack,
    openSelectBucketScreen,
    redirectToInitializationScreen,
    redirectToMainScreen,
    redirectToPinCodeGenerationScreen
} from '../reducers/navigation/navigationActions';
import {createWallet, getWallets} from '../reducers/billing/billingActions';
import TabBarActionModelFactory from '../models/TabBarActionModel';
import MainComponent from '../components/MainComponent';
import filePicker from '../utils/filePicker';
import observablePropFactory from '../models/ObservableProperty';
import ServiceModule from '../utils/serviceModule';
import SyncModule from '../utils/syncModule';
import StorjModule from '../utils/storjModule';
import CameraModule from '../utils/cameraModule';
import {SYNC_BUCKETS} from '../utils/constants/syncBuckets';
import ListItemModel from '../models/ListItemModel';
import BucketModel from '../models/BucketModel';
import FileModel from '../models/FileModel';
import {listUploadingFiles} from "../reducers/asyncActions/fileActionsAsync";
import {
    listSyncQueueEntriesAsync,
    updateSyncQueueEntryFileNameAsync,
    updateSyncQueueEntryStatusAsync
} from "../reducers/mainContainer/SyncQueue/syncQueueReducerAsyncActions";
import {getAllFromCode} from "../utils/syncQueue/syncStatusMapper";
import SyncQueueEntryComponent from "../components/SynQueue/SyncQueueEntryComponent";
import SyncQueueCallbackObject from "../models/SyncQueueCallbackObject";
import SyncState from '../utils/constants/syncState';
import {getDeviceHeight} from "../utils/adaptive";
import {getFileCopyName} from "../utils/fileUtils";
import {setButtonInvokeTimeout} from '../utils/buttonDelay';


const { PICTURES } = SYNC_BUCKETS;

class MainContainer extends Component {
    constructor(props) {
        super(props);

        this.isLoading = false;

        this.interpolate = this.interpolate.bind(this);
        this.interpolateBackground = this.interpolateBackground.bind(this);
        this.getSyncQueueEntry = this.getSyncQueueEntry.bind(this);
        this.showSyncWindow = this.showSyncWindow.bind(this);
        this.hideSyncWindow = this.hideSyncWindow.bind(this);
        this.getLoadingSyncEntry = this.getLoadingSyncEntry.bind(this);
        this.getSyncEntryLoadingCount = this.getSyncEntryLoadingCount.bind(this);
        this.getBuckets = this.getBuckets.bind(this);
        this.getFiles = this.getFiles.bind(this);
        this.getBucketId = this.getBucketId.bind(this);
        this.createBucket = this.createBucket.bind(this);
        this.onActionBarPress = this.onActionBarPress.bind(this);
        this.selectAll = this.selectAll.bind(this);
        this.deselectAll = this.deselectAll.bind(this);
        this.deletePIN = this.deletePIN.bind(this);

        //Common stuff
        let newAction = TabBarActionModelFactory.createNewAction;
        let uploadFileIcon = require('../images/ActionBar/UploadFileIcon.png');
        this.favIcon = require('../images/ActionBar/FavoritesIcon.png');
        this.unfavIcon = require('../images/ActionBar/UnsetFavourite.png');
        let trashIcon = require('../images/ActionBar/TrashBucketIcon.png');
        let iosUploadPhotoIcon = require('../images/ActionBar/IosUploadPhoto.png');
        let iosUploadFileIcon = require('../images/ActionBar/IosUploadFile.png');
        let openedBucketIdGetter = () => this.props.openedBucketId;
        let dashboardBucketIdGetter = () => this.props.dashboardBucketId;
        let myPhotosBucketIdGetter = () => {
            if(this.props.myPhotosBucketId) return this.props.myPhotosBucketId;

            return this.props.buckets.filter(bucket => bucket.entity.name === "Pictures")[0].entity.id;
        };

        let actionWithDelay = (action) => {
            if(this.isLoading) return;

            this.isLoading = true;
            action();
            setButtonInvokeTimeout(2000, this);
        };

        //Action callbacks
        let createBucketAction = newAction(() => {  actionWithDelay(() => this.props.showCreateBucketInput()) }, require('../images/ActionBar/NewBucketIcon.png'));
        let openFilePickerAction = (type, imgUrl) => newAction(() => { actionWithDelay(() => this.bucketScreenUploadFile(type)); }, imgUrl);
        let openCameraAction = () => newAction(() => { actionWithDelay(() => CameraModule.openCamera(myPhotosBucketIdGetter())); }, require('../images/ActionBar/UploadPhotoIcon.png'));
        let uploadFileAction = (bucketIdGetter, type, imgUrl) => newAction(() => { actionWithDelay(() => this.uploadFile(bucketIdGetter(), type)); }, imgUrl);
        this.setFavouriteAction = newAction(() => { this.setFavourite(); }, this.props.isStarredBucketsSelected ? this.unfavIcon
                                                                                                            : this.favIcon);
        let uploadFileToSelectedBucketsAction = (type, imgUrl) => newAction(() => { actionWithDelay(() => this.uploadFileToSelectedBuckets(type)); }, imgUrl);
        let tryDeleteBucketsAction = newAction(() => { actionWithDelay(() => this.tryDeleteBuckets()); }, trashIcon);
        this.setFavouriteFilesAction = newAction(() => { this.setFavouriteFiles(); }, this.props.isStarredFilesSelected ? this.unfavIcon 
                                                                                                                        : this.favIcon);
        let downloadSelectedFilesAction = newAction(() => { actionWithDelay(() => this.downloadSelectedFiles()); }, require('../images/ActionBar/DownloadIFileIcon.png'));
        let tryCopySelectedFilesAction = newAction(() => { actionWithDelay(() => this.tryCopySelectedFiles()); }, require('../images/ActionBar/CopyBucketIcon.png'));
        let tryDeleteFiles = newAction(() => { actionWithDelay(() => this.tryDeleteFiles()); }, trashIcon);

        //Action arrays
        this.bucketActions = Platform.OS === "android" 
            ? [ createBucketAction, openFilePickerAction(null, uploadFileIcon), openCameraAction() ] 
            : [ createBucketAction, openFilePickerAction("document", iosUploadFileIcon), 
                openFilePickerAction("image", iosUploadPhotoIcon), openCameraAction() ];
    
        this.selectionBucketActions = Platform.OS === "android" 
            ? [ this.setFavouriteAction, uploadFileToSelectedBucketsAction(null, uploadFileIcon), tryDeleteBucketsAction ]
            : [ this.setFavouriteAction, uploadFileToSelectedBucketsAction("document", iosUploadFileIcon), 
                uploadFileToSelectedBucketsAction("image", iosUploadPhotoIcon), tryDeleteBucketsAction ];

        this.selectionFileActions = [
            this.setFavouriteFilesAction,
            downloadSelectedFilesAction, 
            tryCopySelectedFilesAction, 
            tryDeleteFiles
        ];

        this.openedBucketActions = Platform.OS === "android" 
            ? [ uploadFileAction(openedBucketIdGetter, "", uploadFileIcon), openCameraAction() ]
            : [ uploadFileAction(openedBucketIdGetter, "document", iosUploadFileIcon), 
                uploadFileAction(openedBucketIdGetter, "image", iosUploadPhotoIcon), openCameraAction() ];

        this.dashboardBucketActions = Platform.OS === "android" 
            ? [ uploadFileAction(dashboardBucketIdGetter, "", uploadFileIcon), openCameraAction() ]
            : [ uploadFileAction(dashboardBucketIdGetter, "document", iosUploadFileIcon), 
                uploadFileAction(dashboardBucketIdGetter, "image", iosUploadPhotoIcon), openCameraAction() ];

        this.picturesBucketActions = Platform.OS === "android" 
            ? [ uploadFileAction(myPhotosBucketIdGetter, "", uploadFileIcon), openCameraAction() ]
            : [ uploadFileAction(myPhotosBucketIdGetter, "document", iosUploadFileIcon), 
                uploadFileAction(myPhotosBucketIdGetter, "image", iosUploadPhotoIcon), openCameraAction() ];

        this.downloadListener = (fileParams) => {
            let res = observablePropFactory.getObservable(fileParams.fileId);
            res.Property = fileParams;
        };

        this.onHardwareBackPress = this.onHardwareBackPress.bind(this);

        this.filePickerResponsePaths = [];

        this.animValue = new Animated.Value(0);

        let callbackOject = {};
        let reSyncCallback = (entry) => this.props.updateSyncQueueEntryStatusAsync(entry.getId(), SyncState.IDLE);
        let errorCallback = (entry) => {
            if(entry.entity.errorCode === 1013) {
                let fileName = entry.getName();
                fileName = getFileCopyName(fileName);

                return this.props.updateSyncQueueEntryFileNameAsync(entry.getId(), fileName);
            }

            return this.props.updateSyncQueueEntryStatusAsync(entry.getId(), SyncState.IDLE);
        };

        callbackOject.queued = (entry) => ServiceModule.removeFileFromSyncQueue(entry.getId()); 
        callbackOject.error = errorCallback;
        callbackOject.cancelled = reSyncCallback;
        callbackOject.processing = (entry) => StorjModule.cancelUpload(entry.entity.fileHandle);
        callbackOject.processed = reSyncCallback;
        callbackOject.idle = (entry) => this.props.updateSyncQueueEntryStatusAsync(entry.getId(), SyncState.CANCELLED);

        SyncQueueCallbackObject.CallbackObject = callbackOject;

        this.props.listUploadingFiles();
    }

    interpolate() {
        const moveY = this.animValue.interpolate({
            inputRange: [
                0, 
                1
            ],
            outputRange: [-getDeviceHeight(), 10]
        });

        return {
            transform: [
                { translateY: moveY }
            ]
        };
    }

    interpolateBackground() {
        const opacityChange = this.animValue.interpolate({
            inputRange: [
                0, 
                1
            ],
            outputRange: [0, 0.6]
        });

        return {
            opacity: opacityChange
        };
    }

    getProgress(fileHandle) {
        let uploadingFile = this.props.uploadingFiles.find(uploadingFile => uploadingFile.getId() === fileHandle);

        if(uploadingFile) {
            return uploadingFile.progress;
        }

        return 0;
    }

    getSyncEntryLoadingCount() {
        let loadingEntries = this.props.syncQueueEntries.filter(entry => entry.entity.status === SyncState.PROCESSING || entry.entity.status === SyncState.QUEUED);

        return loadingEntries.length;
    }

    getLoadingSyncEntry() {
        let loadingSyncEntry = this.props.syncQueueEntries.find(entry => entry.entity.status === SyncState.PROCESSING);
        if(loadingSyncEntry) {
            return this._getSyncQueueEntry(loadingSyncEntry, false);
        }
    }

    getSyncQueueEntry({item}) {
        return this._getSyncQueueEntry(item, true);
    }

    _getSyncQueueEntry(item, addProps) {
        const describer = getAllFromCode(item.entity.status, new SyncQueueCallbackObject(item));
        
        return(
            <SyncQueueEntryComponent
                error = { describer.error }
                styleContainer = { addProps }
                key = { item.getId() }
                fileName = { item.getName() }
                iconSource = { require("../images/Icons/CloudFile.png") }
                actionIconSource = { describer.actionIcon }
                actionCallback = { describer.action }
                isLoading = { describer.isLoading }
                progress = { this.getProgress(item.entity.fileHandle) }
                status = { describer.status } />
        );
    }

    showSyncWindow() {
        this.props.toggleSyncWindow(true);

        Animated.timing(
            this.animValue,
            {
              toValue: 1,
              duration: 500,
              delay: 0,
              easing: Easing.circle,
              useNativeDriver: true
            }
        ).start();
    }

    hideSyncWindow() {
        Animated.timing(
            this.animValue,
            {
              toValue: 0,
              duration: 500,
              easing: Easing.circle,
              useNativeDriver: true
            }
        ).start(() => this.props.toggleSyncWindow(false));
    }

    async componentWillMount () {
        if(Platform.OS === "android") {
            BackHandler.addEventListener("hardwareBackPress", this.onHardwareBackPress);
        }

        this.keyboardDidShowListener = Keyboard.addListener('keyboardDidShow', () => { this.props.disableSelectionMode(); });
    }

    tryDeleteBuckets() {
        Alert.alert(
            'Delete permanently?',
            'Are you sure to delete selected buckets permanently?',
            [
                { text: 'Cancel', onPress: () => { }, style: 'cancel' },
                { text: 'Delete', onPress: () => { this.deleteBuckets(); } }
            ],
            { cancelable: false }
        );
    }

    tryDeleteFiles(bucketId) {
        Alert.alert(
            'Delete permanently?',
            'Are you sure to delete selected files permanently?',
            [
                { text: 'Cancel', onPress: () => { }, style: 'cancel' },
                { text: 'Delete', onPress: () => { this.deleteSelectedFiles(bucketId); } }
            ],
            { cancelable: false }
        );
    }
    
    deleteFile(bucketId, fileId) {        
        ServiceModule.deleteFile(bucketId, fileId);
    }

    /**
     * Deletes files with isSelected property and disables
     * selecting mode afterwards
     * @param {string} bucketId 
     */
    deleteSelectedFiles(bucketId) {
        this.props.fileListModels.forEach(fileItem => { 

            if(fileItem.isSelected)
                this.deleteFile(fileItem.entity.bucketId, fileItem.getId());
        });

        if(this.props.isSingleItemSelected)
            this.props.disableSelectionMode();
    }
    
    componentWillUnmount () {
        if(Platform.OS === "android") {
            BackHandler.removeEventListener("hardwareBackPress");
        }

        this.keyboardDidShowListener.remove();
    }

    onHardwareBackPress() {
        if(this.props.isLoading)
            return;

        const index = this.props.mainNavReducer.index;
        const routes = this.props.mainNavReducer.routes;   

        if(this.props.isSelectionMode 
        || this.props.isSingleItemSelected 
        || this.props.isActionBarShown) {

            this.props.disableSelectionMode();
        }

        if(routes[index].routeName === "ImageViewerScreen") {
            this.props.redirectToMainScreen();
        }
    }

    onCreateBucketPress() {
        this.props.showCreateBucketInput();
    }

    getCurrentScreen() {
        const index = this.props.mainScreenNavReducer.index;      
        const routes = this.props.mainScreenNavReducer.routes;
        
        return routes[index].routeName;
    }

    onActionBarPress() {
        const currentScreen = this.getCurrentScreen();

        if(currentScreen !== "MyAccountScreen") {
            this.props.isActionBarShown ? 
                this.props.hideActionBar() : this.props.showActionBar();
        }
    }

    async bucketScreenUploadFile(type) {
        let filePickerResponse = await filePicker.show(type);
        this.props.hideActionBar();

        // TODO: change to isSuccess after backend refactoring
        if (!filePickerResponse && !filePickerResponse.response) {
            return;
        }

        filePickerResponse.result.forEach(file => {
            this.filePickerResponsePaths.push(file.path);
        });

        this.props.openSelectBucketScreen(this.getBucketId.bind(this));
    }  

    getBucketId(params) {
        if(params.bucketId) {
            this.filePickerResponsePaths.forEach(element => {
                ServiceModule.uploadFile(params.bucketId, element);
            });  
        }

        this.filePickerResponsePaths = [];
        this.props.disableSelectionMode();
    }

    async uploadFileToSelectedBuckets(type) {
        let filePickerResponse = await filePicker.show(type);
        this.props.hideActionBar();

        if(filePickerResponse.isSuccess) {
            filePickerResponse.result.forEach(file =>{
                this.getSelectedItems(this.props.buckets).forEach(item => {
                    ServiceModule.uploadFile(item.getId(), file.path);
                });
            })
        }
    }

    async uploadFile(bucketId, type) {
        let filePickerResponse = await filePicker.show(type);
        this.props.hideActionBar();

        if(filePickerResponse.isSuccess) {
            filePickerResponse.result.forEach(file => {
                ServiceModule.uploadFile(bucketId, file.path)
            });
        }
    }

    async downloadFile(file, localPath) {    
        ServiceModule.downloadFile(file.entity.bucketId, file.getId(), localPath);
    }

    async downloadSelectedFiles() {
        this.props.fileListModels.forEach(async fileItem => {
            if(fileItem.isSelected) {
                let result = await StorjModule.getDownloadFolderPath();

                this.downloadFile(fileItem, result + "/" + fileItem.getName());
            }
        });
    }

    async createBucket(name) {   
        ServiceModule.createBucket(name);        
    }

    async deleteBucket(bucket) {
        ServiceModule.deleteBucket(bucket.getId());
    }

    async updateBucketsStarred(bucketsArray, starredStatus) {
        let updatedItems = []; 

        for(let i = 0; i < bucketsArray.length; i++) {
            const item = bucketsArray[i];
            let updateStarredResponse = await SyncModule.updateBucketStarred(item.getId(), starredStatus);

            if(updateStarredResponse.isSuccess) {
                updatedItems.push(item);
            }    
        }

        this.props.updateFavourite(updatedItems, starredStatus);  
    }

    areFavoritesExists(array) {
        if(!array) return false;

        return array.filter(item => {return item.entity.isStarred === true}).length !== 0;
    } 

    setFavourite() {
        let selectedBuckets = this.getSelectedItems(this.props.buckets);

        if(this.areFavoritesExists(selectedBuckets)) {
            this.updateBucketsStarred(selectedBuckets, false);
            return;
        }

        this.updateBucketsStarred(selectedBuckets, true);     
    }

    async updateFilesStarred(filesArray, starredStatus) {
        let updatedItems = [];        

        for(let i = 0; i < filesArray.length; i++) {
            const item = filesArray[i];
            let updateStarredResponse = await SyncModule.updateFileStarred(item.getId(), starredStatus);

            if(updateStarredResponse.isSuccess) {
                updatedItems.push(item);
            }    
        }

        this.props.updateFavouriteFiles(updatedItems, starredStatus);
    }

    getSelectedItems(array) {
        if(!array) return 0;

        return array.filter(item => item.isSelected === true);
    }

    setFavouriteFiles() {        
        let selectedFiles = this.getSelectedItems(this.props.fileListModels);

        if(this.areFavoritesExists(selectedFiles)) {
            this.updateFilesStarred(selectedFiles, false);
            return;
        }
        
        this.updateFilesStarred(selectedFiles, true);  
    }

    deleteBuckets() {
        this.getSelectedItems(this.props.buckets).forEach(item => {
            if(item.getName() === PICTURES) return; //TODO: we shoul add some notification here

            this.deleteBucket(item);
        });

        if(this.props.isSingleItemSelected)
            this.props.disableSelectionMode();
    }

    static navigationOptions = {
        header: null
    };

    tryCopySelectedFiles() {
        this.props.openSelectBucketScreen(this.copySelectedFiles.bind(this));
    }

    copySelectedFiles(params) {
        let bucketId = params.bucketId;
        
        if(bucketId) {
            let selectedFiles = this.props.fileListModels.filter(fileItem => fileItem.isSelected);

            selectedFiles.forEach(async fileItem => {
                if(fileItem.entity.isDownloaded) {
                    ServiceModule.uploadFile(bucketId, fileItem.entity.localPath);
                } else {
                    let result = await StorjModule.getDownloadFolderPath();
                    ServiceModule.copyFile(fileItem.entity.bucketId, fileItem.getId(), result + "/" + fileItem.getName(), bucketId);
                }
            });
        }

        this.props.disableSelectionMode();
    }

    getFilteredFiles(bucketId, searchSequence) {
        let resultArray = [];
        let length = this.props.fileListModels.length;

        if(!searchSequence) {
            for(let i = 0; i < length; i++) {
                let file = this.props.fileListModels[i];

                if(file.entity.bucketId === bucketId) resultArray.push(file);
            }

            return resultArray;
        }

        for(let i = 0; i < length; i++) {
            let file = this.props.fileListModels[i];

            if(file.entity.bucketId === bucketId && file.entity.name.toLowerCase().includes(searchSequence.toLowerCase())) 
                resultArray.push(file);
        }

        return resultArray;
    } 

    getBucketsForSelection() {       
        if(!this.props.searchSequence) {
            return this.props.buckets;
        }

        let resultArray = [];
        let length = this.props.buckets.length;

        for(let i = 0; i < length; i++) {
            let bucket = this.props.buckets[i];
            if(bucket.entity.name.toLowerCase().includes(this.props.searchSequence.toLowerCase())) 
                resultArray.push(bucket);
        }
        

        return resultArray; 
    }

    selectAll(bucketId) {
        if(bucketId) {
            this.props.selectFiles( this.getFilteredFiles(bucketId, this.props.searchSequence) );
            return;
        }

        this.props.selectBuckets(this.getBucketsForSelection());
    }

    deselectAll() {
        this.props.disableSelectionMode();
        this.props.deselectFiles();
        this.props.deselectBuckets();
    }

    getTapBarActions() {  
        const isSelectionMode = this.props.isSelectionMode || this.props.isSingleItemSelected;
        const index = this.props.mainScreenNavReducer.index;      
        const routes = this.props.mainScreenNavReducer.routes;
        const currentScreen = routes[index].routeName;

        const dashboardIndex = this.props.dashboardNavReducer.index;
        const dashboardRoutes = this.props.dashboardNavReducer.routes;
        const currentDashboardScreen = dashboardRoutes[dashboardIndex].routeName;

        switch(currentScreen) {
            case "DashboardScreen":
                const dashboardActions = handleDashboardScreenActions(
                    this.props.dashboardBucketId, 
                    isSelectionMode, 
                    this.dashboardBucketActions, 
                    this.selectionFileActions,
                    this.selectionBucketActions,
                    currentDashboardScreen);

                if(dashboardActions) return dashboardActions;

                break;
            case "BucketsScreen":
                const actions = handleScreenActions(
                    this.props.openedBucketId, 
                    isSelectionMode, 
                    this.openedBucketActions, 
                    this.selectionFileActions);

                if(actions) return actions;

                break;
            case "MyPhotosScreen":
                const picturesActions = handleScreenActions(
                    this.props.myPhotosBucketId, 
                    isSelectionMode, 
                    this.picturesBucketActions, 
                    this.selectionFileActions);

                if(picturesActions) return picturesActions;

                break;
        }

        if(this.props.isSelectionMode || this.props.isSingleItemSelected) return this.selectionBucketActions;

        return this.bucketActions;
    };

    async getBuckets(sortingMode) {
		let bucketsResponse = await SyncModule.listBuckets(sortingMode);

        if(bucketsResponse.isSuccess) {
            let buckets = bucketsResponse.result.map((file) => {
                return new ListItemModel(new BucketModel(file));
            });                    

			ServiceModule.createBaseBuckets(buckets);

            this.props.getBuckets(buckets);
        }
    }
    
    async getFiles(sortingMode) {

        let bucketId = null;

        switch(this.getCurrentScreen()) {
            case "BucketsScreen":
                bucketId = this.props.openedBucketId;
                break;
            case "MyPhotosScreen":
                bucketId = this.props.myPhotosBucketId;
                break;
            case "DashboardScreen":
                bucketId = this.props.dashboardBucketId;
                break;
        }

        let filesResponse = await SyncModule.listFiles(bucketId, sortingMode);		

        if(filesResponse.isSuccess) {
            let files = filesResponse.result.map((file) => {
                return new ListItemModel(new FileModel(file));
            });                    
            this.props.listFiles(bucketId, files);
        }
    }

    async deletePIN() {
        await StorjModule.importKeys(
            this.props.email,
            this.props.password,
            this.props.mnemonic,
            '');
    }

    render() {
        const index = this.props.bucketsScreenNavReducer.index;      
        const routes = this.props.bucketsScreenNavReducer.routes;
        
        this.setFavouriteAction.changeIcon(this.props.isStarredBucketsSelected ? this.unfavIcon : this.favIcon);
        this.setFavouriteFilesAction.changeIcon(this.props.isStarredFilesSelected ? this.unfavIcon : this.favIcon);

        return(
            <MainComponent
                interpolate = { this.interpolate }
                interpolateBackground = { this.interpolateBackground }
                syncQueueEntries = { this.props.syncQueueEntries }
                renderSyncQueueEntry = { this.getSyncQueueEntry }
                showSyncWindow = { this.showSyncWindow }
                hideSyncWindow = { this.hideSyncWindow }
                isSyncWindowShown = { this.props.isSyncWindowShown }
                getLoadingSyncEntry = { this.getLoadingSyncEntry }
                getSyncEntryLoadingCount = { this.getSyncEntryLoadingCount }

                getBuckets = { this.getBuckets }
                getFiles = { this.getFiles }
                getBucketId = { this.getBucketId }
                redirectToInitializationScreen = { this.props.redirectToInitializationScreen.bind(this) }
                redirectToPinCodeGenerationScreen = { this.props.redirectToPinCodeGenerationScreen }
                isGridViewShown = { this.props.isGridViewShown }
                setGridView = { this.props.setGridView }
                setListView = { this.props.setListView }
                bucketScreenRouteName = { routes[index].routeName }
                createBucket = { this.createBucket }
                hideCreateBucketInput = { this.props.hideCreateBucketInput }
                tapBarActions = { this.getTapBarActions() } 
                enableSelectionMode = { this.props.enableSelectionMode }
                disableSelectionMode = { this.props.disableSelectionMode }
                isSelectionMode = { this.props.isSelectionMode }
                isSingleItemSelected = { this.props.isSingleItemSelected }
                onActionBarPress = { this.onActionBarPress }
                isActionBarShown = { this.props.isActionBarShown } 
                isCreateBucketInputShown = { this.props.isCreateBucketInputShown }
                wallets = { this.props.wallets }
                isLoading = { this.props.isLoading }
                createWallet = { this.props.createWallet } 
                getWallets = { this.props.getWallets }
                buckets = { this.props.buckets }
                openBucket = { this.props.openBucket }
                bucketNavigateBack = { this.props.bucketNavigateBack }
                setSorting = { this.props.setSorting }
                dashboardNavigateBack = { this.props.dashboardNavigateBack }
                email = { this.props.email }
                password = { this.props.password }
                mnemonic = { this.props.mnemonic }
                selectAll = { this.selectAll }
                deselectAll = { this.deselectAll }
                changePINOptionStatus = { this.props.changePINOptionStatus }
                changePasswordPopupStatus = { this.props.changePasswordPopupStatus }
                isPinOptionsShown = { this.props.isPinOptionsShown }
                isChangePasswordPopupShown = { this.props.isChangePasswordPopupShown }
                deletePIN = { this.deletePIN }
                searchSequence = { this.props.searchSequence } />
        );
    }
}

function handleScreenActions(bucketId, isSelection, actions, selectionModeActions) {
    if(!bucketId) {
        return null;
    }

    let result = null;

    if(isSelection) {
        result = selectionModeActions;
    } else {
        result = actions;
    }

    return result;
}

function handleDashboardScreenActions(bucketId, isSelection, actions, selectionModeActions, selectionModeBucketActions, currentScreen) {
    let result = null;

    if(isSelection) {
        result = currentScreen === "FavoriteBucketsScreen" ? selectionModeBucketActions : selectionModeActions;
    } else if(bucketId) {
        result = actions;
    }

    return result;
}

function mapStateToProps(state) { 

    function getSearchSequenceIndex(mainNavigationState, bucketsScreenNavState, dashboardScreenNavState) {
        if(mainNavigationState.index === 2) return 0;
        if(mainNavigationState.index === 1 && bucketsScreenNavState.index === 0) return 1;
        if(mainNavigationState.index === 1 && bucketsScreenNavState.index === 1) return 2;
        if(mainNavigationState.index === 0 && dashboardScreenNavState.index === 0) return 3;
        if(mainNavigationState.index === 0 && dashboardScreenNavState.index === 1) return 4;
    }

    let isStarredBucketsSelected = state.bucketReducer.buckets.filter(item => item.isSelected === true).filter(item => item.entity.isStarred === true).length !== 0; 
    let isStarredFilesSelected = state.filesReducer.fileListModels.filter(item => item.isSelected === true).filter(item => item.entity.isStarred === true).length !== 0;
    let sequenceIndex = getSearchSequenceIndex(state.mainScreenNavReducer, state.bucketsScreenNavReducer, state.dashboardScreenNavReducer);
    let sequnceList = [ 
        state.mainReducer.myPhotosSearchSubSequence,
        state.mainReducer.bucketSearchSubSequence,
        state.mainReducer.filesSearchSubSequence,
        state.mainReducer.starredSearchSubSequence,
        state.mainReducer.dashboardFilesSearchSubSequence
    ];

    return {
        searchSequence: sequnceList[sequenceIndex],
        syncQueueEntries: state.syncQueueReducer.syncQueueEntries,
        uploadingFiles: state.filesReducer.uploadingFileListModels,
        isSyncWindowShown: state.mainReducer.isSyncWindowShown,

        email: state.authReducer.user.email,
        password: state.authReducer.user.password,
        mnemonic: state.authReducer.user.mnemonic,
        dashboardNavReducer: state.dashboardScreenNavReducer,
        bucketsScreenNavReducer: state.bucketsScreenNavReducer,
        mainNavReducer: state.navReducer,
        mainScreenNavReducer: state.mainScreenNavReducer,
        fileListModels: state.filesReducer.fileListModels,
        openedBucketId: state.mainReducer.openedBucketId,
        dashboardBucketId: state.mainReducer.dashboardBucketId,
        myPhotosBucketId: state.mainReducer.myPhotosBucketId,
        isSelectionMode: state.mainReducer.isSelectionMode, 
        isSingleItemSelected: state.mainReducer.isSingleItemSelected,
        isActionBarShown: state.mainReducer.isActionBarShown,
        buckets: state.bucketReducer.buckets,
        isCreateBucketInputShown: state.mainReducer.isCreateBucketInputShown,
        isFirstSignIn: state.mainReducer.isFirstSignIn,
        isLoading: state.mainReducer.isLoading,
        isGridViewShown: state.mainReducer.isGridViewShown,
        wallets: state.billingReducer.wallets,
        isStarredBucketsSelected,
        isStarredFilesSelected,
        isPinOptionsShown: state.mainReducer.isPinOptionsShown,
        isChangePasswordPopupShown: state.mainReducer.isChangePasswordPopupShown
    };
}
function mapDispatchToProps(dispatch) { 
    return {
        ...bindActionCreators({
            listSyncQueueEntriesAsync,
            updateSyncQueueEntryFileNameAsync,
            updateSyncQueueEntryStatusAsync,
            toggleSyncWindow,
            listUploadingFiles,

            redirectToMainScreen, 
            redirectToInitializationScreen,
            bucketNavigateBack, 
            dashboardNavigateBack,
            redirectToPinCodeGenerationScreen,
            openSelectBucketScreen,
            showActionBar,
            hideActionBar,
            showCreateBucketInput,
            hideCreateBucketInput,
            setGridView,
            setListView,
            openBucket,
            selectBuckets,
            selectFiles,
            deselectBuckets,
            deselectFiles,
            listFiles,
            setSorting,
            enableSelectionMode,
            disableSelectionMode,
            createBucket,
            deleteBucket, 
            deleteFile, 
            updateFavourite, 
            updateFavouriteFiles,
            createWallet,
            getWallets,
            getBuckets,
            changePINOptionStatus,
            changePasswordPopupStatus
        }, dispatch)    
    };    
}

export default connect(mapStateToProps, mapDispatchToProps)(MainContainer);
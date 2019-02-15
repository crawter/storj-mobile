import React, { Component } from 'react';
import {
	BackHandler,
	Platform,
	View,
	DeviceEventEmitter,
	NativeEventEmitter,
	StyleSheet,
	NetInfo
} from 'react-native';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { addNavigationHelpers } from 'react-navigation';
import StackNavigator from '../navigators/StackNavigator';
import eventNames from '../utils/constants/eventNames';
import {
	setLoading,
	unsetLoading,
	popLoading,
} from '../reducers/mainContainer/mainReducerActions';
import { saveMnemonic } from '../reducers/authentification/authActions';
import {
	createBucket,
	getBuckets,
    deleteBucket
} from '../reducers/mainContainer/Buckets/bucketReducerActions';
import {
    uploadFileError,
    updateFileUploadProgress,
    downloadFileSuccess,
    downloadFileError,
    updateFileDownloadProgress,
	deleteFile,
	listFiles
} from '../reducers/mainContainer/Files/filesReducerActions'
import {
	redirectToLoginScreen,
    redirectToMainScreen,
    redirectToMnemonicConfirmationScreen,
    redirectToMnemonicConfirmedScreen,
    redirectToMnemonicGenerationScreen,
    redirectToMnemonicInfoScreen,
    redirectToMnemonicNotConfirmedScreen,
    redirectToRegisterScreen,
	redirectToRegisterSuccessScreen,
	redirectToMnemonicHelpScreen,
	navigateBack
} from '../reducers/navigation/navigationActions';
import ListItemModel from '../models/ListItemModel';
import BucketModel from '../models/BucketModel';
import FileModel from '../models/FileModel';
import WarningComponent from '../components/Common/WarningComponent';
import { uploadFileStart, uploadFileSuccess } from '../reducers/asyncActions/fileActionsAsync';
import { listSyncQueueEntriesAsync, getSyncQueueEntryAsync } from "../reducers/mainContainer/SyncQueue/syncQueueReducerAsyncActions";
import { listSettingsAsync } from "../reducers/mainContainer/MyAccount/Settings/SettingsActionsAsync";
import { addErrorNotification, deleteNotification } from '../reducers/notification/notificationActions';

import SyncModule from '../utils/syncModule';
import ServiceModule from '../utils/serviceModule';

import { statusBarHeightIos } from "../utils/adaptive";

/**
 * Component that contains main navigator
 */

class Apps extends Component {

	constructor(props) {
        super(props);

		this.onHardwareBackPress = this.onHardwareBackPress.bind(this);
		this.getbucketsListener = null;
		this.bucketCreatedListener = null;
		this.bucketDeletedListener = null;
		this.fileDeletedListener = null;
		this.getFilesListener = null;
		this.downloadFileStartListener = null;
		this.downloadFileProgressListener = null;
		this.downloadFileSuccessListener = null;
		this.downloadFileErrorListener = null;
		this.syncQueueEntryUpdateListener = null;
		this.syncStartedListener = null;

		this.isAndroid = Platform.OS === "android";
		this.timer = null;
    }

	async componentWillMount() {
		let eventEmitter = this.isAndroid ? DeviceEventEmitter : new NativeEventEmitter(ServiceModule.getServiceNativeModule());
        
		this.getbucketsListener = eventEmitter.addListener(eventNames.EVENT_BUCKETS_UPDATED, this.onBucketsReceived.bind(this));
		this.bucketCreatedListener = eventEmitter.addListener(eventNames.EVENT_BUCKET_CREATED, this.onBucketCreated.bind(this));
		this.bucketDeletedListener = eventEmitter.addListener(eventNames.EVENT_BUCKET_DELETED, this.onBucketDeleted.bind(this));
		
		this.fileDeletedListener = eventEmitter.addListener(eventNames.EVENT_FILE_DELETED, this.onFileDeleted.bind(this));
		this.getFilesListener = eventEmitter.addListener(eventNames.EVENT_FILES_UPDATED, this.onFilesReceived.bind(this));
		
		this.fileUploadStartedListener = eventEmitter.addListener(eventNames.EVENT_FILE_UPLOAD_START, this.onFileUploadStart.bind(this));
		this.fileUploadProgressListener = eventEmitter.addListener(eventNames.EVENT_FILE_UPLOADED_PROGRESS, this.fileUploadProgress.bind(this));
		this.fileUploadSuccessListener = eventEmitter.addListener(eventNames.EVENT_FILE_UPLOADED_SUCCESSFULLY, this.fileUploadSuccess.bind(this));
		this.fileUploadErrorListener = eventEmitter.addListener(eventNames.EVENT_FILE_UPLOAD_ERROR, this.fileUploadError.bind(this));

		this.downloadFileStartListener = eventEmitter.addListener(eventNames.EVENT_FILE_DOWNLOAD_START, this.onFileDownloadStart.bind(this));
		this.downloadFileProgressListener = eventEmitter.addListener(eventNames.EVENT_FILE_DOWNLOAD_PROGRESS, this.onFileDownloadProgress.bind(this));
		this.downloadFileSuccessListener = eventEmitter.addListener(eventNames.EVENT_FILE_DOWNLOAD_SUCCESS, this.onFileDownloadSuccess.bind(this));
		this.downloadFileErrorListener = eventEmitter.addListener(eventNames.EVENT_FILE_DOWNLOAD_ERROR, this.onFileDownloadError.bind(this));

		this.syncQueueEntryUpdateListener = eventEmitter.addListener(eventNames.EVENT_SYNC_ENTRY_UPDATED, this.onSyncQueueEntryUpdated.bind(this));
		this.syncStartedListener = eventEmitter.addListener(eventNames.EVENT_SYNC_STARTED, this.onSyncStarted.bind(this));

		NetInfo.isConnected.fetch().then(isConnected => {
			if (isConnected) return;

			this.props.addErrorNotification('No internet connection', this.props.deleteNotification);
		});

		NetInfo.isConnected.addEventListener('connectionChange', this.onConnectionChange.bind(this));

		if(this.isAndroid) {
			await ServiceModule.bindGetBucketsService();
			await ServiceModule.bindDownloadService();
			await ServiceModule.bindUploadService();
		}

		//ServiceModule.startSync();
	}
	
	onConnectionChange(isConnected) {
		if(!isConnected) {
			this.props.addErrorNotification('No internet connection', this.props.deleteNotification);

			return;
		}
	}

	async fileUploadError(params) {
		params = JSON.parse(params);
		this.props.uploadFileError(params.fileHandle);
	}

	async fileUploadSuccess(params) {
		params = JSON.parse(params);
		this.props.uploadSuccess(params.fileHandle, params.fileId);
	}

	async fileUploadProgress(params) {		
		params = JSON.parse(params);
		this.props.updateFileUploadProgress(params.fileHandle, params.progress, params.uploaded);
	}

	async onFileUploadStart(params) {
        params = JSON.parse(params);
		this.props.getUploadingFile(params.fileHandle);
	}

	componentDidMount() {
		if(Platform.OS === 'android') {
			BackHandler.addEventListener("hardwareBackPress", this.onHardwareBackPress);
		}
	}

	onFileDownloadStart(params) {
		params = JSON.parse(params);		
		this.props.updateFileDownloadProgress(null, params.fileId, params.progress, params.fileHandle);
	}

	onFileDownloadProgress(params) {
		params = JSON.parse(params);		
		this.props.updateFileDownloadProgress(null, params.fileId, params.progress, params.fileHandle);
	}

	onFileDownloadSuccess(params) {		
		params = JSON.parse(params);
		this.props.downloadFileSuccess(null, params.fileId, params.localPath, params.thumbnail);
	}

	onFileDownloadError(params) {		
		params = JSON.parse(params);		
		this.props.downloadFileError(null, params.fileId);
	}

	onSyncStarted() {
		this.props.listSettings(this.props.email);
		this.props.listSyncQueueEntriesAsync();
	}

	onSyncQueueEntryUpdated(params) { //TODO: name error
		this.props.getSyncQueueEntryAsync(params.syncEntryId);
	}

	componentWillUnmount() {
		if(Platform.OS === 'android') {
			BackHandler.removeEventListener("hardwareBackPress", this.onHardwareBackPress);
		}
		
		this.getbucketsListener.remove();
		this.bucketCreatedListener.remove();
		this.bucketDeletedListener.remove();
		this.fileDeletedListener.remove();
		this.getFilesListener.remove();
		this.downloadFileStartListener.remove();
		this.downloadFileProgressListener.remove();
		this.downloadFileSuccessListener.remove();
		this.downloadFileErrorListener.remove();
		this.syncQueueEntryUpdateListener.remove();
		this.syncStartedListener.remove();
		this.fileUploadStartedListener.remove();
		this.fileUploadProgressListener.remove();
		this.fileUploadSuccessListener.remove();
		this.fileUploadErrorListener.remove();
	}

	onHardwareBackPress() {
		if (this.props.nav.index === 0) {
			return true;
		}

		//this.props.dispatch(NavigationActions.back());
		return true;
	}

	async onFilesReceived(response) {
		response = JSON.parse(response);

		if(!response.isSuccess) {
			this.props.popLoading("files");
			return;
		}

		let filesResponse = await SyncModule.listFiles(response.result, this.props.sortingMode);
			if(filesResponse.isSuccess) {
				let files = filesResponse.result.map((file) => {
					return new ListItemModel(new FileModel(file));
				});                    
				this.props.listFiles(response.result, files);
			}
        
		this.props.popLoading("files");
	}

	onBucketCreated(response) {
		response = JSON.parse(response);

		if(response.isSuccess) {
			this.props.createBucket(new ListItemModel(new BucketModel(response.result)));	
		} else {
			switch(response.error.code) {
				case 409:
					this.props.addErrorNotification(response.error.message, this.props.deleteNotification);
					break;
				case 10006:
					this.props.addErrorNotification(response.error.message, this.props.deleteNotification);
					break;
				default:
					this.props.addErrorNotification(response.error.message, this.props.deleteNotification);
					break;
			}
		}
	}

	async onBucketsReceived() {
        this.props.setLoading();
		let bucketsResponse = await SyncModule.listBuckets(this.props.sortingMode);

        if(bucketsResponse.isSuccess) {
            let buckets = bucketsResponse.result.map((file) => {
                return new ListItemModel(new BucketModel(file));
            });                    

			ServiceModule.createBaseBuckets(buckets);

            this.props.getBuckets(buckets);
        }
		
        this.props.unsetLoading();
	}

	onBucketDeleted(response) {
		response = JSON.parse(response);

		if(response.isSuccess) {
			this.props.deleteBucket(response.result);
		}
	}
	
	onFileDeleted(response) {		
		response = JSON.parse(response);

		if(response.isSuccess) {
			let result = response.result;
			this.props.deleteFile(result.bucketId, result.fileId);
		}
	}

	render() {
		return (
			<View style = { styles.mainContainer }>
				<StackNavigator 
					screenProps = {{
						saveMnemonic: this.props.saveMnemonic,
						redirectToLoginScreen: this.props.redirectToLoginScreen,
						redirectToMainScreen: this.props.redirectToMainScreen,
						redirectToMnemonicConfirmationScreen: this.props.redirectToMnemonicConfirmationScreen,
						redirectToMnemonicConfirmedScreen: this.props.redirectToMnemonicConfirmedScreen,
						redirectToMnemonicGenerationScreen: this.props.redirectToMnemonicGenerationScreen,
						redirectToMnemonicInfoScreen: this.props.redirectToMnemonicInfoScreen,
						redirectToMnemonicNotConfirmedScreen: this.props.redirectToMnemonicNotConfirmedScreen,
						redirectToRegisterSuccessScreen: this.props.redirectToRegisterSuccessScreen,
						redirectToRegisterScreen: this.props.redirectToRegisterScreen,
						redirectToMnemonicHelpScreen: this.props.redirectToMnemonicHelpScreen,
						navigateBack : this.props.navigateBack
					}}
					navigation = { addNavigationHelpers({
						dispatch: this.props.dispatch,
						state: this.props.nav					
					})}
				/>
				<WarningComponent
					notification = { this.props.notification } />
			</View>
		);
	};
}

const styles = StyleSheet.create({
	mainContainer: {
		flex: 1,
		backgroundColor: 'white',
		paddingTop: Platform.select({
			ios: statusBarHeightIos,
			android: 0
		})
	}
});

/**
 * connecting navigation reducer to component props
 */
function mapStateToProps(state) {
    return {
		nav: state.navReducer,
		sortingMode: state.mainReducer.sortingMode,
		email: state.authReducer.user.email,
		notification: state.notificationReducer.notificationQueue[0],
    };
}

function mapDispatchToProps(dispatch) {
	return {
		...bindActionCreators( {
		saveMnemonic,
		setLoading,
		unsetLoading,
		createBucket,
		getBuckets,
		popLoading,
		deleteBucket,
		uploadFileError,
		updateFileUploadProgress,
		downloadFileSuccess,
		downloadFileError,
		updateFileDownloadProgress,
		deleteFile,
		listFiles,
		redirectToLoginScreen,
		redirectToMainScreen,
		redirectToMnemonicConfirmationScreen,
		redirectToMnemonicConfirmedScreen,
		redirectToMnemonicGenerationScreen,
		redirectToMnemonicInfoScreen,
		redirectToMnemonicHelpScreen,
		redirectToMnemonicNotConfirmedScreen,
		redirectToRegisterScreen,
		redirectToRegisterSuccessScreen,
		navigateBack,
		listSyncQueueEntriesAsync,
		addErrorNotification,
		deleteNotification,
		getSyncQueueEntryAsync  }, dispatch),
		listSettings: (settingsId) => dispatch(listSettingsAsync(settingsId)),
		uploadSuccess: (fileHandle, fileId) => dispatch(uploadFileSuccess(fileHandle, fileId)),		
		getUploadingFile: (fileHandle) => dispatch(uploadFileStart(fileHandle))};
}

/**
 * Creating navigator container
 */
export const AppWithNavigationState = connect(mapStateToProps, mapDispatchToProps)(Apps);
 
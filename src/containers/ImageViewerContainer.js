import {} from 'react-native';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { redirectToMainScreen } from '../reducers/navigation/navigationActions';
import { imageViewerActions } from '../reducers/mainContainer/Files/filesReducerActions';
import StorjModule from '../utils/StorjModule';
import SyncModule from "../utils/SyncModule";
import TabBarActionModelFactory from '../models/TabBarActionModel';
import ImageViewComponent from "../components/ImageViewerComponent";
import ListItemModel from '../models/ListItemModel';
import FileModel from "../models/FileModel";

class ImageViewerContainer extends Component {
    constructor(props) {
        super(props);

        this.state = {
            showActionBar: false
        };

        this.localPath = "file://" + props.navigation.state.params.localPath;
        this.fileId = props.navigation.state.params.fileId;
        this.bucketId = props.navigation.state.params.bucketId;
        this.isStarred = props.navigation.state.params.isStarred;

        this.toggleActionBar = this.toggleActionBar.bind(this);

        this.actionBarActions = actionBarActions = [
            TabBarActionModelFactory.createNewAction(() => { this.setFavourite(); }, 'Action 1', require('../images/ActionBar/FavoritesIcon.png')),
            TabBarActionModelFactory.createNewAction(() => { console.log('Action 3') }, 'Action 3', require('../images/ActionBar/CopyBucketIcon.png')),
            TabBarActionModelFactory.createNewAction(() => { this.deleteImage(); }, 'Action 4', require('../images/ActionBar/TrashBucketIcon.png'))
        ];
    }

    async componentWillMount() {
        let checkImageResponse = await SyncModule.checkImage(this.fileId, this.props.navigation.state.params.localPath);
        
        if(!checkImageResponse.isSuccess) {
            this.props.downloadFileError(this.bucketId, this.fileId);
            this.props.redirectToMainScreen();
        }
    }

    toggleActionBar() {
        this.setState({
            showActionBar: !this.state.showActionBar
        });
    }

    async setFavourite() {
        let updateStarredResponse = await SyncModule.updateFileStarred(this.fileId, !this.isStarred);
        
        if(updateStarredResponse.isSuccess) {
            this.props.updateFavouriteFiles([new ListItemModel(new FileModel({ fileId: this.fileId }))]);
        }    
    }

    async deleteImage() {
        let deleteFileResponse = await StorjModule.deleteFile(this.bucketId, this.fileId);

        if(deleteFileResponse.isSuccess) {
            this.props.deleteFile(this.bucketId, this.fileId);
            this.props.redirectToMainScreen();
        }
    }

    static navigationOptions = { header: null };

    render() {
        return(
            <ImageViewComponent
                onBackPress = { this.props.redirectToMainScreen }
                imageUri = { { uri: this.localPath } }
                showActionBar = { this.state.showActionBar }
                onOptionsPress = { this.toggleActionBar }
                actionBarActions = { this.actionBarActions } />
        );   
    }
}

function mapStateToProps(state) {
    return {};
}

function mapDispatchToProps(dispatch) {
    return bindActionCreators({ redirectToMainScreen, ...imageViewerActions }, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(ImageViewerContainer);
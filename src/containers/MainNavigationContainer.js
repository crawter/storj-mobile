import React, { Component } from 'react';
import { NavigationActions, addNavigationHelpers } from 'react-navigation';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import MainScreenTabNav from '../navigators/MainScreenNavigator';
import PropTypes from 'prop-types';
import { 
    setPhotosBucketId,
    hideActionBar,
    pushLoading
} from '../reducers/mainContainer/mainReducerActions';
import { listSettingsAsync } from "../reducers/mainContainer/MyAccount/Settings/SettingsActionsAsync";

/**
* Container for main screen navigation
*/
class MainNavigationContainer extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        return(
            <MainScreenTabNav
                screenProps = {{                                    
                    showOptions: this.props.showOptions, 
                    showQR: this.props.showQR,
                    showStorageInfo: this.props.showStorageInfo,
                    showCredits: this.props.showCredits,
                    redirectToInitializationScreen: this.props.redirectToInitializationScreen,
                    isFirstSignIn: this.props.isFirstSignIn,
                    selectAll: this.props.selectAll,
                    deselectAll: this.props.deselectAll,
                    showSyncWindow: this.props.showSyncWindow
                }}
                navigation = { addNavigationHelpers({
                    email: this.props.email,
                    listSettings: this.props.listSettings,
                    pushLoading: this.props.pushLoading,
                    isActionBarShown: this.props.isActionBarShown,
                    isSelectionMode: this.props.isSelectionMode,
                    isSingleItemSelected: this.props.isSingleItemSelected,
                    dispatch: this.props.dispatch,
                    state: this.props.nav,
                    goToBucketsScreen: this.props.goToBucketsScreen,
                    onActionBarPress: this.props.onActionBarPress,
                    hideActionBar: this.props.hideActionBar,
                    currentRouteIndex: this.props.nav.index,
                    buckets: this.props.buckets,
                    openBucket: this.props.openBucket, 
                    bucketNavigateBack: this.props.bucketNavigateBack,
                    dashboardNavigateBack: this.props.dashboardNavigateBack,
                    setPhotosBucketId: this.props.setPhotosBucketId 
                })} />
        );
    };
}

function mapStateToProps(state) {
    return {
        email: state.authReducer.user.email,
        nav: state.mainScreenNavReducer,
        isSelectionMode: state.mainReducer.isSelectionMode,
        isFirstSignIn: state.mainReducer.isFirstSignIn,
    };
};

function mapDispatchToProps(dispatch) {
    return {
        ...bindActionCreators({ 
            setPhotosBucketId,
            hideActionBar,
            pushLoading 
        }, dispatch),
        goToBucketsScreen: () => { dispatch(NavigationActions.navigate({ routeName: 'BucketsScreen'})); },
		listSettings: (settingsId) => dispatch(listSettingsAsync(settingsId)),
        dispatch
    };
};

const TabNavigatorWithRedux = connect(mapStateToProps, mapDispatchToProps)(MainNavigationContainer);

export default TabNavigatorWithRedux; 

MainNavigationContainer.propTypes = {
    bucketNavigateBack: PropTypes.func,
    buckets: PropTypes.array,
    dashboardNavigateBack: PropTypes.func,
    dispatch: PropTypes.func,
    goToBucketsScreen: PropTypes.func,
    isActionBarShown: PropTypes.bool,
    isSelectionMode: PropTypes.bool,
    isSingleItemSelected: PropTypes.bool,
    nav: PropTypes.object,
    onActionBarPress: PropTypes.func,
    openBucket: PropTypes.func,
    redirectToInitializationScreen: PropTypes.func,
    showCredits: PropTypes.func,
    showOptions: PropTypes.func,
    showQR: PropTypes.func,
    showStorageInfo: PropTypes.func,
    testAction: PropTypes.func
};
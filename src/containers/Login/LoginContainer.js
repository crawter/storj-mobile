import React, { Component } from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';
import { Linking } from 'react-native';
import {
    loginSuccess,
    loginError,
    login,
    navigateToRegisterScreen,
    redirectToAuthFailureScreen,
    redirectToMainScreen,
    redirectToInitializeScreen,
    redirectToQRScannerScreen
} from '../../reducers/authentification/authActions';
import { addErrorNotification, deleteNotification } from '../../reducers/notification/notificationActions';
import StorjLib from '../../utils/storjModule';
import SyncModule from "../../utils/syncModule";
import LoginComponent from '../../components/Login/LoginComponent';
import validator from '../../utils/validator';
import { LoginStateModel } from '../../models/LoginStateModel';
import { LoginErrorModel } from '../../models/LoginErrorModel';
import infoScreensConstants from '../../utils/constants/infoScreensConstants';
import { getFirstAction, setFirstAction } from '../../utils/asyncStorageModule';
import { formatInput } from '../../utils/utils';
import PropTypes from 'prop-types';

/**
 * Container for LoginComponent
 */
class LoginContainer extends Component {
	constructor(props) {
        super(props);

        this.state = {
            stateModel: new LoginStateModel(),
            errorModel: new LoginErrorModel(),
            isLoading: false
        };

        this.onChangeEmailInput = this.onChangeEmailInput.bind(this);
        this.onChangePasswordInput = this.onChangePasswordInput.bind(this);
        this.onChangeMnemonicInput = this.onChangeMnemonicInput.bind(this);
        this.redirectToForgotPassword = this.redirectToForgotPassword.bind(this);
        this.tryLogin = this.tryLogin.bind(this);
        this.redirectToQRScannerScreen = this.redirectToQRScannerScreen.bind(this);
        this.redirectToRegisterScreen = this.redirectToRegisterScreen.bind(this);
    };

    /**
     * Hiding navigation header
     */
    static navigationOptions = {
        header: null
    };

    /**
     * Fill local state from store on component did mount
     */
    async componentDidMount() {
        let email, password, mnemonic = null;

        if(this.props.user.mnemonic) {
            email = this.props.user.email;
            password = this.props.user.password;
            mnemonic = this.props.user.mnemonic;
        } 

        this.setState({
            stateModel: new LoginStateModel(email, password, mnemonic)
        });
    };

    /**
     * Changing internal state when user login inputting
     * @param {string} value current value in Input
     */
    onChangeEmailInput(value) {
        this.setState({
            stateModel: new LoginStateModel(
                value,
                this.state.stateModel.password,
                this.state.stateModel.mnemonic
            )
        });
    };

    /**
     * Changing internal state when user password inputting
     * @param {string} value current value in Input
     */
    onChangePasswordInput(value) {
        this.setState({
            stateModel: new LoginStateModel(
                this.state.stateModel.email,
                value,
                this.state.stateModel.mnemonic
            )
        });
    };
    /**
     * Changing internal state when user password inputting
     * @param {string} value current value in Input
     */
    onChangeMnemonicInput(value) {
        this.setState({
            stateModel: new LoginStateModel(
                this.state.stateModel.email,
                this.state.stateModel.password,
                value
            )
        });
    };

    /**
     * Handle if was allready in use
     */
    async handleFirstLaunch(email) {
        if(!await getFirstAction()) {
            await setFirstAction();
        }

        console.log(await SyncModule.insertSyncSetting(email));
    };

    /**
     * try validate login as email and invokes actionCreators  
     * to change userInfo in store
     */
	async tryLogin() {
        if(this.state.isLoading) return;
        this.setState({ isLoading: true });

        let isEmailValid = validator.isEmail(this.state.stateModel.email);
        let isPasswordValid = !!this.state.stateModel.password;
        let isMnemonicValid = await StorjLib.checkMnemonic(formatInput(this.state.stateModel.mnemonic));
        
        if(isEmailValid && isPasswordValid && isMnemonicValid) {
            await this.login();
        } else {
            this.setState({
                errorModel: new LoginErrorModel(
                    !isEmailValid,
                    !isPasswordValid,
                    !isMnemonicValid,
                    this.state.errorModel.isCredentialsError
                )
            });
        }

        this.setState({ isLoading: false });
    };

    async login() {
        this.props.login(
            this.state.stateModel.email, 
            this.state.stateModel.password,
            formatInput(this.state.stateModel.mnemonic));

        let areCredentialsValidError = await StorjLib.verifyKeys(
            this.state.stateModel.email, 
            this.state.stateModel.password);
            
        if(areCredentialsValidError != 0) {
            this.setState({
                errorModel: new LoginErrorModel(
                    this.state.errorModel.isEmailError,
                    this.state.errorModel.isPasswordError,
                    this.state.errorModel.isMnemonicError,
                    true
                )
            });

            switch (areCredentialsValidError) {
                case 403:
                    this.props.addErrorNotification('Please confirm your email', this.props.deleteNotification);
                break;
                case 401:
                    this.props.addErrorNotification('This account doesn`t exist', this.props.deleteNotification);
                break;
                default: this.props.redirectToAuthFailureScreen({ 
                    mainText: infoScreensConstants.loginFailureMainText, 
                    additionalText: infoScreensConstants.loginFailureAdditionalText 
                });
            }

            this.props.loginError();
            
            return;
        }

        let areKeysImported = await StorjLib.importKeys(
            this.state.stateModel.email,
            this.state.stateModel.password,
            this.state.stateModel.mnemonic.trim().toLowerCase(),
            ''
        );
        
        if(areKeysImported) {
            await this.handleFirstLaunch(this.state.stateModel.email);
            this.props.loginSuccess();
            this.props.redirectToInitializeScreen();
        } else {
            this.props.loginError();
            this.props.redirectToAuthFailureScreen({ 
                mainText: infoScreensConstants.loginFailureMainText, 
                additionalText: infoScreensConstants.loginFailureAdditionalText 
            });
        }
    };

    /**
     * invokes actionCreators that provides navigations
     */
    redirectToRegisterScreen() {
		this.props.navigateToRegisterScreen();
    };
    redirectToMainPageScreen() {
		this.props.redirectToMainScreen();
    };
    redirectToQRScannerScreen() {
        this.props.redirectToQRScannerScreen();
    };

    redirectToForgotPassword() {
        let forgotPasswordURL = 'https://app.storj.io/password-reset';
        Linking.openURL(forgotPasswordURL);
    }

	render() {
		return(
            <LoginComponent
                isLoading = { this.state.isLoading }
                email = { this.state.stateModel.email }
                password = { this.state.stateModel.password }
                mnemonic = { this.state.stateModel.mnemonic }
                isRedirectedFromRegister = { this.props.user.isRedirectedFromRegister }
                isEmailError = { this.state.errorModel.isEmailError }
                isPasswordError = { this.state.errorModel.isPasswordError }
                isMnemonicError = { this.state.errorModel.isMnemonicError }
                onChangeLogin = { this.onChangeEmailInput }
                onChangePassword = { this.onChangePasswordInput }
                onChangeMnemonic = { this.onChangeMnemonicInput }
                redirectToForgotPassword = { this.redirectToForgotPassword }
                onSubmit = { this.tryLogin }
                redirectToQRScannerScreen = { this.redirectToQRScannerScreen }
                registerButtonOnPress = { this.redirectToRegisterScreen }
                redirectToMnemonicHelpScreen = { this.props.screenProps.redirectToMnemonicHelpScreen }
            />
		);
	};
}

/**
 * connecting reducer to component props 
 */
function mapStateToProps(state) { return { user: state.authReducer.user }; };
function mapDispatchToProps(dispatch) { 
    return bindActionCreators({
            loginSuccess,
            loginError,
            login,
            addErrorNotification,
            deleteNotification,
            navigateToRegisterScreen,
            redirectToAuthFailureScreen,
            redirectToMainScreen,
            redirectToInitializeScreen,
            redirectToQRScannerScreen}, dispatch);
};

/**
 * Creating LoginScreen container
 */
export default connect(mapStateToProps, mapDispatchToProps)(LoginContainer);

/**
 * Checking LoginContainer correct prop types
 */
LoginContainer.propTypes = {
    user: PropTypes.shape({
        isLoggedIn: PropTypes.bool,
        email: PropTypes.string,
        password: PropTypes.string,
        mnemonic: PropTypes.string,
        isLoading: PropTypes.bool,
        error: PropTypes.string
    })
};

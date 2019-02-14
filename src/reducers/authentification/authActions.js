import { NavigationActions } from 'react-navigation';
import authActions from '../../utils/constants/actionConstants';
import { changePasswordRequest } from '../../utils/dataservice';

const { LOGIN, LOGIN_SUCCESS, LOGIN_ERROR, 
    REGISTER, REGISTER_SUCCESS, REGISTER_ERROR,
    SET_EMAIL_NOT_CONFIRMED, SET_EMAIL_CONFIRMED,
    SET_ACCOUNT_NOT_EXIST, SET_ACCOUNT_EXIST, CLEAR,
    SAVE_MNEMONIC } = authActions;


/**
 * sends email with link to reset password
 * @param {string} email registered email
 * @returns validity of request
 */
export function resetPassword(email) {
    return async (dispatch) => {  
        let request = changePasswordRequest(email);

        let response = await fetch(request);
        
        return response.ok;
    };
};

/**
 * ActionCreator on login success 
 * @returns action
 */
export function loginSuccess() {
    return { type: LOGIN_SUCCESS };
};

/**
 * ActionCreator on login error
 * @returns action 
 */
export function loginError() {
    return { type: LOGIN_ERROR };
};

/**
 * ActionCreator on login that changing authreducer userInfo state
 * @param {string} email registered email
 * @param {string} password registered password 
 * @param {string} mnemonic registered mnemonic
 * @returns action
 */
export function login(email, password, mnemonic) {
    return { type: LOGIN, payload: { email, password, mnemonic } };
};

/**
 * ActionCreator for setting authReducer to default state
 * @returns action 
 */
export function clear() {
    return { type: CLEAR };
};

/**
 * Saves mnemonic separatly in auth reducer
 * @param {string} mnemonic 
 * @returns action
 */
export function saveMnemonic(mnemonic) {
    return { type: SAVE_MNEMONIC, mnemonic };
}

/**
 * ActionCreator that provides redux navigation to RegisterScreen
 * @returns action
 */
export function navigateToRegisterScreen() {
    return NavigationActions.navigate({ routeName: 'RegisterScreen'});
};

/**
 * ActionCreator on register that changing authreducer userInfo state
 * @param {string} email 
 * @param {string} password 
 * @param {string} mnemonic 
 * @returns action
 */
export function register(email, password) {
    return { type: REGISTER, payload: { email, password } };
};

/**
 * ActionCreator on register success 
 * @returns action
 */
export function registerSuccess(mnemonic) {
    return { type: REGISTER_SUCCESS, payload: { mnemonic }};
};

/**
 * ActionCreator on register error that setting error property in authREducer state in errorMessage
 * @param {string} error error massage 
 * @returns action
 */
export function registerError(error) {
    return { type: REGISTER_ERROR, error };
};

/**
 * ActionCreator that provides redux back navigation
 * @returns action
 */
export function navigateBack() {
    return NavigationActions.back();
};

/**
 * ActionCreator that provides redux navigation to LoginScreen
 * @returns action
 */
export function redirectToLoginScreen() {
    return NavigationActions.reset({
        index: 0,
        actions: [
          NavigationActions.navigate({ routeName: 'LoginScreen'})
        ]
    });
};

/**
 * ActionCreator that provides redux navigation to MainScreen
 * @returns action
 */
export function redirectToMainScreen() {
    return NavigationActions.reset({
        index: 0,
        actions: [
          NavigationActions.navigate({ routeName: 'MainScreen'})
        ]
    });
};

/**
 * ActionCreator that provides redux navigation to QRScannerScreen
 * @returns action
 */
export function redirectToQRScannerScreen() {
    return NavigationActions.navigate({ routeName: 'QRScannerScreen' })
};

/**
 * ActionCreator that provides redux navigation to TermsOfUseScreen
 * @returns action
 */
export function redirectToTermsOfUse() {
    return NavigationActions.navigate({ routeName: 'TermsOfUseScreen' })
};

/**
 * ActionCreator that provides redux navigation to AuthFailureInfoScreen
 * @param {string} error
 * @returns action
 */
export function redirectToAuthFailureScreen(params) {
    return NavigationActions.navigate({ routeName: 'AuthFailureInfoScreen', params });
};

/**
 * ActionCreator that provides redux navigation to RegisterSuccessInfoScreen
 * @returns action
 */
export function redirectToRegisterSuccessScreen() {
    return NavigationActions.navigate({ routeName: 'RegisterSuccessInfoScreen' });
};

/**
 * ActionCreator that provides redux navigation to InitializationScreen
 * @returns action
 */
export function redirectToInitializeScreen() {
    return NavigationActions.navigate({ routeName: 'InitializationScreen' });
}

// ActionCreators for LoginContainer
export const loginActionsCreators = {
    loginSuccess,
    loginError,
    login,
    navigateToRegisterScreen,
    redirectToAuthFailureScreen,
    redirectToMainScreen,
    redirectToInitializeScreen,
    redirectToQRScannerScreen
};

// ActionCreators for RegisterContainer
export const registerActionsCreators = {
    registerSuccess,
    registerError,
    register,
    navigateBack,
    redirectToTermsOfUse,
    redirectToLoginScreen,
    redirectToAuthFailureScreen,
    redirectToRegisterSuccessScreen
};
export const qrScannerActionCreators = {
    loginSuccess,
    loginError,
    login,
    navigateToRegisterScreen,
    redirectToAuthFailureScreen,
    redirectToMainScreen,
    navigateBack,
    redirectToInitializeScreen
};

export const initializeActionCreators = {
    login,
    redirectToQRScannerScreen,
    redirectToLoginScreen,
    navigateToRegisterScreen
};

export const pincodeActionCreators = {
    clear
};

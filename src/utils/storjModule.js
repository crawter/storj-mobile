import {NativeModules, Platform} from 'react-native';

//TODO: StorjModule wil send us only Response objects, 
// so all try/catch blocks should be removed and all error logic should be after checking !isSuccess
const StorjLib = (() => {
    let instance = null;

    const storjLibAndroid = NativeModules.StorjLibAndroid;
    const storjLibIos = NativeModules.StorjLibIos;
    const isAndroid = Platform.OS === 'android';

    const storjLib = isAndroid ? storjLibAndroid : storjLibIos;

    /**
     * This module wraps Native Modules for StorjLib.
     */
    class StorjModule {

        getStorjLibNativeModule(){
            return storjLib;
        }

        /**
         * Generate mnemonic
         * @returns {Promise<boolean>}
         */
        async generateMnemonic() {
            return await storjLib.generateMnemonic();
        }

        async openPayment() {
            return await storjLib.openPaymentScreen();
        }

        /**
         * Check if mnemonic provided has valid format
         * @param {string} mnemonic
         * @returns {Promise<boolean>}
         */
        async checkMnemonic(mnemonic) {
            return storjLib.checkMnemonic(mnemonic);
        };

        /**
         * Send new registration request
         * @param {string} email
         * @param {string} password
         */
        async register(email, password) {
            return await storjLib.register(email, password);
        };

        /**
         * Verify if user exist in storj network
         * @param {string} email
         * @param {string} password
         */
        async verifyKeys(email, password) {
            return await storjLib.verifyKeys(email, password);
        };

        /**
         * Check if auth file allready exist on the device
         * @returns {Promise<boolean>}
         */
        async keysExists() {
            return await storjLib.keysExists();
        };

        /**
         * Creates new auth file for given credentials and stores it on the device
         * and saves them in the current context
         * @param {string} email
         * @param {string} password
         * @param {string} mnemonic
         * @param {string} passcode optional, pass if you want to protect auth file with additional password
         * @returns {Promise<boolean>}
         */
        async importKeys(email, password, mnemonic, passcode) {
            return await storjLib.importKeys(email, password, mnemonic, passcode);
        };


        /**
         * Delete auth file
         * @returns {Promise<boolean>}
         */
        async deleteKeys() {
            return await storjLib.deleteKeys();
        };

        /**
         *
         * @param {string} passcode needed if user has protected your auth file with additional password
         */
        async getKeys(passcode) {
            let response = JSON.parse(await storjLib.getKeys(passcode));

            if(!response.isSuccess) {
                console.log('getKeys ', response.error.message);
            }

            return response;
        };

        /**
         * cancel file downloading
         * @returns {Promise<any>}
         */
        async cancelDownload(fileRef) {
            let response = await storjLib.cancelDownload(fileRef);

            if(!response) {
                console.log('cancelDownload error');
            }

            return response;
        }

        /**
         * cancel file uploading
         * @returns {Promise<any>}
         */
        async cancelUpload(fileRef) {
            let response = await storjLib.cancelUpload(fileRef);

            if(!response) {
                console.log('cancelUpload error');
            }

            return response;
        }

        async getDownloadFolderPath(){
            let response = JSON.parse(await storjLib.getDownloadFolderPath());

            if(!response.isSuccess){
                console.log("getDownloadFolderPath ", response.error.message);
            }

            return response.result;
        }
    }

    return {
        /**
        * @returns {StorjModule}
        */
        getInstance: function() {
            if(!instance) {
                instance = new StorjModule();
            }

            return instance;
        }
    };
})();

export default StorjLib.getInstance();



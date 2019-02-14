import { NOTIFICATIONS_ACTIONS } from '../../utils/constants/actionConstants';
import { NOTIFICATION_TYPES } from '../../utils/constants/typesConstants';

export function addSuccessNotification(message, callback) {
    return { type: NOTIFICATIONS_ACTIONS.ADD, payload: { message, callback, type: NOTIFICATION_TYPES.SUCCESS } };
};

export function addInfoNotification(message, callback) {
    return { type: NOTIFICATIONS_ACTIONS.ADD, payload: { message, callback, type: NOTIFICATION_TYPES.NOTIFICATION } };
};

export function addErrorNotification(message, callback) {
    return { type: NOTIFICATIONS_ACTIONS.ADD, payload: { message, callback, type: NOTIFICATION_TYPES.ERROR } };
};

export function deleteNotification() {
    return { type: NOTIFICATIONS_ACTIONS.DELETE };
};
export function pauseNotification() {
    return { type: NOTIFICATIONS_ACTIONS.PAUSE };
};
export function resumeNotification() {
    return { type: NOTIFICATIONS_ACTIONS.RESUME };
};

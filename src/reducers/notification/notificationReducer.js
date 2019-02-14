import { NOTIFICATIONS_ACTIONS } from '../../utils/constants/actionConstants';
import { DelayedNotification } from '../../models/DelayedNotification';

const initialState = {
    notificationQueue: [],
};


export default function notificationReducer(state = initialState, action) {

    let newState = Object.assign({}, state);

    switch(action.type) {
        case NOTIFICATIONS_ACTIONS.ADD:

            const notification = new DelayedNotification(
                action.payload.callback,
                action.payload.type,
                action.payload.message,
            );

            newState.notificationQueue.push(notification);

            // Pause current notification if it`s not first
            if (state.notificationQueue.length > 1) {
                notification.pause();
            }

            break;
        case NOTIFICATIONS_ACTIONS.DELETE:
            newState.notificationQueue[0].pause();
            newState.notificationQueue.shift();

            // Starts next notification in queue if it exist
            if (newState.notificationQueue[0]) {
                newState.notificationQueue[0].start();
            }

            break;
        case NOTIFICATIONS_ACTIONS.PAUSE:
            state.notificationQueue[0].pause();

            break;
        case NOTIFICATIONS_ACTIONS.RESUME:
            state.notificationQueue[0].start();

            break;
        default:
            return state || initialState;
    }

    return newState;
};
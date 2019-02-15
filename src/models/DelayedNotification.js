import { NOTIFICATION_TYPES } from '../utils/constants/typesConstants'

export class DelayedNotification {

    constructor(callback, type, message) {
        this.callback = callback;
        this.type = type;
        this.message = message;
        this.id = getId();
        this.remainingTime = 4000;
        this.timerId = null;

        switch (type) {
            case NOTIFICATION_TYPES.SUCCESS:
                this.color = 'green';
                break;
            case NOTIFICATION_TYPES.ERROR:
                this.color = 'red';
                break;
            case NOTIFICATION_TYPES.NOTIFICATION:
            default:
                this.color = 'blue';
                break;
        }

        this.start();
    }

    pause() {
        clearTimeout(this.timerId);
        this.remainingTime -= new Date().getMilliseconds() - this.startTime;
    }

    start() {
        this.startTime = new Date().getMilliseconds();
        this.timerId = setTimeout(this.callback, this.remainingTime);
    }
}

function getId() {
    return '_' + Math.random().toString(36).substr(2, 9);
}


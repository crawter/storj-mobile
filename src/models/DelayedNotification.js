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
                this.color = 'rgba(214, 235, 208, 0.4)';
                break;
            case NOTIFICATION_TYPES.ERROR:
                this.color = 'rgba(246, 205, 204, 0.4)';
                break;
            case NOTIFICATION_TYPES.NOTIFICATION:
            default:
                this.color = 'rgba(219, 225, 232, 0.4)';
                break;
        }

        this.start();
    }

    pause() {
        clearTimeout(this.timerId);
        this.remainingTime -= new Date().getMilliseconds() - this.startTime;
    }

    start() {
        console.log("start", this.callback)
        this.startTime = new Date().getMilliseconds();
        this.timerId = setTimeout(this.callback, this.remainingTime);
    }
}

function getId() {
    return '_' + Math.random().toString(36).substr(2, 9);
}


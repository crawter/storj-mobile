export default class LoadingStack {
    constructor(array) {
        this.loadingStack = array.slice();
    }

    setLoading(value) {
        this.loadingStack.push(value);
        console.log("LOADING STACK PUSH", this.loadingStack)
        return this.loadingStack;
    }

    unsetLoading(value) {
        let index = this.loadingStack.indexOf(value);

        if(index === -1) {
            return this.loadingStack;
        }

        this.loadingStack.splice(index, 1);
        console.log("LOADING STACK AFTER UNSET", this.loadingStack)
        return this.loadingStack;
    }
}
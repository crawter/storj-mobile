package mio.storj.mobile.storjlibmodule.utils;

/**
 * Created by Yaroslav-Note on 3/13/2018.
 */

public class ProgressResolver {
    private double mProgress;

    public void setMProgress(double progress) {
        if(progress - mProgress > 0.05) {
            mProgress = progress;
        }
    }

    public double getMProgress() {
        return mProgress;
    }
}

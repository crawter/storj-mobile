package mio.storj.mobile.common;

import mio.storj.mobile.common.utils.GsonSingle;

public abstract class Convertible {
    public String toJson() {
        return GsonSingle.getInstanse().toJson(this);
    }
}

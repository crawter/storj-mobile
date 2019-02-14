package io.storj.mobile.common;

import io.storj.mobile.common.utils.GsonSingle;

public abstract class Convertible {
    public String toJson() {
        return GsonSingle.getInstanse().toJson(this);
    }
}

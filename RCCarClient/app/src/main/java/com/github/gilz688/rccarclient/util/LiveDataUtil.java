package com.github.gilz688.rccarclient.util;

import androidx.lifecycle.MutableLiveData;

public class LiveDataUtil {
    public static void notifyObserver(MutableLiveData data) {
        Object value = data.getValue();
        data.setValue(value);
    }
}

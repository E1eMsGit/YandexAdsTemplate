package ru.apps.e1em.yandexadstemplate;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class DefaultProcessLifecycleObserver implements DefaultLifecycleObserver {
    public void  onProcessCameForeground() {

    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        onProcessCameForeground();
    }
}
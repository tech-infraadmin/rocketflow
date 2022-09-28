package com.tracki.di.component;

import android.app.Application;

import com.tracki.TrackiApplication;
import com.tracki.di.builder.ActivityBuilder;
import com.tracki.di.builder.ReceiverBuilder;
import com.tracki.di.builder.ServiceBuilder;
import com.tracki.di.builder.WorkerMangerBuilder;
import com.tracki.di.module.AppModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {AndroidInjectionModule.class,
        AppModule.class,
        ActivityBuilder.class})
public interface AppComponent {

    void inject(TrackiApplication app);

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }
}



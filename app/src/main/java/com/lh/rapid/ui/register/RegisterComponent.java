package com.lh.rapid.ui.register;


import com.lh.rapid.injector.PerActivity;
import com.lh.rapid.injector.component.ApplicationComponent;
import com.lh.rapid.injector.module.ActivityModule;
import dagger.Component;

/**
 * Created by lh on 2017/9/28.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class,modules = {ActivityModule.class})
public interface RegisterComponent {
    void inject(RegisterActivity activity);
}

package com.lh.rapid.ui.choosecircle;

import com.lh.rapid.injector.PerActivity;
import com.lh.rapid.injector.component.ApplicationComponent;
import com.lh.rapid.injector.module.ActivityModule;

import dagger.Component;

/**
 * Created by we-win on 2017/7/25.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class})
public interface ChooseCircleComponent {
    void inject(ChooseCircleActivity activity);
}

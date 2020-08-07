package it.wazabit.dev.extension.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import it.wazabit.dev.extension.di.components.CameraComponent
import it.wazabit.dev.extension.di.components.MainComponent
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        ViewModelBuilderModule::class
//        AppModuleBinds::class
    ]
)
interface ApplicationComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance applicationContext: Context
        ): ApplicationComponent
    }


    fun mainComponent(): MainComponent.Factory
    fun cameraComponent(): CameraComponent.Factory


}

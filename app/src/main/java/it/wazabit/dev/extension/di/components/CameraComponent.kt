package it.wazabit.dev.extension.di.components

import dagger.Subcomponent
import it.wazabit.dev.extension.di.modules.CameraModule
import it.wazabit.dev.extension.ui.camera.Camera2Fragment
import it.wazabit.dev.extension.ui.camera.CameraFragment


@Subcomponent(modules = [CameraModule::class])
interface CameraComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): CameraComponent
    }

    fun inject(fragment:CameraFragment)
    fun inject(fragment:Camera2Fragment)
}
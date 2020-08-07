package it.wazabit.dev.extension.di.components


import dagger.Subcomponent
import it.wazabit.dev.extension.MainActivity
import it.wazabit.dev.extension.di.modules.MainModule

@Subcomponent(modules = [MainModule::class])
interface MainComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): MainComponent
    }

    fun inject(activity: MainActivity)

}
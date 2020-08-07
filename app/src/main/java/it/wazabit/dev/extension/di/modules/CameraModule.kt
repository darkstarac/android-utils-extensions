package it.wazabit.dev.extension.di.modules

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import it.wazabit.dev.extension.di.ViewModelKey
import it.wazabit.dev.extension.ui.camera.CameraViewModel

@Module
abstract class CameraModule {

    @Binds
    @IntoMap
    @ViewModelKey(CameraViewModel::class)
    abstract fun bindFileViewModel(viewModel: CameraViewModel): ViewModel
}
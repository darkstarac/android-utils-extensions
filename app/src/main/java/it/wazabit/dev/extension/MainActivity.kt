package it.wazabit.dev.extension

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import it.wazabit.dev.extension.ds.MainRepository
import it.wazabit.dev.extension.ui.MainViewModel
import it.wazabit.dev.extension.ui.camera.CameraViewModel
import it.wazabit.dev.extensions.activity.loading
import it.wazabit.dev.extensions.datasource.NetworkResponse
import it.wazabit.dev.extensions.widget.LoadingDialog
import okhttp3.internal.toHexString
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

//    private val applicationComponent by lazy {  (application as Extensions).applicationComponent }
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val loadingDialogFragment = LoadingDialog()
    private val mainViewModel:MainViewModel by viewModels()

//    private val viewModel: CameraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        applicationComponent.mainComponent().create().inject(this)


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_network,R.id.nav_file_system,R.id.nav_camera,R.id.nav_permissions_settings
        ), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

//        mainViewModel.loading.observe(this,{
//            Timber.d("Loading..... $it")
//            loading(it,loadingDialogFragment)
//        })

        loading(true,loadingDialogFragment,Bundle().apply {
            putString(LoadingDialog.MESSAGE,"A loading message")
            putInt(LoadingDialog.BACKGROUND_COLOR,Color.parseColor("#446200EE"))
            putInt(LoadingDialog.PROGRESS_COLOR,Color.parseColor("#ffffff"))
            putInt(LoadingDialog.MESSAGE_COLOR,resources.getColor(R.color.colorError,theme))
        })


    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
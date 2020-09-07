package it.wazabit.dev.extension.ui.network

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import it.wazabit.dev.extension.R
import it.wazabit.dev.extension.ds.persistance.entities.User
import it.wazabit.dev.extension.ui.MainViewModel
import it.wazabit.dev.extensions.datasource.NetworkError
import it.wazabit.dev.extensions.datasource.NetworkResponse
import it.wazabit.dev.extensions.getNetworkStatus
import it.wazabit.dev.extensions.invisible
import it.wazabit.dev.extensions.setSafeOnClickListener
import it.wazabit.dev.extensions.visible
import kotlinx.android.synthetic.main.fragment_network.*
import timber.log.Timber

@AndroidEntryPoint
class NetworkFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()

    private val isNetworkActive by lazy { requireContext().getNetworkStatus() }

    enum class NetworkErrorSimulation{NONE,PARSING,NOT_FOUND}

    private val networkErrorSimulation = MutableLiveData(NetworkErrorSimulation.NONE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_network, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isNetworkActive.observe(viewLifecycleOwner,{
            if(it){
                fragment_network_status_active.visible()
                fragment_network_status_inactive.invisible()
            }else{
                fragment_network_status_inactive.visible()
                fragment_network_status_active.invisible()
            }
        })

        fragment_network_error_toggle_action.check(R.id.fragment_network_error_toggle_none_action)
        fragment_network_error_toggle_action.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked){
                networkErrorSimulation.value = when(checkedId){
                    R.id.fragment_network_error_toggle_none_action -> NetworkErrorSimulation.NONE
                    R.id.fragment_network_error_toggle_404_action -> NetworkErrorSimulation.NOT_FOUND
                    R.id.fragment_network_error_toggle_json_action -> NetworkErrorSimulation.PARSING
                    else -> NetworkErrorSimulation.NONE
                }
            }
        }


        fragment_network_nbr_test_action.setSafeOnClickListener {
            mainViewModel.test(networkErrorSimulation.value).observe(viewLifecycleOwner, Observer {
                parseResponse(it)
            })
        }
    }

    private fun parseResponse(response:NetworkResponse<List<User>>){
        when(response){
            is NetworkResponse.Loading -> {
                fragment_network_nbr_test_result.text = ""
                /*if (it.data == null)*/ mainViewModel.loading.value =true
            }
            is NetworkResponse.Success -> {
                mainViewModel.loading.value = false
                fragment_network_nbr_test_result.text = "Result fetched correctly"
            }
            is NetworkResponse.Error -> {
                mainViewModel.loading.value = false
                Timber.d("Exception ${response.exception}")
                Timber.d("Error ${response.error}")
                when(response.error){
                    is NetworkError.HttpError -> {
                        val error = response.error as NetworkError.HttpError
                        fragment_network_nbr_test_result.text = "HttpError ${error.httpCode} -> ${error.message}"
                    }
                    is NetworkError.JsonParsingError -> {
                        val error = response.error as NetworkError.JsonParsingError
                        fragment_network_nbr_test_result.text = "JsonParsingError ${error.message}"
                    }
                    NetworkError.Timeout -> fragment_network_nbr_test_result.text = "Timeout error"
                    NetworkError.Unknown -> fragment_network_nbr_test_result.text = "Unknown error"
                    NetworkError.IOError -> fragment_network_nbr_test_result.text = "IOError"
                }
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NetworkFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
package it.wazabit.dev.extension.ui.file

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import it.wazabit.dev.extension.R
import kotlinx.android.synthetic.main.fragment_file_system.*

class FileSystemFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_file_system, container, false)
    }


    private lateinit var viewPagerAdapter: PagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPagerAdapter = PagerAdapter(this)

        with(fragment_file_system_view_pager){
            adapter = viewPagerAdapter
        }

        TabLayoutMediator(fragment_file_system_tabs, fragment_file_system_view_pager) { tab, position ->
            tab.text = when(position){
                1 -> "Cache"
                2 -> "External"
                else -> "Internal"
            }
        }.attach()

    }


    private inner class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment = when(position){
            1 -> FileSystemCacheFragment.newInstance()
            2 -> FileSystemExternalFragment.newInstance()
            else -> FileSystemInternalFragment.newInstance()
        }
    }

}
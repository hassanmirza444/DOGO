package com.explore.pakistan.tourism.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.android_hssn.dogo.R
import com.android_hssn.dogo.adapters.DogsGridAdapter
import com.android_hssn.dogo.database.DogsDatabase
import com.android_hssn.dogo.database.repositories.DogCharacteristicsRepository
import com.android_hssn.dogo.managers.AdsManager
import com.android_hssn.dogo.models.DogCharacteristics
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AdsManager.getInstance().loadBanner(requireActivity(), fl_banner)
        DogCharacteristicsRepository.getInstance()!!
            .getAllDogs().observe(viewLifecycleOwner, {
                rv_grid_items.layoutManager = GridLayoutManager(requireContext(), 2)
                val dogsGridAdapter = DogsGridAdapter(
                    requireContext(),
                    it!! as ArrayList<DogCharacteristics?>
                )
                rv_grid_items.setAdapter(dogsGridAdapter)
            })

    }

    override fun onResume() {
        super.onResume()

    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}
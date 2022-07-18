package com.explore.pakistan.tourism.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_hssn.dogo.R
import com.android_hssn.dogo.adapters.DogsGridAdapter
import com.android_hssn.dogo.adapters.DogsListAdapter
import com.android_hssn.dogo.database.repositories.DogCharacteristicsRepository
import com.android_hssn.dogo.models.DogCharacteristics
import kotlinx.android.synthetic.main.fragment_favourites.*

class FavouritesFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        DogCharacteristicsRepository.getInstance()!!
            .getAllFavouriteDogs().observe(viewLifecycleOwner, {
                rv_list_items.layoutManager = LinearLayoutManager(requireContext())
                var mylist = it!! as ArrayList<DogCharacteristics?>
                val t: DogCharacteristics = DogCharacteristics()
                for (i in it!!.indices step 3) {
                    mylist.add(i, DogCharacteristics())
                }
                val dogsListAdapter = DogsListAdapter(requireContext(), mylist)
                rv_list_items.setAdapter(dogsListAdapter)
            })

    }

    companion object {
        @JvmStatic
        fun newInstance() = FavouritesFragment()
    }
}
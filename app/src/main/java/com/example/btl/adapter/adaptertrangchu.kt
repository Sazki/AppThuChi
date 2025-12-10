//package com.example.btl.adapter
//
//import androidx.fragment.app.Fragment
//import androidx.viewpager2.adapter.FragmentStateAdapter
//import com.example.btl.database.MainActivity
//import com.example.btl.database.ui.chi
//import com.example.btl.database.ui.thu
//import com.example.btl.database.ui.trangchu
//
//class adaptertrangchu {
//    class Adapter(activity: MainActivity) : FragmentStateAdapter(activity){
//        override fun createFragment(position: Int): Fragment {
//            return when (position) {
//                0 -> chi()
//                1 -> thu()
//                else -> chi()
//            } as Fragment
//        }
//
//        override fun getItemCount(): Int = 2
//
//    }
//}
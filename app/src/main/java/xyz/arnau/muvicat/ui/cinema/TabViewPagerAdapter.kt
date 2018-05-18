package xyz.arnau.muvicat.ui.cinema

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class TabViewPagerAdapter constructor(
    private val fragmentList: List<Fragment>,
    private val fragmentTitleList: List<Int>,
    fm: FragmentManager,
    private val context: Context
) :
    FragmentPagerAdapter(fm) {
    var fragmentElementsCount = List(fragmentList.size, { 0 })

    override fun getCount(): Int = fragmentList.size

    override fun getItem(position: Int): Fragment = fragmentList[position]

    override fun getPageTitle(position: Int): CharSequence? {
        return if (fragmentElementsCount[position] > 0)
            "${context.getString(fragmentTitleList[position])} (${fragmentElementsCount[position]})"
        else
            context.getString(fragmentTitleList[position])
    }
}
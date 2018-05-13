package xyz.arnau.muvicat.ui

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.movie_list.*

abstract class ListFragment : Fragment() {
    private var mSavedRecyclerLayoutState: Parcelable? = null
    private lateinit var recyclerView: RecyclerView

    abstract fun getRecyclerView(): RecyclerView
    abstract fun getRecyclerViewAdapter(): RecyclerView.Adapter<*>
    abstract fun getRecyclerViewLayoutManager(): RecyclerView.LayoutManager

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView = getRecyclerView()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = getRecyclerViewLayoutManager()
        recyclerView.adapter = getRecyclerViewAdapter()
        recyclerView.isEnabled = false
        saveRecyclerViewState()
    }

    override fun onResume() {
        super.onResume()
        restoreRecyclerViewState()
    }

    override fun onPause() {
        super.onPause()
        saveRecyclerViewState()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        restoreRecyclerViewState()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveRecyclerViewState()
    }


    fun restoreRecyclerViewState() {
        if (mSavedRecyclerLayoutState != null) {
            recyclerView.layoutManager.onRestoreInstanceState(mSavedRecyclerLayoutState)
        }
    }

    private fun saveRecyclerViewState() {
        mSavedRecyclerLayoutState = recyclerView.layoutManager.onSaveInstanceState()
    }
}
package xyz.arnau.muvicat.ui.showing

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.showing_list_toolbar.*
import xyz.arnau.muvicat.repository.model.CinemaShowing
import xyz.arnau.muvicat.ui.cinema.CinemaActivity
import xyz.arnau.muvicat.utils.setGone
import xyz.arnau.muvicat.viewmodel.cinema.CinemaViewModel
import javax.inject.Inject

class CinemaShowingListFragment : BasicShowingListFragment<CinemaShowing>() {
    @Inject
    lateinit var showingsAdapter: CinemaShowingListAdapter

    private lateinit var viewModel: CinemaViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = (activity as CinemaActivity).viewModel
        showingsToolbarLayout.setGone()
    }


    override fun getShowingsLiveData() = viewModel.showings

    override fun getRecyclerViewAdapter() = showingsAdapter

    override fun handleShowingsUpdate(data: List<CinemaShowing>) {
        showingsAdapter.showings = data.sortedWith(compareBy { it.date })
        showingsAdapter.notifyDataSetChanged()
    }
}
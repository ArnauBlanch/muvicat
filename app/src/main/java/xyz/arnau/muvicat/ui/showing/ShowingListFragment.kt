package xyz.arnau.muvicat.ui.showing

import android.app.Activity
import android.location.Location
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.showing_list.*
import kotlinx.android.synthetic.main.showing_list_toolbar.*
import org.joda.time.LocalDate
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.repository.model.Showing
import xyz.arnau.muvicat.ui.utils.DateFilterable
import xyz.arnau.muvicat.ui.LocationAwareActivity
import xyz.arnau.muvicat.ui.MainActivity
import xyz.arnau.muvicat.ui.utils.ScrollableToTop
import xyz.arnau.muvicat.utils.DateFormatter
import xyz.arnau.muvicat.utils.LocationUtils
import xyz.arnau.muvicat.utils.setVisible
import xyz.arnau.muvicat.viewmodel.showing.ShowingListViewModel
import javax.inject.Inject

class ShowingListFragment : BasicShowingListFragment<Showing>(),
    ScrollableToTop,
    DateFilterable {
    @Inject
    lateinit var showingsAdapter: ShowingListAdapter

    @Inject
    lateinit var datesAdapter: DateListAdapter

    @Inject
    lateinit var dateFormatter: DateFormatter

    @Inject
    lateinit var showingListViewModel: ShowingListViewModel

    private var hasLocation = false
    private var showingDates = listOf<LocalDate>()
    private var selectedDate = LocalDate.now()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupToolbar()
    }

    override fun getShowingsLiveData() = showingListViewModel.showings

    override fun onResume() {
        super.onResume()
        if (!hasLocation && getLastLocation() != null) {
            notifyLastLocation(getLastLocation()!!)
        }
        if ((activity as MainActivity).isSelectedFragment(FRAG_ID)) context?.let {
            FirebaseAnalytics.getInstance(it)
                .setCurrentScreen(activity as Activity, "Showing list", "Showing list")
            FirebaseAnalytics.getInstance(it)
        }

        filterShowingsButton.text = dateFormatter.shortDate(selectedDate.toDate())
        showingsAdapter.filter.filter(selectedDate.toDate().time.toString())
        showingsAdapter.notifyDataSetChanged()
    }

    override fun handleShowingsUpdate(data: List<Showing>) {
        handleShowingsUpdate(data, getLastLocation())
    }

    private fun handleShowingsUpdate(data: List<Showing>, lastLocation: Location?) {
        if (lastLocation != null) {
            setLocationToShowings(data, lastLocation)
            hasLocation = true
        }
        showingsAdapter.showings = data.sortedWith(
            compareBy<Showing> { it.date }.thenBy(
                nullsLast(),
                { it.cinemaDistance })
        )

        showingDates = data.map { LocalDate(it.date.time) }.distinct().sorted()
        if (showingDates.isNotEmpty()) {
            filterShowingsButton.text = dateFormatter.shortDate(showingDates.first().toDate())
            showingsAdapter.filter.filter(showingDates.first().toDate().time.toString())
            showingsAdapter.notifyDataSetChanged()
            filterShowingsButton.setVisible()
        }
    }

    private fun setupToolbar() {
        showingsToolbarCollapsing
            .setExpandedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.nunito_sans_black))

        showingsToolbarCollapsing
            .setCollapsedTitleTypeface(ResourcesCompat.getFont(context!!, R.font.nunito_sans_black))

        showingsToolbar.setOnClickListener {
            scrollToTop()
        }

        filterShowingsButton.setOnClickListener {
            context?.let { context ->
                datesAdapter.dates = showingDates
                datesAdapter.currentDate = selectedDate
                datesAdapter.dateFilterable = this
                datesAdapter.notifyDataSetChanged()

                DatePickerDialog(context, datesAdapter).apply {
                    setContentView(layoutInflater.inflate(R.layout.date_picker_dialog, null))
                    show()
                }
            }
        }
    }

    private fun getLastLocation() = (activity as LocationAwareActivity).lastLocation

    private fun notifyLastLocation(lastLocation: Location) {
        if (::showingsAdapter.isInitialized) {
            handleShowingsUpdate(showingsAdapter.showings, lastLocation)
        } else {
            hasLocation = false
        }
    }

    private fun setLocationToShowings(showingList: List<Showing>, location: Location) {
        showingList.forEach {
            if (it.cinemaLatitude != null && it.cinemaLongitude != null) {
                it.cinemaDistance = LocationUtils.getDistance(
                    location,
                    it.cinemaLatitude!!,
                    it.cinemaLongitude!!
                )
            }
        }
    }

    override fun getRecyclerViewAdapter() = showingsAdapter

    override fun scrollToTop() {
        showingsRecyclerView?.scrollToPosition(0)
        showingsToolbarLayout?.setExpanded(true)
    }

    override fun onDatePicked(date: LocalDate) {
        selectedDate = date
        filterShowingsButton.text = dateFormatter.shortDate(date.toDate())
        scrollToTop()
        showingsAdapter.filter.filter(date.toDate().time.toString())
        showingsAdapter.notifyDataSetChanged()
    }

    companion object {
        const val FRAG_ID = 1
    }
}
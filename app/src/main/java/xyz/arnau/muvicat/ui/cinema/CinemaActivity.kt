package xyz.arnau.muvicat.ui.cinema

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.cinema_info.*
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.data.model.CinemaInfo
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.model.Status
import xyz.arnau.muvicat.viewmodel.cinema.CinemaViewModel
import javax.inject.Inject

class CinemaActivity : AppCompatActivity() {
    @Inject
    lateinit var cinemaViewModel: CinemaViewModel

    @Inject
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.cinema_info)
        setupToolbar(null)

        val cinemaId = intent.getLongExtra(CinemaActivity.CINEMA_ID, (-1).toLong())
        if (cinemaId == (-1).toLong())
            throw Exception("Missing cinema identifier")
        else
            cinemaViewModel.setId(cinemaId)
    }

    override fun onStart() {
        super.onStart()

        cinemaViewModel.cinema.observe(this,
            Observer<Resource<CinemaInfo>> {
                if (it != null) handleDataState(it)
            })
    }

    @SuppressLint("SetTextI18n")
    private fun handleDataState(cinemaRes: Resource<CinemaInfo>) {
        when (cinemaRes.status) {
            Status.SUCCESS -> {
                val cinema = cinemaRes.data
                if (cinema != null) {
                    setupToolbar(cinema)
                    cinemaName.text = cinema.name
                    cinemaAddress.text = cinema.address
                    cinemaTown.text = cinema.town
                    if (cinema.region != null && cinema.province != null)
                        cinemaRegion.text = "${cinema.region} (${cinema.province})"
                    else if (cinema.region != null)
                        cinemaRegion.text = cinema.region
                    else if (cinema.province != null)
                        cinemaRegion.text = cinema.province
                }
            }
            Status.ERROR -> throw Exception("The cinema could not be retrieved")
            Status.LOADING -> return
        }
    }

    private fun setupToolbar(cinema: CinemaInfo?) {
        setSupportActionBar(cinemaInfoToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        cinemaInfoToolbarCollapsing
            .setCollapsedTitleTypeface(
                ResourcesCompat.getFont(
                    context,
                    R.font.nunito_sans_extrabold
                )
            )
        cinemaInfoToolbar.setNavigationOnClickListener { onBackPressed() }

        val backArrow =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_chevron_left_black, null)
        backArrow?.setColorFilter(Color.parseColor("#AF0000"), PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setHomeAsUpIndicator(backArrow)


        cinemaInfoToolbarLayout.addOnOffsetChangedListener(object :
            AppBarLayout.OnOffsetChangedListener {
            var isShown = true
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = cinemaInfoToolbarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    cinemaInfoToolbarCollapsing.title = cinema?.name
                    isShown = true
                } else {
                    cinemaInfoToolbarCollapsing.title = " "
                    isShown = false
                }
            }
        })
    }


    companion object {
        private const val CINEMA_ID = "cinema_id"

        fun createIntent(context: Context, cinemaId: Long): Intent {
            return Intent(context, CinemaActivity::class.java).putExtra(CINEMA_ID, cinemaId)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}
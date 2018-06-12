package xyz.arnau.muvicat.ui.home
import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.mikepenz.aboutlibraries.LibsBuilder
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.data_sources.*
import xyz.arnau.muvicat.R
import xyz.arnau.muvicat.ui.UiPreferencesHelper
import xyz.arnau.muvicat.utils.DateFormatter
import java.util.*
import javax.inject.Inject


class DataSourcesDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.data_sources, container, false)
    }

    override fun onStart() {
        super.onStart()
        updateDate.text = arguments.getString(GENCAT_UPDATE_DATE)
    }

    companion object {
        fun newInstance(): DataSourcesDialog {
            return DataSourcesDialog()
        }

        const val GENCAT_UPDATE_DATE = "gencat_update_date"
    }
}
package xyz.arnau.muvicat.ui.home
import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.mikepenz.aboutlibraries.LibsBuilder
import xyz.arnau.muvicat.R


class LibrariesDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_dialog, container, false)

        childFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainer, LibsBuilder()
                    //.withActivityStyle(Libs.ActivityStyle.DARK)
                    .withLicenseShown(true)
                    .withLicenseDialog(true)
                    .withAutoDetect(true)
                    .withLibraries(
                        "skeleton", "lifecycle", "room",
                        "materialratingbar", "youtube", "pageindicatorview", "shimmerlayout",
                        "aspectratioimageview", "gridlayout", "cardview", "apachecommons"
                    )
                    .fragment()
            ).commit()
        return v
    }

    companion object {
        fun newInstance(): LibrariesDialog {
            return LibrariesDialog()
        }
    }
}
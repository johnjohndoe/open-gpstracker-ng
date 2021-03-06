package nl.sogeti.android.gpstracker.ng.features.trackedit

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import nl.sogeti.android.gpstracker.ng.features.databinding.FeaturesBindingComponent
import nl.sogeti.android.gpstracker.v2.sharedwear.util.observe
import nl.sogeti.android.opengpstrack.ng.features.R
import nl.sogeti.android.opengpstrack.ng.features.databinding.FragmentEditDialogBinding

class TrackEditDialogFragment : DialogFragment(), TrackEditModel.View {

    private lateinit var presenter: TrackEditPresenter
    private var binding: FragmentEditDialogBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uri = arguments?.get(TrackEditDialogFragment.ARG_URI) as Uri
        presenter = ViewModelProviders.of(this).get(TrackEditPresenter::class.java)
        presenter.trackUri = uri
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentEditDialogBinding>(inflater, R.layout.fragment_edit_dialog, container, false, FeaturesBindingComponent())
        binding.model = presenter.viewModel
        binding.presenter = presenter
        binding.spinner.onItemSelectedListener = presenter.onItemSelectedListener
        this.binding = binding
        presenter.viewModel.dismissed.observe { sender ->
            if (sender is ObservableBoolean && sender.get()) {
                dismiss()
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        presenter.start()
    }

    override fun onStop() {
        presenter.stop()
        super.onStop()
    }

    companion object {
        private const val ARG_URI = "ARGUMENT_URI"
        fun newInstance(uri: Uri): TrackEditDialogFragment {
            val arguments = Bundle()
            arguments.putParcelable(ARG_URI, uri)
            val fragment = TrackEditDialogFragment()
            fragment.arguments = arguments

            return fragment
        }
    }
}

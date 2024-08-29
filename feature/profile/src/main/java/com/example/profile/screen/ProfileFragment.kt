package com.example.profile.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.permission.registerForRequestPermissionResult
import com.example.permission.requestPermission
import com.example.profile.databinding.FragmentEditPhotoDialogBinding
import com.example.profile.databinding.FragmentProfileBinding
import com.example.profile.di.ProfileComponentViewModel
import com.example.ui.MviFragment
import dagger.Lazy
import javax.inject.Inject
import com.example.common_view.R as commonR


class ProfileFragment : MviFragment<ProfileState, ProfileSideEffect, ProfileEvent>() {
    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding
        get() = _binding!!

    private val adapter: FriendAdapter = FriendAdapter()

    private val takePhotoForResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it != null && it) {
                refreshProfileImage()
            }
        }

    private val pickImageForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    viewModel.consumeEvent(ProfileEvent.Ui.UpdateImageByUri(uri))
                }
            }
        }

    private var _registerForResult: ActivityResultLauncher<String>? = null
    private val registerForResult: ActivityResultLauncher<String>
        get() = _registerForResult!!

    @Inject
    lateinit var factory: Lazy<ProfileViewModel.Factory>

    public override lateinit var viewModel: ProfileViewModel

    override fun renderState(state: ProfileState) {
        with(binding) {
            refreshProfileImage()

            adapter.submitList(state.friends)

            unregisterSwitchChangeListener()
            layoutBased.notificationSwitch.isChecked = state.sendNotifications
            registerSwitchChangeListener()
        }
    }

    private fun registerSwitchChangeListener() {
        binding.layoutBased.notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.consumeEvent(ProfileEvent.Ui.UpdateSendNotificationStatus(isChecked))

            if (isChecked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermission(
                    registerForResult,
                    requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS,
                    requireActivity(),
                )
            }
        }
    }

    private fun unregisterSwitchChangeListener() {
        binding.layoutBased.notificationSwitch.setOnCheckedChangeListener(null)
    }

    private fun refreshProfileImage() {
        with(binding.layoutBased) {
            // Fix ImageView cache
            profileIV.setImageURI(null)
            profileIV.setImageURI(viewModel.state.value.imageUri)

            if (profileIV.drawable == null) {
                profileIV.setImageResource(commonR.drawable.ic_standard_profile)
            }
        }
    }

    override fun handleSideEffects(effect: ProfileSideEffect) {
        when (effect) {
            is ProfileSideEffect.ShowErrorMessage -> {
                Toast.makeText(
                    requireContext(),
                    effect.error.asString(requireContext()),
                    Toast.LENGTH_SHORT
                ).show()
            }

            ProfileSideEffect.RefreshProfileImage -> {
                refreshProfileImage()
            }
        }
    }

    override fun onAttach(context: Context) {
        if (!this::factory.isInitialized) {
            ViewModelProvider(this).get<ProfileComponentViewModel>()
                .profileComponent.inject(this)
            viewModel = ViewModelProvider(this, factory.get()).get()
        }

        _registerForResult =
            registerForRequestPermissionResult(getString(commonR.string.request_notification_permission))

        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.consumeEvent(ProfileEvent.Internal.LoadNotificationStatus)
            viewModel.consumeEvent(ProfileEvent.Internal.LoadFriends)
            viewModel.consumeEvent(ProfileEvent.Internal.LoadProfileImageUri)
        }

        binding.layoutBased.profileIV.setOnClickListener {
            showEditPhotoDialog()
        }

        initAdapter()
    }

    private fun showEditPhotoDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val dialogBinding = FragmentEditPhotoDialogBinding.inflate(layoutInflater)
        builder.setView(dialogBinding.root)

        val dialog: AlertDialog = builder.create()

        dialogBinding.pickPhotoLayout.setOnClickListener {
            val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            pickImageForResult.launch(pickImg)
            dialog.dismiss()
        }
        dialogBinding.takePictureLayout.setOnClickListener {
            takePhotoForResult.launch(viewModel.state.value.imageUri)
            dialog.dismiss()
        }
        dialogBinding.deletePictureLayout.setOnClickListener {
            viewModel.consumeEvent(ProfileEvent.Ui.DeleteImage)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun initAdapter() {
        binding.layoutBased.friendRecycler.addItemDecoration(FriendAdapter.CustomItemDecoration())
        binding.layoutBased.friendRecycler.adapter = adapter
        binding.layoutBased.friendRecycler.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()

        const val TAG = "ProfileFragment"
    }
}

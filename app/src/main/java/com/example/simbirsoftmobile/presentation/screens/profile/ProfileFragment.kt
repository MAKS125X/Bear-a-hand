package com.example.simbirsoftmobile.presentation.screens.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simbirsoftmobile.R
import com.example.simbirsoftmobile.databinding.FragmentEditPhotoDialogBinding
import com.example.simbirsoftmobile.databinding.FragmentProfileBinding
import com.example.simbirsoftmobile.presentation.models.FriendUI
import java.io.File
import java.io.FileOutputStream

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding
        get() = _binding!!

    private val adapter: FriendAdapter by lazy { FriendAdapter(testList) }

    private val profilePhotoFile: File
        get() {
            val tempImagesDir =
                File(
                    requireContext().filesDir,
                    getString(R.string.temp_images_dir),
                )
            tempImagesDir.mkdir()

            return File(
                tempImagesDir,
                getString(R.string.temp_image),
            )
        }

    private val profilePhotoUri: Uri
        get() =
            FileProvider.getUriForFile(
                requireContext(),
                getString(R.string.authorities),
                profilePhotoFile,
            )

    private val takePhotoForResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it != null && it) {

                updateImage()
            }
        }

    private val pickImageForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    copyImageToProfileLocation(uri)
                }
            }
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

        if (profilePhotoFile.exists()) {
            binding.layoutBased.profileIV.setImageURI(profilePhotoUri)
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
            takePhotoForResult.launch(profilePhotoUri)
            dialog.dismiss()
        }
        dialogBinding.deletePictureLayout.setOnClickListener {
            profilePhotoFile.delete()
            binding.layoutBased.profileIV.setImageResource(R.drawable.ic_standard_profile)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun initAdapter() {
        binding.layoutBased.friendRecycler.addItemDecoration(FriendAdapter.CustomItemDecoration())
        binding.layoutBased.friendRecycler.adapter = adapter
        binding.layoutBased.friendRecycler.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun copyImageToProfileLocation(imageUri: Uri) {
        val inputStream = requireContext().contentResolver.openInputStream(imageUri)
        val outputStream = FileOutputStream(profilePhotoFile)
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        updateImage()
    }

    private fun updateImage() {
        binding.layoutBased.profileIV.setImageURI(null)
        binding.layoutBased.profileIV.setImageURI(profilePhotoUri)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()

        const val TAG = "ProfileFragment"

        private val testList =
            arrayOf(
                FriendUI("Дмитрий Валерьевич", R.drawable.avatar_3),
                FriendUI("Дмитрий Валерьевич", R.drawable.avatar_3),
                FriendUI("Дмитрий Валерьевич", R.drawable.avatar_3),
                FriendUI("Дмитрий Валерьевич", R.drawable.avatar_3),
                FriendUI("Евгений Александров", R.drawable.avatar_2),
                FriendUI("Евгений Александров", R.drawable.avatar_2),
                FriendUI("Евгений Александров", R.drawable.avatar_2),
                FriendUI("Евгений Александров", R.drawable.avatar_2),
                FriendUI("Виктор Кузнецов", R.drawable.avatar_1),
                FriendUI("Виктор Кузнецов", R.drawable.avatar_1),
                FriendUI("Виктор Кузнецов", R.drawable.avatar_1),
                FriendUI("Виктор Кузнецов", R.drawable.avatar_1),
            )
    }
}

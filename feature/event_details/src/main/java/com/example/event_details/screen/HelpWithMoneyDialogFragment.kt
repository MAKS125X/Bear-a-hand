package com.example.event_details.screen

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.event_details.databinding.DialogHelpBinding
import com.example.event_details.R as detailsR


class HelpWithMoneyDialogFragment : DialogFragment() {
    private var _binding: DialogHelpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder: AlertDialog.Builder = AlertDialog.Builder(it)
            _binding = DialogHelpBinding.inflate(layoutInflater)

            builder.setView(binding.root)
                .setPositiveButton(
                    detailsR.string.transfer
                ) { dialog, _ ->
                    with(binding) {
                        val value = when {
                            donateEditText.text.toString()
                                .isNotBlank() -> donateEditText.text.toString().toInt()

                            donate100Button.isSelected -> 100
                            donate500Button.isSelected -> 500
                            donate1000Button.isSelected -> 1000
                            donate2000Button.isSelected -> 2000

                            else -> return@setPositiveButton
                        }

                        setFragmentResult(REQUEST_RESULT_KEY, bundleOf(BUNDLE_RESULT_KEY to value))
                        dialog.dismiss()
                    }
                }

                .setNegativeButton(
                    detailsR.string.cancel
                ) { dialog, _ ->
                    dialog.cancel()
                }

            with(binding) {
                donate100Button.setSelectedDonationValue("100")
                donate500Button.setSelectedDonationValue("500")
                donate1000Button.setSelectedDonationValue("1000")
                donate2000Button.setSelectedDonationValue("2000")

                donateEditText.addTextChangedListener { editable ->
                    editable?.let {
                        (dialog as? AlertDialog)?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled =
                            isTranferButtonEnabled()
                    }
                }
            }

            (dialog as? AlertDialog)?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false

            builder.create()
        } ?: throw IllegalStateException("Activity is null")
    }

    override fun onStart() {
        super.onStart()
        clearSelectedValues()
        (dialog as? AlertDialog)?.getButton(AlertDialog.BUTTON_POSITIVE)?.isEnabled = false
    }

    private fun isTranferButtonEnabled(): Boolean =
        binding.donateEditText.text.toString().toIntOrNull() in 1..9_999_99 ||
                binding.donationAmountLayout.children.any { it.isSelected }

    private fun clearSelectedValues() {
        binding.donate100Button.isSelected = false
        binding.donate500Button.isSelected = false
        binding.donate1000Button.isSelected = false
        binding.donate2000Button.isSelected = false
    }

    private fun Button.setSelectedDonationValue(value: String) {
        setOnClickListener {
            val isSelected = this.isSelected

            clearSelectedValues()
            binding.donateEditText.setText(value, TextView.BufferType.EDITABLE)

            this.isSelected = !isSelected
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "HelpWithMoneyDialogFragment"

        const val REQUEST_RESULT_KEY = "RequestDonatedAmount"
        const val BUNDLE_RESULT_KEY = "BundleDonatedAmount"
    }
}
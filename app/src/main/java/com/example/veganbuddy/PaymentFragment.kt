package com.example.veganbuddy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.veganbuddy.databinding.FragmentPaymentBinding


/**
 * A simple [Fragment] subclass.
 * Use the [PaymentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val viewModel by viewModels<CreditCardDbOperations>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.creditCard.observe(viewLifecycleOwner){
            binding.creditCardText.setText(it?.cardNumber.toString() ?: "")
            binding.expiryDateText.setText(it?.expiryDate.toString() ?: "")

        }
        binding.saveCard.setOnClickListener {
            viewModel.onSubmit(binding.creditCardText.text.toString().toLong(),binding.expiryDateText.text.toString())
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
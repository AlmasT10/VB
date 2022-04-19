package com.example.veganbuddy

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.veganbuddy.databinding.FragmentHomeBinding
import org.bson.types.Code
import java.util.regex.Matcher
import java.util.regex.Pattern

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root



        fun useRegex(input: String): Boolean{
            var p: Pattern = Pattern.compile("[A-Za-z][0-9][A-Za-z][0-9][A-Za-z][0-9]")
            var m: Matcher = p.matcher(input)
            return m.matches()
        }
        binding.postalcode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (useRegex(binding.postalcode.text.toString())) {
                    binding.srch.isEnabled = true
                } else {
                    binding.srch.isEnabled = false
                }
            }
        })

        binding.srch.setOnClickListener {
            //val value = binding.postalcode.text

            var code = binding.postalcode.text.toString()
             val intent = Intent(this@HomeFragment.requireContext(),RideDetailsActivity::class.java)
             intent.putExtra("CODE",code)
             startActivity(intent)
//            val intent = Intent(this@HomeFragment.requireContext(),MapsActivity::class.java)
//            startActivity(intent)
            //Toast.makeText(context, "Postal code : $value", Toast.LENGTH_SHORT).show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
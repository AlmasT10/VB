package com.example.veganbuddy

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.veganbuddy.databinding.FragmentHomeBinding
import com.example.veganbuddy.databinding.FragmentProfileBinding


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val viewModel by viewModels<UserDbOperations>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val REQUEST_IMAGE_GALLERY = 132
    private val REQUEST_IMAGE_CAMERA = 142

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.user.observe(viewLifecycleOwner){
            binding.fName.setText(it?.firstName ?: "")
            binding.lName.setText(it?.lastName ?: "")

        }
        binding.profileSave.setOnClickListener {
            viewModel.onSubmit(binding.fName.text.toString(),binding.lName.text.toString())
        }

        binding.profileImage.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Select Image")
            builder.setMessage("Chosse Your Option")
            builder.setPositiveButton("Gallery", { dialog, which ->
                dialog.dismiss()
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
            })
            builder.setNegativeButton("Camera", { dialog, which ->
                dialog.dismiss()
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                        val permission: Int = ContextCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.CAMERA)
                        if (permission != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(android.Manifest.permission.CAMERA),1)
                        }
                        else{
                            startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAMERA)
                        }
                    }
                }
            })
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            binding.profileImage.setImageURI(data.data)
        }
        else if (requestCode == REQUEST_IMAGE_CAMERA && resultCode == Activity.RESULT_OK && data != null) {
            binding.profileImage.setImageBitmap(data.extras?.get("data")as Bitmap)
        }
        else {
            binding.profileImage.setImageResource(R.drawable.account)
            //Toast.makeText(context, "Something went Wrong", Toast.LENGTH_LONG).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
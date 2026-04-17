package com.example.mystudyapp.document.Document_Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.mystudyapp.R
import com.example.mystudyapp.databinding.ScreenDocumentBinding

class Document : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ScreenDocumentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ScreenDocumentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load FragDocMajor trực tiếp
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FragDocMajor())
                .commit()
        }

        binding.fab.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab -> {
                val intent = Intent(this, AddDocumentActivity::class.java)
                startActivity(intent)
            }
        }
    }

}
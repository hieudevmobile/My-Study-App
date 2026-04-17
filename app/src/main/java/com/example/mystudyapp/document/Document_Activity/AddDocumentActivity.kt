package com.example.mystudyapp.document.Document_Activity

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.mystudyapp.databinding.ActivityAddDocumentBinding
import com.example.mystudyapp.document.Database.AppDatabase
import com.example.mystudyapp.document.Entity.Documents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.mystudyapp.R
import com.example.mystudyapp.document.repository.DocumentRepository
import com.example.mystudyapp.document.repository.SubjectRepository
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddDocumentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddDocumentBinding
    private lateinit var subjectRepository: SubjectRepository
    private lateinit var documentRepository: DocumentRepository

    // Dữ liệu được truyền từ FileListActivity (nếu có)
    private var prefilledTenMonHoc: String? = null
    private var prefilledPhanLoai: String? = null

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDocumentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)
        subjectRepository = SubjectRepository(db.monHocDao())
        documentRepository = DocumentRepository(db.taiLieuDao())

        // Mặc định ngày tạo = hôm nay
        binding.ngayTao.text = dateFormat.format(Date())

        // Click vào ngày tạo hoặc icon lịch → mở DatePicker (chỉ cho chọn từ hôm nay trở đi)
        val openDatePicker = View.OnClickListener { showDatePicker() }
        binding.ngayTao.setOnClickListener(openDatePicker)
        binding.btnChonNgay.setOnClickListener(openDatePicker)

        initSpinners()

        // Nhận dữ liệu từ FileListActivity (nếu có)
        prefilledTenMonHoc = intent.getStringExtra("tenMonHoc")
        prefilledPhanLoai = intent.getStringExtra("phanLoai")
        prefillAndDisableFields()

        binding.Them.setOnClickListener { addDocument() }
        binding.Huy.setOnClickListener { finish() }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val picker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selected = Calendar.getInstance()
                selected.set(year, month, dayOfMonth)
                binding.ngayTao.text = dateFormat.format(selected.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        // Chỉ cho chọn từ hôm nay trở đi
        picker.datePicker.minDate = System.currentTimeMillis() - 1000
        picker.show()
    }

    /**
     * Nếu mở từ FileListActivity → fill sẵn tên môn học + phân loại và disable
     */
    private fun prefillAndDisableFields() {
        if (prefilledTenMonHoc != null) {
            binding.tenMonHoc.setText(prefilledTenMonHoc)
            binding.tenMonHoc.isEnabled = false
            binding.tenMonHoc.setTextColor(Color.GRAY)
        }

        if (prefilledPhanLoai != null) {
            binding.phanLoai.text = prefilledPhanLoai
            binding.dsPhanloai.visibility = View.GONE

            // Tìm vị trí trong spinner và set (để giữ đồng bộ)
            val phanLoaiArray = resources.getStringArray(R.array.phanloai)
            val index = phanLoaiArray.indexOf(prefilledPhanLoai)
            if (index >= 0) {
                binding.dsPhanloai.setSelection(index)
            }
        }
    }

    private fun addDocument() {
        // ...existing validation...
        if (binding.tenMonHoc.text.isEmpty()) {
            binding.tenMonHoc.error = "Vui lòng nhập tên môn học"
            binding.tenMonHoc.requestFocus()
            return
        }
        if (binding.tenFile.text.isEmpty()) {
            binding.tenFile.error = "Vui lòng nhập tên file"
            binding.tenFile.requestFocus()
            return
        }
        if (binding.urlFile.text.isEmpty()) {
            binding.urlFile.error = "Vui lòng nhập url file"
            binding.urlFile.requestFocus()
            return
        }

        val tenMonHoc = binding.tenMonHoc.text.toString()
        val phanLoai = binding.phanLoai.text.toString()
        val tenFile = binding.tenFile.text.toString()
        val urlFile = binding.urlFile.text.toString()
        val ngayTao = binding.ngayTao.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            val monHocId = subjectRepository.getOrCreateSubject(tenMonHoc, ngayTao)

            val taiLieu = Documents(
                monHocId = monHocId,
                tenFile = tenFile,
                phanLoai = phanLoai,
                urlFile = urlFile,
                ngayTao = ngayTao,
                isDefault = 0
            )
            documentRepository.addDocument(taiLieu)
            withContext(Dispatchers.Main) { finish() }
        }
    }

    private fun initSpinners() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.phanloai,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        binding.dsPhanloai.adapter = adapter

        binding.dsPhanloai.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                binding.phanLoai.text = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

}
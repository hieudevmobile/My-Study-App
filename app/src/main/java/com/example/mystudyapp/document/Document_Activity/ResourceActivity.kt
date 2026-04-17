package com.example.mystudyapp.document.Document_Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mystudyapp.document.Database.AppDatabase
import com.example.mystudyapp.R
import com.example.mystudyapp.document.repository.DocumentRepository
import com.example.mystudyapp.document.repository.SubjectRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ResourceActivity : AppCompatActivity() {
    private lateinit var subjectRepository: SubjectRepository
    private lateinit var documentRepository: DocumentRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resource)

        val db = AppDatabase.getDatabase(this)
        subjectRepository = SubjectRepository(db.monHocDao())
        documentRepository = DocumentRepository(db.taiLieuDao())

        val monHocId = intent.getIntExtra("monHocId", -1)

        // Load thông tin môn học
        CoroutineScope(Dispatchers.Main).launch {
            val monHoc = withContext(Dispatchers.IO) { subjectRepository.getSubjectById(monHocId) }
            findViewById<TextView>(R.id.tieude).text = monHoc?.tenMonHoc ?: "Unknown"
            if (monHoc?.isDefault == 1) {
                findViewById<TextView>(R.id.xoaMonHoc).visibility = View.GONE
            }
        }

        // Setup click listeners cho 5 phân loại
        setupCategoryClick(R.id.baigiang, monHocId, "Bài giảng")
        setupCategoryClick(R.id.decuong, monHocId, "Đề cương")
        setupCategoryClick(R.id.tailieugiuaky, monHocId, "Tài liệu giữa kỳ")
        setupCategoryClick(R.id.tailieucuoiky, monHocId, "Tài liệu cuối kỳ")
        setupCategoryClick(R.id.tailieuthamkhao, monHocId, "Tài liệu tham khảo")

        // Xóa môn học
        findViewById<TextView>(R.id.xoaMonHoc).setOnClickListener {
            deleteSubject(monHocId)
        }
    }

    private fun setupCategoryClick(viewId: Int, monHocId: Int, phanLoai: String) {
        findViewById<TextView>(viewId).setOnClickListener {
            val intent = Intent(this, FileListActivity::class.java)
            intent.putExtra("monHocId", monHocId)
            intent.putExtra("phanLoai", phanLoai)
            startActivity(intent)
        }
    }

    private fun deleteSubject(monHocId: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val monHoc = withContext(Dispatchers.IO) { subjectRepository.getSubjectById(monHocId) }
            if (monHoc != null) {
                try {
                    withContext(Dispatchers.IO) {
                        documentRepository.deleteDocumentsBySubject(monHocId)
                        subjectRepository.deleteSubject(monHoc)
                    }
                    Toast.makeText(
                        this@ResourceActivity,
                        "Xóa thành công !",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } catch (e: Exception) {
                    return@launch
                }
            }
        }
    }
}
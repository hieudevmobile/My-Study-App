package com.example.mystudyapp.document.Document_Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mystudyapp.R
import com.example.mystudyapp.document.repository.DocumentRepository
import com.example.mystudyapp.document.repository.SubjectRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.net.toUri
import com.example.mystudyapp.document.Database.AppDatabase


class FileListActivity : AppCompatActivity() {
    private lateinit var subjectRepository: SubjectRepository
    private lateinit var documentRepository: DocumentRepository
    private lateinit var recyclerView: RecyclerView
    private var monHocId: Int = -1
    private var phanLoai: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_list)

        val db = AppDatabase.getDatabase(this)
        subjectRepository = SubjectRepository(db.monHocDao())
        documentRepository = DocumentRepository(db.taiLieuDao())

        monHocId = intent.getIntExtra("monHocId", -1)
        phanLoai = intent.getStringExtra("phanLoai") ?: ""

        recyclerView = findViewById(R.id.dsTaiLieu)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load thông tin môn học
        CoroutineScope(Dispatchers.Main).launch {
            val monHoc = withContext(Dispatchers.IO) { subjectRepository.getSubjectById(monHocId) }
            val tenMonHoc = monHoc?.tenMonHoc ?: "Unknown"
            findViewById<TextView>(R.id.tieudeMonhoc).text = tenMonHoc
            if (monHoc?.isDefault == 1) {
                findViewById<TextView>(R.id.xoaitem).visibility = View.GONE
            }

            // Nút thêm tài liệu - truyền sẵn tên môn học + phân loại
            findViewById<TextView>(R.id.themitem).setOnClickListener {
                val addIntent = Intent(this@FileListActivity, AddDocumentActivity::class.java)
                addIntent.putExtra("tenMonHoc", tenMonHoc)
                addIntent.putExtra("phanLoai", phanLoai)
                startActivity(addIntent)
            }
        }
        findViewById<TextView>(R.id.phanLoaiItem).text = phanLoai

        loadFileList()
    }

    override fun onResume() {
        super.onResume()
        loadFileList()
    }

    private fun loadFileList() {
        CoroutineScope(Dispatchers.Main).launch {
            val taiLieuList = withContext(Dispatchers.IO) {
                documentRepository.getDocumentsBySubjectAndCategory(monHocId, phanLoai)
            }
            val adapter = TaiLieuAdapter(taiLieuList.toMutableList()) { taiLieu ->
                openFile(taiLieu.urlFile)
            }
            recyclerView.adapter = adapter

            // Xóa tài liệu đã chọn
            findViewById<TextView>(R.id.xoaitem).setOnClickListener {
                deleteSelectedFiles(adapter)
            }
        }
    }

    private fun deleteSelectedFiles(adapter: TaiLieuAdapter) {
        val selectedIds = adapter.getSelectedItemIds()
        if (selectedIds.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn tài liệu để xoá", Toast.LENGTH_SHORT).show()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                documentRepository.deleteDocuments(selectedIds)
                withContext(Dispatchers.Main) {
                    adapter.removeSelectedItems()
                    Toast.makeText(
                        this@FileListActivity,
                        "Đã xoá thành công",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun openFile(url: String) {
        val uri = url.toUri()
        val mimeType = when {
            url.contains("drive.google.com/drive/folders") -> null
            url.contains("drive.google.com/file/d/") -> "*/*"
            url.endsWith(".pdf") -> "application/pdf"
            url.endsWith(".doc") || url.endsWith(".docx") -> "application/msword"
            url.endsWith(".txt") -> "text/plain"
            else -> "*/*"
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            if (mimeType != null) {
                intent.setDataAndType(uri, mimeType)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Không thể mở liên kết hoặc không có ứng dụng hỗ trợ",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

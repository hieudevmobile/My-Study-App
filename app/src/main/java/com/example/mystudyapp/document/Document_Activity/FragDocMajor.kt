package com.example.mystudyapp.document.Document_Activity

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mystudyapp.document.Database.AppDatabase
import com.example.mystudyapp.R
import com.example.mystudyapp.document.Entity.Subjects
import com.example.mystudyapp.document.repository.SubjectRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Fragment hiển thị danh sách tất cả môn học (NGÀNH HỌC)
 */
class FragDocMajor : Fragment(), SearchView.OnQueryTextListener {
    private lateinit var subjectRepository: SubjectRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var adapter: MonHocAdapter
    private var dsMonHocGoc: List<Subjects> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_major, container, false)
        val db = AppDatabase.getDatabase(requireContext())
        subjectRepository = SubjectRepository(db.monHocDao())

        recyclerView = view.findViewById(R.id.dsTaiLieu)
        recyclerView.layoutManager = LinearLayoutManager(context)
        searchView = view.findViewById(R.id.searchMonHoc)

        loadMonHoc()
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.top = if (parent.getChildAdapterPosition(view) == 0) 12 else 14
            }
        })
        searchView.setOnQueryTextListener(this)
        return view
    }

    private fun loadMonHoc() {
        CoroutineScope(Dispatchers.Main).launch {
            dsMonHocGoc = withContext(Dispatchers.IO) {
                subjectRepository.getAllSubjects()
            }
            adapter = MonHocAdapter(dsMonHocGoc) { monHoc ->
                val intent = Intent(context, ResourceActivity::class.java)
                intent.putExtra("monHocId", monHoc.id)
                startActivity(intent)
            }
            recyclerView.adapter = adapter
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(newText: String?): Boolean {
        filter(newText.orEmpty())
        return true
    }

    private fun filter(query: String) {
        val danhSachLoc = if (query.isEmpty()) {
            dsMonHocGoc
        } else {
            dsMonHocGoc.filter {
                it.tenMonHoc.contains(query, ignoreCase = true)
            }
        }
        adapter.filterList(danhSachLoc)
    }

    override fun onResume() {
        super.onResume()
        if (isAdded) {
            loadMonHoc()
        }
    }
}

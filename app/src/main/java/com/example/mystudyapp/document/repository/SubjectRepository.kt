package com.example.mystudyapp.document.repository

import com.example.mystudyapp.document.DAO.SubjectDao
import com.example.mystudyapp.document.Entity.Subjects

class SubjectRepository(private val subjectDao: SubjectDao) {

    suspend fun getAllSubjects(): List<Subjects> {
        return subjectDao.getAll()
    }

    suspend fun getSubjectById(id: Int): Subjects? {
        return subjectDao.getById(id)
    }

    suspend fun deleteSubject(subject: Subjects) {
        subjectDao.delete(subject)
    }

    /**
     * Kiểm tra môn học đã tồn tại chưa, nếu chưa thì tạo mới.
     * Trả về ID của môn học.
     */
    suspend fun getOrCreateSubject(name: String, ngayThem: String): Int {
        val existing = subjectDao.getByName(name)
        return if (existing != null) {
            existing.id
        } else {
            val newSubject = Subjects(
                tenMonHoc = name,
                ngayThem = ngayThem,
                isDefault = 0
            )
            subjectDao.insert(newSubject).toInt()
        }
    }
}


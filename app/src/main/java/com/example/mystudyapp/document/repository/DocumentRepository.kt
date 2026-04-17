package com.example.mystudyapp.document.repository

import com.example.mystudyapp.document.DAO.DocumentDao
import com.example.mystudyapp.document.Entity.Documents

class DocumentRepository(private val documentDao: DocumentDao) {

    suspend fun addDocument(document: Documents) {
        documentDao.insert(document)
    }
    /**
     * Lấy tài liệu theo môn học và phân loại
     */
    suspend fun getDocumentsBySubjectAndCategory(
        monHocId: Int,
        phanLoai: String
    ): List<Documents> {
        return documentDao.getByMonHocId(monHocId).filter { it.phanLoai == phanLoai }
    }

    fun deleteDocuments(ids: List<Int>) {
        documentDao.deleteByIds(ids)
    }

    fun deleteDocumentsBySubject(monHocId: Int) {
        documentDao.deleteBymonHocID(monHocId)
    }
}


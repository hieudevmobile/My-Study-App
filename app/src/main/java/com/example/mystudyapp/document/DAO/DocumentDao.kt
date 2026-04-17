package com.example.mystudyapp.document.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mystudyapp.document.Entity.Documents

@Dao
interface DocumentDao {
    @Insert
    suspend fun insert(taiLieu: Documents)

    @Query("SELECT * FROM tai_lieu WHERE monHocId = :monHocId")
    suspend fun getByMonHocId(monHocId: Int): MutableList<Documents>

    @Query("DELETE FROM tai_lieu WHERE id IN (:ids)")
    fun deleteByIds(ids: List<Int>)

    @Query("DELETE FROM tai_lieu WHERE monHocId = :mhID")
    fun deleteBymonHocID(mhID: Int)
}
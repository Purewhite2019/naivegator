package com.sjtu.naivegator.db

import androidx.room.*
import java.util.*
//
//@Entity
//data class CanteenHistoryEntry(
//    @PrimaryKey val hid: Int,
//    @ColumnInfo(name = "date") val date : String,
//    @ColumnInfo(name = "canteen") val canteen : String,
//    @ColumnInfo(name = "rating") val rating : Int,
//    @ColumnInfo(name = "remark") val remark : String
//)
//
//@Dao
//interface CanteenHistoryDao {
//    @Query("SELECT * FROM canteenhistoryentry")
//    fun getAll(): List<UserPreference>
//
//    @Query("SELECT * FROM canteenhistoryentry WHERE hid IN (:hids)")
//    fun loadAllByIds(hids: IntArray) : List<CanteenHistoryEntry>
//
//    @Insert
//    fun insertAll(historyEntry: List<CanteenHistoryEntry>)
//
//    @Insert
//    fun insert(historyEntry: CanteenHistoryEntry)
//
//    @Delete
//    fun delete(historyEntry: CanteenHistoryEntry)
//
//    @Delete
//    fun deleteAll(historyEntry: List<CanteenHistoryEntry>)
//
//    @Update
//    fun update(historyEntry: CanteenHistoryEntry)
//}
//
//@Database(entities = [CanteenHistoryEntry::class], version = 1)
//abstract class CanteenHistoryDatabase : RoomDatabase() {
//    abstract fun canteenHistoryDao() : CanteenHistoryDao
//}
package com.sjtu.naivegator.db

import androidx.room.*

@Entity
data class History(
    @PrimaryKey val date: Long,
    @ColumnInfo(name = "primaryKey") val primaryKey : String,
    @ColumnInfo(name = "secondaryKey") val secondaryKey : String,
    @ColumnInfo(name = "rating") val rating : Int,
    @ColumnInfo(name = "remark") val remark : String
)

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history")
    fun getAll(): List<History>

    @Query("SELECT * FROM history WHERE primaryKey == :primKey AND secondaryKey == :secondKey")
    fun loadAllByBothKey(primKey: String, secondKey: String) : List<History>

    @Query("SELECT * FROM history WHERE primaryKey == :primKey")
    fun loadAllByPrimaryKey(primKey: String) : List<History>

    @Query("SELECT * FROM history WHERE secondaryKey == :secondKey")
    fun loadAllBySecondaryKey(secondKey: String) : List<History>

    @Query("SELECT * FROM history WHERE date == :queryDate")
    fun loadAllByDate(queryDate: Long) : List<History>

    @Insert
    fun insertAll(history: List<History>)

    @Insert
    fun insert(history: History)

    @Delete
    fun delete(history: History)

    @Delete
    fun deleteAll(history: List<History>)

    @Update
    fun update(history: History)
}

@Database(entities = [History::class], version = 1)
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun historyDao() : HistoryDao
}
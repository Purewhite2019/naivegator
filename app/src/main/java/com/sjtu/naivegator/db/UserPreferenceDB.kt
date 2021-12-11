package com.sjtu.naivegator.db
//
//import androidx.room.*
//
//@Entity
//data class UserPreference(
//    @PrimaryKey val pid: Long,
//    @ColumnInfo(name = "target") val target: String,
//    @ColumnInfo(name = "weight") val weight: Int,
//    @ColumnInfo(name = "date") val date: String
//)
//
//@Dao
//interface UserPreferenceDao {
//    @Query("SELECT * FROM userpreference")
//    fun getAll(): List<UserPreference>
//
//    @Query("SELECT * FROM userpreference WHERE pid IN (:pids)")
//    fun loadAllByIds(pids: List<Int>) : List<UserPreference>
//
//    @Insert
//    fun insertAll(userPreferences: List<UserPreference>)
//
//    @Insert
//    fun insert(userPreference: UserPreference)
//
//    @Delete
//    fun delete(userPreference: UserPreference)
//
//    @Delete
//    fun deleteAll(userPreferences: List<UserPreference>)
//
//    @Update
//    fun update(userPreferences: UserPreference)
//}
//
//@Database(entities = [UserPreference::class], version = 1)
//abstract class UserPreferenceDatabase : RoomDatabase() {
//    abstract fun userPreferenceDao() : UserPreferenceDao
//}
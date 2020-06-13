package net.schueller.peertube.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ServerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Server server);

    @Query("DELETE FROM server_table")
    void deleteAll();

    @Delete
    void delete(Server server);

    @Query("SELECT * from server_table ORDER BY id ASC")
    LiveData<List<Server>> getAllServers();
}
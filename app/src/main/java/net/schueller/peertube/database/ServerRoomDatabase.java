package net.schueller.peertube.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Server.class}, version = 1, exportSchema = false)
public abstract class ServerRoomDatabase extends RoomDatabase {

    public abstract ServerDao serverDao();

    private static volatile ServerRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static ServerRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ServerRoomDatabase.class) {
                if (INSTANCE == null) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                ServerRoomDatabase.class, "server_database")
                                .build();
                    }
                }
            }
        }
        return INSTANCE;
    }

}


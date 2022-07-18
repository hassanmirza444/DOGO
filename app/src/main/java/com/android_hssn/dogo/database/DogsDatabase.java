package com.android_hssn.dogo.database;

import androidx.room.Room;
import androidx.room.Database;
import android.content.Context;
import androidx.room.RoomDatabase;

import com.android_hssn.dogo.temp.Example;
import com.android_hssn.dogo.database.dao.DogCharacteristicsDao;
import com.android_hssn.dogo.models.DogCharacteristics;
import com.android_hssn.dogo.temp.ExampleCharacteristicsDao;

@Database(entities = {DogCharacteristics.class, Example.class}, version = 1)
public abstract class DogsDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "dogs";
    private static DogsDatabase INSTANCE;

    public static DogsDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DogsDatabase.class, DATABASE_NAME)
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public abstract DogCharacteristicsDao dogCharacteristicsDao();
    public abstract ExampleCharacteristicsDao exampleCharacteristicsDao();

}

package com.footprints.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by Douglas on 27-05-2018.
 */
@Database(entities = {GeoData.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase mController;

    static AppDatabase getInstance(final Context mContext) {
        if (mController == null) {
            synchronized (AppDatabase.class) {
                if (mController == null) {
                    mController = Room.databaseBuilder(mContext.getApplicationContext(),
                            AppDatabase.class, "geo")
                            .build();
                }
            }
        }
        return mController;
    }

    abstract GeoDataDao getGeoDataDao();
}

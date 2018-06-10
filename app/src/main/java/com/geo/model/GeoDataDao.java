package com.geo.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Dell on 27-05-2018.
 */
@Dao
public interface GeoDataDao {

    @Query("SELECT * from geodata")
    LiveData<List<GeoData>> getGeoData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GeoData geoData);
}

package com.footprints.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Douglas on 27-05-2018.
 */
@Dao
public interface GeoDataDao {

    @Query("SELECT * from geodata")
    LiveData<List<GeoData>> getGeoData();

    @Query("SELECT * from geodata")
    List<GeoData> getLocationsList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GeoData geoData);

    @Delete
    void deleteMyLoc(GeoData geoData);

    @Query("DELETE FROM geodata where geoId = :id")
    void geoTab(int id);

    @Query("DELETE FROM geodata")
    void deleteAllMyHistory();
}

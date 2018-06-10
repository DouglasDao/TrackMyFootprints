package com.geo.model;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

/**
 * Created by Dell on 27-05-2018.
 */

public class GeoDataRepo {

    private LiveData<List<GeoData>> mLocations;
    private GeoDataDao geoDataDao;

    public GeoDataRepo(Application application) {
        geoDataDao = AppDatabase.getInstance(application).getGeoDataDao();
        mLocations = geoDataDao.getGeoData();
    }

    public LiveData<List<GeoData>> getLocations() {
        return mLocations;
    }

    public void insert(GeoData geoData) {
        new insertAsyncTask(geoDataDao).execute(geoData);
    }

    private static class insertAsyncTask extends AsyncTask<GeoData, Void, Void> {

        private GeoDataDao mAsyncTaskDao;

        insertAsyncTask(GeoDataDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final GeoData... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}

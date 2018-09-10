package com.geo.model;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Dell on 27-05-2018.
 */

public class GeoDataRepo {

    public GeoDataDao mAsyncTaskDao;
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

        insertAsyncTask insertAsyncTask = new insertAsyncTask(geoDataDao);
        mAsyncTaskDao = insertAsyncTask.mAsyncTaskDao;
        insertAsyncTask.execute(geoData);
    }

    public void delete(int geoData) {
        new delAsyncTask(geoDataDao).execute(geoData);
    }

    public void deleteAll() {
        new deleteAllAsyncTask(geoDataDao).execute();
    }

    public static class insertAsyncTask extends AsyncTask<GeoData, Void, Void> {

        GeoDataDao mAsyncTaskDao;

        insertAsyncTask(GeoDataDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final GeoData... params) {
            GeoData geoData = params[0];
            mAsyncTaskDao.insert(geoData);
            return null;
        }
    }

    public static class delAsyncTask extends AsyncTask<Integer, Void, Void> {

        GeoDataDao mAsyncTaskDao;

        delAsyncTask(GeoDataDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            if (mAsyncTaskDao != null) {
                int a = integers[0];
                Log.d(TAG, "Integer : " + a);
                mAsyncTaskDao.geoTab(integers[0]);
            }
            return null;
        }
    }

    public static class deleteAllAsyncTask extends AsyncTask<Void, Void, Void> {

        GeoDataDao mAsyncTaskDao;

        deleteAllAsyncTask(GeoDataDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (mAsyncTaskDao != null) {
                mAsyncTaskDao.deleteAllMyHistory();
            }
            return null;
        }
    }


}

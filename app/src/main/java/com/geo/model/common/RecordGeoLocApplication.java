package com.geo.model.common;

import android.app.Application;

import com.geo.model.DaoMaster;
import com.geo.model.DaoSession;

import org.greenrobot.greendao.database.Database;

public class RecordGeoLocApplication extends Application {

    private DaoSession daoSession;
    private Database db;
    DaoMaster.DevOpenHelper helper;
    private static RecordGeoLocApplication mAppController;

    public static RecordGeoLocApplication getInstance() {
        return mAppController;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bindDao();
    }

    private void bindDao() {
        helper = new DaoMaster.DevOpenHelper(this, "geo-db");
        db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public void ClearDao() {
        daoSession.clear();
        DaoMaster.dropAllTables(db, true);
        DaoMaster.createAllTables(db, false);
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}

package com.geo.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class GeoData {
    private String latit;
    private String longit;

    @Generated(hash = 253891504)
    public GeoData(String latit, String longit) {
        this.latit = latit;
        this.longit = longit;
    }

    @Generated(hash = 474607495)
    public GeoData() {
    }

    public String getLatit() {
        return latit;
    }

    public void setLatit(String latit) {
        this.latit = latit;
    }

    public String getLongit() {
        return longit;
    }

    public void setLongit(String longit) {
        this.longit = longit;
    }
}

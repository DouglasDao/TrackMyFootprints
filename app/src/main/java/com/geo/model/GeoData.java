package com.geo.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class GeoData {
    private String latit;
    private String longit;
    private String address;

    @Generated(hash = 132852881)
    public GeoData(String latit, String longit, String address) {
        this.latit = latit;
        this.longit = longit;
        this.address = address;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

package com.footprints.view.iview;

import android.net.Uri;

public interface IGeoLocationView extends IView {
    void setImgRes(int res);
    void updateProfileInfo(String name, Uri photo);

    void signOut();
}

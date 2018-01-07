package com.geo.presenter.ipresenter;

import android.content.Intent;
import android.os.Bundle;

public interface IPresenter {

    void onCreatePresenter(Bundle bundle);

    void onStartPresenter();

    void onStopPresenter();

    void onPausePresenter();

    void onResumePresenter();

    void onDestroyPresenter();

    void onActivityResultPresenter(int requestCode, int resultCode, Intent data);

    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

}

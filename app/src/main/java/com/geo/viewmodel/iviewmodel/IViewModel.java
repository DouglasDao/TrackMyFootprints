package com.geo.viewmodel.iviewmodel;

import android.content.Intent;
import android.os.Bundle;

public interface IViewModel {

    void onCreateViewModel(Bundle bundle);

    void onStartViewModel();

    void onStopViewModel();

    void onPauseViewModel();

    void onResumeViewModel();

    void onDestroyViewModel();

    void onActivityResultViewModel(int requestCode, int resultCode, Intent data);

    void onSaveInstanceStateViewModel(Bundle data);

    void onRestoreInstanceStateViewModel(Bundle data);

    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

}

package com.footprints.common;

public interface Constants {
    interface SharedPrefKey {
        String LAT_KEY = "LAT_KEY";
        String LNG_KEY = "LNG_KEY";
        String LOGIN_FLAG = "LOGIN_FLAG";
        String PHOTO_URI = "PHOTO_URI";
        String DISPLAY_NAME = "DISPLAY_NAME";
        String PHONE_VERIFY_CODE = "PHONE_VERIFY_CODE";
    }

    interface RequestCodes {
        int GOOGLE_SIGN_IN = 9001;
        int FB_SIGN_IN = 9002;
    }

    interface BundleKey {
        String USER_EMAIL = "USER_EMAIL";
        String USER_NAME = "USER_NAME";
        String PHOTO_URI = "PHOTO_URO";
        String LOGIN_TYPE = "LOGIN_TYPE";
        String MOB_NUM = "MOB_NUM";
        String USER = "USER";
        String SOCIAL = "SOCIAL";
    }

}

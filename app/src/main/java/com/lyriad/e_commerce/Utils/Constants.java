package com.lyriad.e_commerce.Utils;

import java.util.Locale;
import java.util.TimeZone;

public final class Constants {

    public static final int GALLERY_REQUEST_CODE = 3268;
    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("America/Santo_Domingo");
    public static final Locale LOCALE = new Locale("en", "DO");
    public static final int SHARED_PREFERENCES_PRIVATE_MODE = 0;
    public static final String SHARED_PREFERENCES_NAME = "UserSession";
    public static final String FIREBASE_AUTH_EMAIL = "manueleduardo0320@gmail.com";
    public static final String FIREBASE_AUTH_PASSWORD = "$2y$18$Vhw3gljlyerW6J9NlsClZeaD85tqoK//02pojksXANuDakq.u3iEq";
    public static final String API_URL = "http://ec2-3-86-40-181.compute-1.amazonaws.com:6789";
    public static final String API_TOKEN = "Gk1mjFIIyYOmQmF-5kbELuIHrwZZE8MYSytLtTjT";
    public static final String API_LOGIN = "/login";
    public static final String API_FORGOT_PASSWORD = "/forgot";
    public static final String API_REGISTER_USER = "/register";
    public static final String API_UPDATE_USER = "/user/update";
    public static final String API_CATEGORIES = "/category";
    public static final String API_PRODUCTS = "/product";

}

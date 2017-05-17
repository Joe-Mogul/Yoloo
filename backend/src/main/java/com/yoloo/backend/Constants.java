package com.yoloo.backend;

import lombok.experimental.UtilityClass;

/**
 * API Keys, Client Ids and Audience Ids for accessing APIs and configuring
 * Cloud Endpoints.
 * When you deploy your solution, you need to use your own API Keys and IDs.
 * Please refer to the documentation for this sample for more details.
 */
@UtilityClass
public final class Constants {

  public static final String ADMIN_EMAIL = "admin@yolooapp.com";
  public static final String ADMIN_USERNAME = "yoloo_admin";

  /**
   * Firebase app url.
   */
  public static final String FIREBASE_APP_URL = "https://yoloo-151719.firebaseio.com";

  /**
   * Firebase secret path.
   */
  public static final String FIREBASE_SECRET_JSON_PATH =
      "/WEB-INF/firebase_service_account.json";

  /**
   * Android client ID from Google Cloud console.
   */
  public static final String ANDROID_CLIENT_ID =
      "175355244637-aeloianmbm6g0sh2ifc1t3dq6pc7caiq.apps.googleusercontent.com";

  /**
   * iOS client ID from Google Cloud console.
   */
  public static final String IOS_CLIENT_ID = "YOUR-IOS-CLIENT-ID";

  /**
   * Web client ID from Google Cloud console.
   */
  public static final String WEB_CLIENT_ID =
      "175355244637-cnod9pcii71rsqpjj17mp24v95uul63o.apps.googleusercontent.com";

  /**
   * Audience ID used to limit access to some client to the API.
   */
  public static final String AUDIENCE_ID = WEB_CLIENT_ID;

  public static final String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";

  /**
   * API package name.
   */
  public static final String API_OWNER = "backend.yoloo.com";

  /**
   * API package path.
   */
  public static final String API_PACKAGE_PATH = "";
}

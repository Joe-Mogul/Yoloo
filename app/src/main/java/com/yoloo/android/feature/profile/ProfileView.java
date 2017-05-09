package com.yoloo.android.feature.profile;

import com.yoloo.android.data.db.AccountRealm;
import com.yoloo.android.framework.MvpView;

public interface ProfileView extends MvpView {

  void onProfileLoaded(AccountRealm account);

  void onError(Throwable t);
}

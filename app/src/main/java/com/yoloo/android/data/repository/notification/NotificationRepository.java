package com.yoloo.android.data.repository.notification;

import com.yoloo.android.data.Response;
import com.yoloo.android.data.db.FcmRealm;
import com.yoloo.android.data.db.NotificationRealm;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NotificationRepository {

  private static NotificationRepository instance;

  private final NotificationRemoteDataSource remoteDataStore;
  private final NotificationDiskDataSource diskDataStore;

  private NotificationRepository(NotificationRemoteDataSource remoteDataStore,
      NotificationDiskDataSource diskDataStore) {
    this.remoteDataStore = remoteDataStore;
    this.diskDataStore = diskDataStore;
  }

  public static NotificationRepository getInstance(NotificationRemoteDataSource remoteDataStore,
      NotificationDiskDataSource diskDataStore) {
    if (instance == null) {
      instance = new NotificationRepository(remoteDataStore, diskDataStore);
    }
    return instance;
  }

  public Completable registerFcmToken(@Nonnull FcmRealm fcm) {
    return remoteDataStore
        .registerFcmToken(fcm)
        .doOnComplete(() -> diskDataStore.registerFcmToken(fcm))
        .subscribeOn(Schedulers.io());
  }

  public Completable unregisterFcmToken(@Nonnull FcmRealm fcm) {
    return remoteDataStore
        .unregisterFcmToken(fcm)
        .doOnComplete(diskDataStore::unregisterFcmToken)
        .subscribeOn(Schedulers.io());
  }

  public Observable<Response<List<NotificationRealm>>> listNotifications(@Nullable String cursor,
      int limit) {

    Observable<Response<List<NotificationRealm>>> diskObservable =
        diskDataStore.list(limit).subscribeOn(Schedulers.io());

    Observable<Response<List<NotificationRealm>>> remoteObservable =
        remoteDataStore.list(cursor, limit)
            .doOnNext(response -> diskDataStore.addAll(response.getData()))
            .subscribeOn(Schedulers.io());

    return remoteObservable;
  }
}

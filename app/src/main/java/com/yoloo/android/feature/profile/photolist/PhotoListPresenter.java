package com.yoloo.android.feature.profile.photolist;

import com.yoloo.android.data.repository.media.MediaRepository;
import com.yoloo.android.framework.MvpPresenter;

import javax.annotation.Nonnull;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

class PhotoListPresenter extends MvpPresenter<PhotoListView> {

  private final MediaRepository mediaRepository;

  private String cursor;

  PhotoListPresenter(MediaRepository mediaRepository) {
    this.mediaRepository = mediaRepository;
  }

  void listMedias(@Nonnull String userId) {
    Disposable d = mediaRepository.listMedias(userId, cursor, 20)
        .observeOn(AndroidSchedulers.mainThread(), false)
        .subscribe(response -> {
          if (response.getData().isEmpty()) {
            getView().onEmpty();
          } else {
            cursor = response.getCursor();
            if (isViewAttached()) {
              getView().onLoaded(response.getData());
            }
          }

          cursor = response.getCursor();
        }, throwable -> getView().onError(throwable));

    getDisposable().add(d);
  }
}

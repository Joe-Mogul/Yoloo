package com.yoloo.android.data.repository.media;

import com.annimon.stream.Optional;
import com.yoloo.android.data.Response;
import com.yoloo.android.data.db.CommentRealm;
import com.yoloo.android.data.db.CommentRealmFields;
import com.yoloo.android.data.db.PostRealm;
import com.yoloo.android.data.db.PostRealmFields;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

class MediaDiskDataStore {

  private static MediaDiskDataStore instance;

  private MediaDiskDataStore() {
  }

  static MediaDiskDataStore getInstance() {
    if (instance == null) {
      instance = new MediaDiskDataStore();
    }
    return instance;
  }

  Single<Optional<CommentRealm>> get(@Nonnull String commentId) {
    return Single.fromCallable(() -> {
      Realm realm = Realm.getDefaultInstance();

      CommentRealm result =
          realm.where(CommentRealm.class).equalTo(CommentRealmFields.ID, commentId).findFirst();

      Optional<CommentRealm> comment =
          result == null ? Optional.empty() : Optional.of(realm.copyFromRealm(result));

      realm.close();

      return comment;
    });
  }

  void add(@Nonnull CommentRealm comment) {
    Realm realm = Realm.getDefaultInstance();

    realm.executeTransaction(tx -> {
      PostRealm post =
          tx.where(PostRealm.class).equalTo(PostRealmFields.ID, comment.getPostId()).findFirst();

      if (post != null) {
        post.increaseCommentCount();

        if (comment.isAccepted()) {
          post.setAcceptedCommentId(comment.getId());
        }

        tx.insertOrUpdate(post);
        tx.insertOrUpdate(comment);
      }
    });

    realm.close();
  }

  void addAll(@Nonnull List<CommentRealm> comments) {
    Realm realm = Realm.getDefaultInstance();
    realm.executeTransaction(tx -> tx.insertOrUpdate(comments));
    realm.close();
  }

  Completable delete(@Nonnull CommentRealm comment) {
    return Completable.fromAction(() -> {
      Realm realm = Realm.getDefaultInstance();

      realm.executeTransaction(tx -> {
        PostRealm post =
            tx.where(PostRealm.class).equalTo(PostRealmFields.ID, comment.getPostId()).findFirst();

        if (post != null) {
          post.decreaseCommentCount();

          if (comment.isAccepted()) {
            post.setAcceptedCommentId(null);
          }
          tx.insertOrUpdate(post);
        }

        CommentRealm result = tx
            .where(CommentRealm.class)
            .equalTo(CommentRealmFields.ID, comment.getId())
            .findFirst();

        if (result != null) {
          result.deleteFromRealm();
        }
      });

      realm.close();
    });
  }

  Observable<Response<List<CommentRealm>>> list(@Nonnull String postId) {
    return Observable.fromCallable(() -> {
      Realm realm = Realm.getDefaultInstance();

      RealmResults<CommentRealm> results = (realm
          .where(CommentRealm.class)
          .equalTo(CommentRealmFields.POST_ID, postId)
          .findAllSorted(CommentRealmFields.CREATED, Sort.DESCENDING));

      List<CommentRealm> comments =
          results.isEmpty() ? Collections.emptyList() : realm.copyFromRealm(results);

      realm.close();

      return Response.create(comments, null);
    });
  }

  Completable vote(@Nonnull String commentId, int direction) {
    return Completable.fromAction(() -> {
      Realm realm = Realm.getDefaultInstance();

      realm.executeTransaction(tx -> {
        CommentRealm comment =
            tx.where(CommentRealm.class).equalTo(CommentRealmFields.ID, commentId).findFirst();

        if (comment != null) {
          setVoteCounter(direction, comment);
          comment.setVoteDir(direction);
          tx.insertOrUpdate(comment);
        }
      });

      realm.close();
    });
  }

  void accept(@Nonnull CommentRealm comment) {
    Realm realm = Realm.getDefaultInstance();

    realm.executeTransaction(tx -> {
      PostRealm post =
          tx.where(PostRealm.class).equalTo(PostRealmFields.ID, comment.getPostId()).findFirst();

      if (post != null) {
        if (comment.isAccepted()) {
          post.setAcceptedCommentId(comment.getId());
        }

        tx.insertOrUpdate(post);
        tx.insertOrUpdate(comment);
      }
    });

    realm.close();
  }

  private void setVoteCounter(int direction, CommentRealm comment) {
    switch (comment.getVoteDir()) {
      case 0:
        if (direction == 1) {
          comment.increaseVotes();
        } else if (direction == -1) {
          comment.decreaseVotes();
        }
        break;
      case 1:
        if (direction == 0) {
          comment.decreaseVotes();
        } else if (direction == -1) {
          comment.decreaseVotes();
          comment.decreaseVotes();
        }
        break;
      case -1:
        if (direction == 0) {
          comment.increaseVotes();
        } else if (direction == 1) {
          comment.increaseVotes();
          comment.increaseVotes();
        }
        break;
    }
  }
}

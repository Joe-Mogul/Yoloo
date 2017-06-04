package com.yoloo.backend.util;

import com.googlecode.objectify.impl.translate.opt.joda.JodaTimeTranslators;
import com.googlecode.objectify.util.Closeable;
import com.yoloo.backend.account.Account;
import com.yoloo.backend.account.AccountShard;
import com.yoloo.backend.bookmark.Bookmark;
import com.yoloo.backend.comment.Comment;
import com.yoloo.backend.comment.CommentShard;
import com.yoloo.backend.device.DeviceRecord;
import com.yoloo.backend.feed.Feed;
import com.yoloo.backend.game.Tracker;
import com.yoloo.backend.group.TravelerGroupEntity;
import com.yoloo.backend.group.TravelerGroupShard;
import com.yoloo.backend.travelertype.TravelerTypeEntity;
import com.yoloo.backend.media.MediaEntity;
import com.yoloo.backend.notification.Notification;
import com.yoloo.backend.post.PostEntity;
import com.yoloo.backend.relationship.Relationship;
import com.yoloo.backend.tag.Tag;
import com.yoloo.backend.tag.TagShard;
import com.yoloo.backend.vote.Vote;
import org.junit.After;
import org.junit.Before;

import static com.yoloo.backend.util.TestObjectifyService.fact;

public class TestBase extends GAETestBase {

  private Closeable rootService;

  @Before
  public void setUp() {
    setUpObjectifyFactory(new TestObjectifyFactory());
    JodaTimeTranslators.add(fact());

    fact().register(Account.class);
    fact().register(AccountShard.class);

    fact().register(PostEntity.class);
    fact().register(PostEntity.PostShard.class);

    fact().register(Tag.class);
    fact().register(TagShard.class);

    fact().register(TravelerTypeEntity.class);
    fact().register(TravelerGroupEntity.class);
    fact().register(TravelerGroupShard.class);

    fact().register(Comment.class);
    fact().register(CommentShard.class);

    fact().register(Relationship.class);
    fact().register(Vote.class);
    fact().register(Feed.class);
    fact().register(Tracker.class);
    fact().register(DeviceRecord.class);
    fact().register(Notification.class);
    fact().register(MediaEntity.class);
    fact().register(Bookmark.class);
  }

  @After
  public void tearDown() {
    rootService.close();
    rootService = null;
  }

  private void setUpObjectifyFactory(TestObjectifyFactory factory) {
    if (rootService != null) {
      rootService.close();
    }

    TestObjectifyService.setFactory(factory);
    rootService = TestObjectifyService.begin();
  }
}

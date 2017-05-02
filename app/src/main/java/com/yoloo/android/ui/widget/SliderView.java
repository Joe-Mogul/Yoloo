package com.yoloo.android.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.annimon.stream.Stream;
import com.bumptech.glide.Glide;
import com.yoloo.android.R;
import com.yoloo.android.util.WeakHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SliderView extends FrameLayout {

  @BindView(R.id.viewpager) ViewPager viewPager;
  @BindView(R.id.indicator) CircleIndicator indicator;

  private int currentPage = 0;
  private SliderPagerAdapter adapter;

  public SliderView(@NonNull Context context) {
    super(context);
    init();
  }

  public SliderView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public SliderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    inflate(getContext(), R.layout.layout_image_slider, this);

    ButterKnife.bind(this);

    adapter = new SliderPagerAdapter(getContext());
    viewPager.setAdapter(adapter);
    indicator.setViewPager(viewPager);
    adapter.registerDataSetObserver(indicator.getDataSetObserver());
  }

  public void setImageUrls(List<String> imageUrls) {
    Stream.of(imageUrls).forEach(s -> adapter.addUrl(s));

    WeakHandler handler = new WeakHandler();

    Runnable runnable = () -> {
      if (currentPage == adapter.getCount()) {
        currentPage = 0;
      }
      viewPager.setCurrentItem(currentPage++, true);
    };

    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        handler.post(runnable);
      }
    }, 5500, 5500);
  }

  public void addImageUrl(String url) {
    setImageUrls(Collections.singletonList(url));
  }

  static class SliderPagerAdapter extends PagerAdapter {
    private final List<String> urls = new ArrayList<>(3);
    private final LayoutInflater inflater;
    private final Context context;

    SliderPagerAdapter(Context context) {
      this.context = context;
      this.inflater = LayoutInflater.from(context);
    }

    public void addUrl(@NonNull String url) {
      urls.add(url);
      notifyDataSetChanged();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      container.removeView((View) object);
    }

    @Override
    public int getCount() {
      return urls.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      View imageLayout = inflater.inflate(R.layout.slide_image, container, false);
      ImageView view = ButterKnife.findById(imageLayout, R.id.iv_slide);
      Glide.with(context).load(urls.get(position)).into(view);
      container.addView(imageLayout, 0);
      return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
      return view.equals(object);
    }
  }
}

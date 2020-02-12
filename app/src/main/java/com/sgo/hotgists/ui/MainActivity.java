package com.sgo.hotgists.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sgo.hotgists.R;
import com.sgo.hotgists.models.Gist;
import com.sgo.hotgists.network.GistService;
import com.sgo.hotgists.network.GitApiClient;
import com.sgo.hotgists.utils.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements ItemClickSupport.OnItemClickListener {

  private static final String TAG = MainActivity.class.getSimpleName();

  // show username and count if user has more than 5 gists
  private static final int THRESHOLD_COUNT = 5;
  private static final int REQ_CODE_DETAIL = 1;

  // subscriptions to be released when activity is no longer needed
  private final CompositeDisposable disposables = new CompositeDisposable();

  private GistListingAdapter adapter;
  private LinearLayoutManager layoutManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // retain subscriptions even on device rotation
    if (savedInstanceState == null) {
      this.adapter = new GistListingAdapter();
      this.layoutManager = new LinearLayoutManager(this);
      // setup network stream
      initializeGistList();
    }

    // setup RecyclerView
    final RecyclerView list = findViewById(R.id.list);
    list.setLayoutManager(layoutManager);
    list.setAdapter(adapter);
    initializeUserGistListOnScroll(list);
    ItemClickSupport.addTo(list).setOnItemClickListener(this);
  }

  /**
   * Loads Gists from network and updates the list when ready
   */
  private void initializeGistList() {
    final DisposableSingleObserver<List<Gist>> disposable = GitApiClient.getGistService().listGistData()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableSingleObserver<List<Gist>>() {
          @Override
          public void onSuccess(List<Gist> gists) {
            adapter.setData(gists);
          }

          @Override
          public void onError(Throwable e) {
            // TODO show feedback to user
            Log.e(TAG, "onError: " + e.getMessage(), e);
          }
        });
    disposables.add(disposable);
  }

  /**
   * For each visible item on the list,
   * load user's Gists from network and update the list when ready.
   * Will only trigger once the scrolling has finished.
   *
   * @param list
   */
  private void initializeUserGistListOnScroll(RecyclerView list) {
    list.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        if (RecyclerView.SCROLL_STATE_IDLE == newState) {
          final int from = layoutManager.findFirstCompletelyVisibleItemPosition();
          final int to = layoutManager.findLastCompletelyVisibleItemPosition();

          final List<Single<Integer>> userGists = new ArrayList<>();
          // subscribe for each visible item
          final GistService service = GitApiClient.getGistService();
          for (int i = from; i < adapter.getItemCount()
              && i <= to; i++) {
            final int position = i;
            final Gist item = adapter.getItem(i);
            final String username = item.getOwnerLogin();

            final DisposableMaybeObserver<Integer> disposable = service.listUserGistData(username)
                .subscribeOn(Schedulers.io())
                .map(List::size)
                .filter(size -> size >= THRESHOLD_COUNT)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableMaybeObserver<Integer>() {
                  @Override
                  public void onSuccess(Integer count) {
                    item.setCount(count);
                    adapter.notifyItemChanged(position);
                  }

                  @Override
                  public void onError(Throwable e) {
                    // TODO show feedback to user
                    Log.e(TAG, "onError: " + e.getMessage(), e);
                  }

                  @Override
                  public void onComplete() {
                    // ignored gist count less than 5
                  }
                });

            disposables.add(disposable);
          }
        }
      }
    });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (isFinishing()) {
      // release subscriptions only if screen is exited.
      // will resume network streams even on device rotation
      disposables.dispose();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQ_CODE_DETAIL) {
      final boolean favorite = data.getBooleanExtra(GistDetailActivity.EXTRA_RESULT, true);
      final int position = data.getIntExtra(GistDetailActivity.EXTRA_POSITION, 0);
      adapter.getItem(position).setFavorite(favorite);
      adapter.notifyItemChanged(position);
    }
  }

  @Override
  public void onItemClicked(RecyclerView recyclerView, int position, View v) {
    // show gist detail screen
    Intent intent = new Intent(this, GistDetailActivity.class);
    intent.putExtra(GistDetailActivity.EXTRA_REQ_CODE, REQ_CODE_DETAIL);
    intent.putExtra(GistDetailActivity.EXTRA_GIST_DATA, adapter.getItem(position));
    intent.putExtra(GistDetailActivity.EXTRA_POSITION, position);

    startActivityForResult(intent, REQ_CODE_DETAIL);
  }
}

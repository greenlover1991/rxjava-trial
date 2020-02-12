package com.sgo.hotgists.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.sgo.hotgists.R;
import com.sgo.hotgists.models.Gist;

public class GistDetailActivity extends AppCompatActivity {

  static final String EXTRA_REQ_CODE = "EXTRA_REQ_CODE";
  static final String EXTRA_GIST_DATA = "EXTRA_GIST_DATA";
  static final String EXTRA_POSITION = "EXTRA_POSITION";
  static final String EXTRA_RESULT = "RESULT";

  private Gist gist;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gist_detail);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    if (savedInstanceState == null) {
      this.gist = (Gist) getIntent().getSerializableExtra(EXTRA_GIST_DATA);
    }
    // TODO show the gists data from the view
    // show raw files using TextView for non-image mimetypes
    // use ImageView for image/* types
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_gist_detail, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    if (gist.isFavorite()) {
      menu.findItem(R.id.favorite).setIcon(R.drawable.baseline_favorite_black_24);
    } else {
      menu.findItem(R.id.favorite).setIcon(R.drawable.baseline_favorite_border_black_24);
    }
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        Intent result = new Intent();
        result.putExtra(EXTRA_RESULT, gist.isFavorite());
        result.putExtra(EXTRA_POSITION, getIntent().getIntExtra(EXTRA_POSITION, 0));
        setResult(RESULT_OK, result);
        finish();
        break;
      case R.id.favorite:
        gist.setFavorite(!gist.isFavorite());
        invalidateOptionsMenu();
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}

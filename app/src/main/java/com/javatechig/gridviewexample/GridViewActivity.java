package com.javatechig.gridviewexample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GridViewActivity extends ActionBarActivity {
    private static final String TAG = GridViewActivity.class.getSimpleName();
    private GridView mGridView;
    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;
    private String popularMoviesUrl = "http://api.themoviedb.org/3/movie/popular?api_key=a247f9509512beb8588090c3d377d6c9";
    private String highestRatedUrl = "http://api.themoviedb.org/3/movie/top_rated?api_key=a247f9509512beb8588090c3d377d6c9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gridview);

        mGridView = (GridView) findViewById(R.id.gridView);
        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, mGridData);
        mGridView.setAdapter(mGridAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                GridItem item = (GridItem) parent.getItemAtPosition(position);

                Intent intent = new Intent(GridViewActivity.this, DetailsActivity.class);


                intent.putExtra("title", item.getTitle()).
                        putExtra("image", item.getImage()).
                        putExtra("releaseDate", item.getReleaseDate()).
                        putExtra("rating", item.getRating()).
                        putExtra("synopsis", item.getSynopsis());

                startActivity(intent);
            }
        });

    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {
        HttpURLConnection connection = null;

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();

                String response = streamToString(stream);
                if (params[0] == popularMoviesUrl) {
                    parseResult(response);
                    result = 1;
                } else if (params[0] == highestRatedUrl) {
                    parseResult(response);
                    result = 1;
                } else {
                    parseId(response);
                    result = 2;
                }

            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {

            if (result == 1) {
                mGridAdapter.setGridData(mGridData);
            } else {
                Toast.makeText(GridViewActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }

        }
    }


    String streamToString(InputStream stream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        if (null != stream) {
            stream.close();
        }
        return result;
    }

    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("results");
            GridItem item;
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                String title = post.optString("title");
                String image = post.optString("poster_path");
                String finalImage = "http://image.tmdb.org/t/p/w185" + image;
                String releaseDate = post.optString("release_date");
                String rating = post.optString("vote_average");
                String synopsis = post.optString("overview");
                item = new GridItem();
                item.setTitle(title);
                item.setImage(finalImage);
                item.setReleaseDate(releaseDate);
                item.setRating(rating);
                item.setSynopsis(synopsis);
                mGridData.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseId(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("results");
            GridItem video;
            for (int i = 0; i < posts.length() && i <= 2; i++) {
                JSONObject post = posts.optJSONObject(i);
                int id = post.optInt("key");
                video = new GridItem();
                video.setId("http://api.themoviedb.org/3/movie/" + id + "/videos");
                mGridData.add(video);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.popular_movies:
                if (mGridData.size() >= 20) {
                    mGridData.clear();
                }
                new AsyncHttpTask().execute(popularMoviesUrl);
                break;


            case R.id.highest_rated:
                if (mGridData.size() >= 20) {
                    mGridData.clear();
                }
                new AsyncHttpTask().execute(highestRatedUrl);
                break;
        }
        return true;
    }

}
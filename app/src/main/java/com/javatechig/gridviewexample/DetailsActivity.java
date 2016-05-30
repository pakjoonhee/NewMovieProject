package com.javatechig.gridviewexample;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends ActionBarActivity {
    private TextView titleTextView;
    private TextView movieRelease;
    private TextView movieRating;
    private TextView movieSynopsis;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details_view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Bundle bundle = getIntent().getExtras();

        String title = bundle.getString("title");
        String image = bundle.getString("image");
        String releaseDate = bundle.getString("releaseDate");
        String rating = bundle.getString("rating");
        String synopsis = bundle.getString("synopsis");
        int id = bundle.getInt("id");
        String parseTrailer = "http://api.themoviedb.org/3/movie/" + id + "/videos";

        titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(Html.fromHtml(title));

        movieRelease = (TextView) findViewById(R.id.movieRelease);
        movieRelease.setText(releaseDate);

        movieRating = (TextView) findViewById(R.id.movieRating);
        String divideTen = rating + "/10";
        movieRating.setText(divideTen);

        movieSynopsis = (TextView) findViewById(R.id.movieSynopsis);
        movieSynopsis.setText(synopsis);

        imageView = (ImageView) findViewById(R.id.grid_item_image);
        Picasso.with(this).load(image).into(imageView);


    }


}

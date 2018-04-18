package com.example.mitest.mitest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mitest.mitest.Adaptor.DeliveryListAdaptor;
import com.example.mitest.mitest.Model.Delivery;
import com.example.mitest.mitest.Util.ImageLoader;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ImageView mImageView;
    private TextView mTextView;

    private Delivery mDelivery;
    private ImageLoader mImageLoader;
    private FloatingActionButton mFloatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        setTitle(Constant.TITLE_NAME_DETAIL);

        mImageView = findViewById(R.id.imageViewDetail);
        mTextView = findViewById(R.id.textViewDetail);

        Intent intent = getIntent();
        mDelivery = (Delivery) intent.getSerializableExtra(Constant.INTENT_NAME);
        mImageLoader = new ImageLoader(getApplicationContext());
        mImageLoader.DisplayImage(mDelivery.imageUrl, mImageView);
        mTextView.setText(mDelivery.description);

        // Initialize the Map View
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize Floating Action Button
        mFloatingActionButton = findViewById(R.id.button_direction);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse(String.format("%s%s,%s", Constant.URI_PATH, mDelivery.location.lat, mDelivery.location.lng));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage(Constant.MAP_PACKAGE);
                startActivity(mapIntent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Insert the map lat and lng and pin point to the location
        LatLng location = new LatLng(mDelivery.location.lat, mDelivery.location.lng);
        googleMap.addMarker(new MarkerOptions().position(location)
                .title(mDelivery.location.address));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        googleMap.setMinZoomPreference(6);
        googleMap.setMinZoomPreference(14);
    }
}

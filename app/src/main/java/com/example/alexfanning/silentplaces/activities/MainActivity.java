package com.example.alexfanning.silentplaces.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexfanning.silentplaces.Geofencing;
import com.example.alexfanning.silentplaces.PlaceLoader;
import com.example.alexfanning.silentplaces.R;
import com.example.alexfanning.silentplaces.SilentPlace;
import com.example.alexfanning.silentplaces.adapters.PlaceAdapter;
import com.example.alexfanning.silentplaces.provider.PlaceContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.*;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements PlaceAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<SilentPlace[]>,
                                                                    GoogleApiClient.ConnectionCallbacks,
                                                                        GoogleApiClient.OnConnectionFailedListener{


    private static final int PLACE_PICKER_REQUEST_CODE = 99;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 98;

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mPlacesRv;
    private PlaceAdapter mAdapter;
    private TextView mTvRv;
    private SilentPlace[] mPlaces;
    private GoogleApiClient mClient;
    private Geofencing mGeofencing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        mPlacesRv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mPlacesRv.setHasFixedSize(false);
        addSwipeToDelete();

        DividerItemDecoration did = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        did.setDrawable(getDrawable(R.drawable.border_places));

        getSupportLoaderManager().initLoader(PlaceLoader.PLACE_LOADER_ID,null,this);
        setUpLoaders();

        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this,this)
                .build();
        mGeofencing = new Geofencing(this,mClient);



    }

    public void mockGeofenceLocation(){
        Location location = new Location("Network");
        location.setLatitude(53.6049012);
        location.setLongitude(-6.1677743);
        location.setTime(new Date().getTime());
        location.setAccuracy(3.0f);
        location.setElapsedRealtimeNanos(System.nanoTime());

        try{
            LocationServices.FusedLocationApi.setMockLocation(mClient,location);
        }catch (SecurityException se){
            Log.e(TAG, "mockGeofenceLocation: " );
        }

    }


    private void findViews(){
        mPlacesRv = (RecyclerView)findViewById(R.id.rv_places);
        mTvRv = (TextView) findViewById(R.id.tv_rv);
    }

    public void button(View view) {
        //mockGeofenceLocation();
        Intent i = new Intent(this,DetailActivity.class);
        startActivity(i);
    }

    private void setUpLoaders(){
        LoaderManager lm = getSupportLoaderManager();
        Loader<SilentPlace[]> spLoader = lm.getLoader(PlaceLoader.PLACE_LOADER_ID);
        if (spLoader == null){
            lm.initLoader(PlaceLoader.PLACE_LOADER_ID,null,this);
        }else{
            lm.restartLoader(PlaceLoader.PLACE_LOADER_ID,null,this).forceLoad();
        }
    }

    @Override
    public Loader<SilentPlace[]> onCreateLoader(int id, Bundle args) {
        return new PlaceLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<SilentPlace[]> loader, SilentPlace[] places) {
        mPlaces = places;
        if (mPlaces == null){
            mPlacesRv.setVisibility(View.GONE);
            mTvRv.setVisibility(View.VISIBLE);
            mTvRv.setText(getString(R.string.rv_error));
        }else if (mPlaces.length == 0){
            mPlacesRv.setVisibility(View.GONE);
            mTvRv.setVisibility(View.VISIBLE);
            mTvRv.setText(getString(R.string.rv_no_places));
        }else{
            mPlacesRv.setVisibility(View.VISIBLE);
            mTvRv.setVisibility(View.GONE);
            mAdapter = new PlaceAdapter(mPlaces,this,this);
            mPlacesRv.setAdapter(mAdapter);
            refreshGeofences();
        }
    }

    private void refreshGeofences(){
        List<String> guids = new ArrayList<>();
        for (SilentPlace sp :mPlaces) {
            guids.add(sp.get_id());
        }
        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mClient,
                guids.toArray(new String[guids.size()]));
        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(@NonNull PlaceBuffer places) {
                mGeofencing.updateList(places);
                mGeofencing.registerAllGeofences();
            }
        });


    }



    @Override
    public void onLoaderReset(Loader<SilentPlace[]> loader) {

    }

    @Override
    public void onListItemClick(SilentPlace place) {
        Toast.makeText(this,place.get_id(),Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpLoaders();
    }


    public void addSwipeToDelete(){
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                SilentPlace sp = mPlaces[viewHolder.getAdapterPosition()];

                Uri uri = PlaceContract.PlaceEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(sp.get_id()).build();
                getContentResolver().delete(uri,null,null);
                setUpLoaders();

            }
        };

        new ItemTouchHelper(itemTouchCallback).attachToRecyclerView(mPlacesRv);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mPlaces != null && mPlaces.length != 0)
            refreshGeofences();

        Log.i(TAG, "API Connected successful!");
        //mockGeofenceLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "API Connection Suspended!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "API Connection Failed!");
    }
}

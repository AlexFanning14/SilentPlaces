package com.example.alexfanning.silentplaces.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexfanning.silentplaces.R;
import com.example.alexfanning.silentplaces.SilentPlace;
import com.example.alexfanning.silentplaces.provider.PlaceContract;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG =  DetailActivity.class.getSimpleName();

    private static final int PLACE_PICKER_REQUEST_CODE = 99;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 98;
    private static final String EMPTY_STRING = "";

    private Button mBtnSelect;
    private Button mBtnAdd;
    private Button mBtnClear;
    private TextView mPlaceName;
    private TextView mPlaceAdd;
    private EditText mDesc;
    private Spinner mSpinner;
    private SilentPlace mSilentPlace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ActionBar ab = this.getSupportActionBar();
        if (ab!= null)
            ab.setDisplayHomeAsUpEnabled(true);

        findViews();
        populateSpinner();
    }

    private void findViews(){
        mBtnSelect = (Button)findViewById(R.id.btn_select_location);
        mBtnAdd = (Button)findViewById(R.id.btnAdd);
        mBtnClear = (Button)findViewById(R.id.btnClear);
        mPlaceName = (TextView)findViewById(R.id.place_name);
        mPlaceName.setText(getString(R.string.tv_place_name,EMPTY_STRING));
        mPlaceAdd = (TextView)findViewById(R.id.place_add);
        mPlaceAdd.setText(getString(R.string.tv_place_add, EMPTY_STRING));
        mDesc = (EditText) findViewById(R.id.et_desc);
        mSpinner = (Spinner) findViewById(R.id.spinner_detail);
    }

    private void populateSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.options_array,R.layout.support_simple_spinner_dropdown_item );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
    }



    public void btnSelect_Clicked(View v){
        checkPermission();
    }

    private void checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);
        }else{
            displayPlacePicker();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    displayPlacePicker();
                }else {
                    Toast.makeText(this, "Permission was not granted, unable to proceed.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void displayPlacePicker(){
        try{
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Intent i = builder.build(this);
            startActivityForResult(i,PLACE_PICKER_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST_CODE && resultCode == RESULT_OK){
            Place place = PlacePicker.getPlace(this,data);
            if (place == null){
                Toast.makeText(this, "No Place Selected", Toast.LENGTH_SHORT).show();
                return;
            }
            mPlaceName.setText(getString(R.string.tv_place_name,place.getName().toString()));
            mPlaceAdd.setText(getString(R.string.tv_place_add, place.getAddress().toString()));
            String placeID = place.getId();
            mSilentPlace = new SilentPlace(placeID);
        }
    }

    public void addBtn_clicked(View v){
        String description;
        int silentMode = mSpinner.getSelectedItemPosition();

        int indexOfPlaceName = 12;
        String placeName = mPlaceName.getText().toString().substring(indexOfPlaceName);

        if (placeName.equals(EMPTY_STRING)){
            Toast.makeText(this, "Please select a location.", Toast.LENGTH_SHORT).show();
            return;
        }




        if (mDesc.getText().equals(EMPTY_STRING)){
            description = mPlaceName.getText().toString().substring(indexOfPlaceName);
        }else{
            description = mDesc.getText().toString();
        }
        mSilentPlace.setSilentMode(silentMode);
        mSilentPlace.setDescription(description);

        insertToDb();

        finish();
    }

    private void insertToDb(){
        ContentValues cv = new ContentValues();
        cv.put(PlaceContract.PlaceEntry.COLUMN_PLACE_ID, mSilentPlace.get_id());
        cv.put(PlaceContract.PlaceEntry.COLUMN_DESCRIPTION, mSilentPlace.getDescription());
        cv.put(PlaceContract.PlaceEntry.COLUMN_SILENT_MODE, mSilentPlace.getSilentMode());

        try{
            Uri uri = getContentResolver().insert(PlaceContract.PlaceEntry.CONTENT_URI,cv);
           // Toast.makeText(this, "Place inserted", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Log.e(TAG, "insertToDb: ");
        }
    }




}


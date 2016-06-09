package samueliox.allergycheck;

import android.app.ProgressDialog;

import android.content.IntentSender;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import samueliox.allergycheck.data.AirQuality;
import samueliox.allergycheck.data.CityLocation;
import samueliox.allergycheck.data.Tree;
import samueliox.allergycheck.service.AccuWeatherService;
import samueliox.allergycheck.service.AllergyServiceCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends FragmentActivity implements AllergyServiceCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, CityLocationListener {

//    private ImageView weatherIconImageView;
    private TextView treeTextView, aqConditionTextView, treeConditionTextView, allergyTextView,
        locationTextView, stateTextView;
    private AccuWeatherService service;
    private EditText cityEditTextView;
    private String cityLocation;
    private Location userLocation;
    private ProgressDialog dialog;
    private ArrayList<String> cityList;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mCityLocationRequest;
    public static final String TAG = MainActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog = new ProgressDialog(this);
        showLoadingDialog();
        allergyTextView = (TextView)findViewById(R.id.allergyTextView);
        treeTextView = (TextView)findViewById(R.id.treeView);
        aqConditionTextView = (TextView)findViewById(R.id.aqContidionTextView);
        treeConditionTextView = (TextView)findViewById(R.id.treeConditionTextView);
        locationTextView = (TextView)findViewById(R.id.locationTextView);
        stateTextView = (TextView)findViewById(R.id.stateTextView);
        cityEditTextView = (EditText)findViewById(R.id.cityLocationInput);
        Button search = (Button)findViewById(R.id.searchCityButton);
        Button userLocationButton = (Button)findViewById(R.id.userLocationButton);

        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mCityLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        userLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        cityList = new ArrayList<String>();
        service = new AccuWeatherService(this);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCityLocation();
                refreshCityList();
                searchCity(v);
            }
        });

        userLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service.refreshInitialCityByValues(userLocation.getLongitude(), userLocation.getLatitude());
            }
        });

        hideLoadingDialog();

//        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

//        add current location functionality
        //sends default start


        //search for key with lat long
//        service.refreshInitialCityByValues(location.getLongitude(), location.getLatitude());
//        refresh(cityLocation, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        System.out.println("connected");
        userLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    public void setCityLocation(){
        cityLocation = cityEditTextView.getText().toString();
    }

    public String getCityLocation(){
        return cityLocation;
    }

    public void setCityList(ArrayList<String> a){
        this.cityList = a;
    }
    public void refreshCityList(){
        service.refreshCityList(getCityLocation());
    }

    public ArrayList<String> getCityList(){
        return cityList;
    }

    public void searchCity(View v){
        CitylistFragment cityList = new CitylistFragment();
        cityList.show(getSupportFragmentManager(), "my_dialog");
//        dialog.hide();
    }

    public void refresh(String location, int cityIndex){
//        dialog.setMessage("Loading...");
//        dialog.show();
        service.refreshCityLocation(location, cityIndex);
    }

    public void serviceSuccess(Tree tree) {
//        dialog.hide();
//        Item item = channel.getItem();
//        int resourceId = getResources().getIdentifier("drawable/icon_" +][\'
//        ''
//        @SuppressWarnings("deprecation")
//        Drawable weatherIconDrawable = getResources().getDrawable(resourceId);
//        weatherIconImageView.setImageDrawable(weatherIconDrawable);
//        temperatureTextView.setText(item.getCondition().getTemperature() + "\u00B0" + channel.getUnits().getTemperature());
//        treeTextView.setText(tree.getName());
        treeConditionTextView.setText(tree.getCategory());
//        locationTextView.setText(service.getCityLocation());
    }
    public void serviceSuccess(CityLocation location) {
//        dialog.hide();
//        Item item = channel.getItem();
//        int resourceId = getResources().getIdentifier("drawable/icon_" +][\'
//        ''
//        @SuppressWarnings("deprecation")
//        Drawable weatherIconDrawable = getResources().getDrawable(resourceId);
//        weatherIconImageView.setImageDrawable(weatherIconDrawable);
//        temperatureTextView.setText(item.getCondition().getTemperature() + "\u00B0" + channel.getUnits().getTemperature());
        locationTextView.setText(location.getCity());
        stateTextView.setText(location.getState());
//        treeConditionTextView.setText(tree.getCategory());
//        locationTextView.setText(service.getCityLocation());
    }
    @Override
    public void serviceSuccess(AirQuality airQuality) {
//        allergyTextView.setText(airQuality.getName());
        aqConditionTextView.setText(airQuality.getCategory());
//        dialog.hide();
    }

    @Override
    public void serviceFailure(Exception e) {
//        dialog.hide();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateCityLocation(String city){
        locationTextView.setText(city);
    }

    @Override
    public void showLoadingDialog(){
        dialog.setMessage("Loading...");
        dialog.show();
    }

    @Override
    public void hideLoadingDialog(){
        dialog.hide();
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        userLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        service.refreshInitialCityByValues(userLocation.getLongitude(), userLocation.getLatitude());
        if (userLocation == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mCityLocationRequest, this);
        }
        else {
            handleNewLocation(userLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
    }
}

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
import samueliox.allergycheck.data.Grass;
import samueliox.allergycheck.data.Ragweed;
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
        locationTextView, stateTextView, countryTextView, grassTextView, grassConditionTextView,
        ragweedTextView, ragweedConditionTextView;
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
        //set loading dialog
        dialog = new ProgressDialog(this);
        showLoadingDialog();
        //sets all the assets
        allergyTextView = (TextView)findViewById(R.id.allergyTextView);
        treeTextView = (TextView)findViewById(R.id.treeView);
        countryTextView = (TextView)findViewById(R.id.countryTextView);
        aqConditionTextView = (TextView)findViewById(R.id.aqContidionTextView);
        treeConditionTextView = (TextView)findViewById(R.id.treeConditionTextView);
        grassTextView = (TextView)findViewById(R.id.grassTextView);
        grassConditionTextView = (TextView)findViewById(R.id.grassConditionTextView);
        ragweedTextView = (TextView)findViewById(R.id.ragweedTextView);
        ragweedConditionTextView = (TextView)findViewById(R.id.ragweedConditionTextView);

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
                refreshCityList();
            }
        });

        userLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                service.refreshInitialCityByValues(userLocation.getLongitude(), userLocation.getLatitude());
            }
        });
        hideLoadingDialog();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
//        userLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
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
        setCityLocation();
        service.refreshCityList(getCityLocation());
    }

    public ArrayList<String> getCityList(){
        return cityList;
    }

    public void searchCity(){
        CitylistFragment cityList = new CitylistFragment();
        cityList.show(getSupportFragmentManager(), "my_dialog");
    }

    public void refresh(String location, int cityIndex){
        service.refreshCityLocation(location, cityIndex);
    }

    public void serviceSuccess(Tree tree) {
        treeConditionTextView.setText(tree.getCategory());
    }

    public void serviceSuccess(Grass g) {
        grassConditionTextView.setText(g.getCategory());
    }

    public void serviceSuccess(Ragweed r) {
        ragweedConditionTextView.setText(r.getCategory());
    }

    public void serviceSuccess(CityLocation location) {
        locationTextView.setText(location.getCity());
        stateTextView.setText(location.getState());
        countryTextView.setText(location.getCountry());

    }
    @Override
    public void serviceSuccess(AirQuality airQuality) {
        aqConditionTextView.setText(airQuality.getCategory());
    }

    @Override
    public void serviceFailure(Exception e) {
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

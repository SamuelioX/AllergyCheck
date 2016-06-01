package samueliox.allergycheck;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.DialogFragment;

import samueliox.allergycheck.data.AirQuality;
import samueliox.allergycheck.data.Location;
import samueliox.allergycheck.data.Tree;
import samueliox.allergycheck.service.AccuWeatherService;
import samueliox.allergycheck.service.AllergyServiceCallback;

public class MainActivity extends FragmentActivity implements AllergyServiceCallback, LocationListener {

//    private ImageView weatherIconImageView;
    private TextView treeTextView, aqConditionTextView, treeConditionTextView, allergyTextView, locationTextView;
    private ListView cityListView;
    private AccuWeatherService service;
    private EditText cityEditTextView;
    private String location;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        allergyTextView = (TextView)findViewById(R.id.allergyTextView);
        treeTextView = (TextView)findViewById(R.id.treeView);
        aqConditionTextView = (TextView)findViewById(R.id.aqContidionTextView);
        treeConditionTextView = (TextView)findViewById(R.id.treeConditionTextView);
        locationTextView = (TextView)findViewById(R.id.locationTextView);
        cityEditTextView = (EditText)findViewById(R.id.cityLocationInput);
        Button search = (Button)findViewById(R.id.searchCityButton);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginMethod(v);
            }
        });
        service = new AccuWeatherService(this);
        dialog = new ProgressDialog(this);
//        add current location functionality
        refresh(location);
    }


    public void loginMethod(View v){
        CitylistFragment cityList = new CitylistFragment();
        cityList.show(getSupportFragmentManager(), "my_dialog");
    }

    public void refresh(String location){
        dialog.setMessage("Loading...");
        dialog.show();
        service.refreshLocation(location);
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
        treeTextView.setText(tree.getName());
        treeConditionTextView.setText(tree.getCategory());
//        locationTextView.setText(service.getLocation());
    }
    public void serviceSuccess(Location location) {
//        dialog.hide();
//        Item item = channel.getItem();
//        int resourceId = getResources().getIdentifier("drawable/icon_" +][\'
//        ''
//        @SuppressWarnings("deprecation")
//        Drawable weatherIconDrawable = getResources().getDrawable(resourceId);
//        weatherIconImageView.setImageDrawable(weatherIconDrawable);
//        temperatureTextView.setText(item.getCondition().getTemperature() + "\u00B0" + channel.getUnits().getTemperature());
        locationTextView.setText(location.getCity());
//        treeConditionTextView.setText(tree.getCategory());
//        locationTextView.setText(service.getLocation());
    }
    @Override
    public void serviceSuccess(AirQuality airQuality) {
        allergyTextView.setText(airQuality.getName());
        aqConditionTextView.setText(airQuality.getCategory());
        dialog.hide();
    }

    @Override
    public void serviceFailure(Exception e) {
//        dialog.hide();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateLocation(String city){

        locationTextView.setText(city);
//        BottomSectionFragment bottomFragment = (BottomSectionFragment)getSupportFragmentManager().findFragmentById(R.id.fragment2);
//        bottomFragment.setMemeText(top, bottom);
    }
}

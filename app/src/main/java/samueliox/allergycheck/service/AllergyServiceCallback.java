package samueliox.allergycheck.service;

import android.view.View;

import java.util.ArrayList;

import samueliox.allergycheck.data.AirQuality;
import samueliox.allergycheck.data.CityLocation;
import samueliox.allergycheck.data.Grass;
import samueliox.allergycheck.data.Ragweed;
import samueliox.allergycheck.data.Tree;

/**
 * @author samuel
 * @version 5/26/2016
 */
    public interface AllergyServiceCallback {
    void serviceSuccess(Tree tree);
    void serviceSuccess(AirQuality airQuality);
    void serviceSuccess(CityLocation location);
    void serviceSuccess(Ragweed weed);
    void serviceSuccess(Grass grass);
    void setCityList(ArrayList<String> a);
    void serviceFailure(Exception e);
    void searchCity();
    void showLoadingDialog();
    void hideLoadingDialog();
}

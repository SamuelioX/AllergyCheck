package samueliox.allergycheck;

import java.util.ArrayList;

/**
 * @author samuel
 * @version 5/30/2016
 */
public interface CityLocationListener {
    public void updateCityLocation(String city);
    public void refresh(String location, int cityIndex);
    public ArrayList<String> getCityList();
    public String getCityLocation();
    public void refreshCityList();
    void showLoadingDialog();
    void hideLoadingDialog();
}

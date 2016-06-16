package samueliox.allergycheck.service;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
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
public class AccuWeatherService {

    private AllergyServiceCallback callback;
    private String location, locationKey;
    private CityLocation locationJSON;
    private Exception error;

    public AccuWeatherService(AllergyServiceCallback callback){
        this.callback = callback;
    }

    public String getCityLocation(){
        return location;
    }

    public String getCityLocationKey(){
        return locationKey;
    }

    public void refreshCityList(String l){
        new AsyncTask<String, Void, Void>(){
            @Override
            protected void onPreExecute() {
                callback.showLoadingDialog();
            }

            @Override
            protected Void doInBackground(String... strings){
                String location = strings[0];
                ArrayList<String> cityList = new ArrayList<>();
                String city = replaceSpaces(location);
                String cityEndpoint = "http://dataservice.accuweather.com/locations/v1/" +
                        "cities/autocomplete?apikey=Olfi1F5YOGygHV3B3lReTkCgGN1UBuHl&q=" + city;
                String JSONQuery = getJSONQuery(cityEndpoint);

                //search for key location
                try{
                    //this query defaults at the top of the array, needs to let user select the correct city
                    JSONArray data = new JSONArray(JSONQuery);
                    for(int i = 0; i < data.length(); i++){
                        cityList.add(data.optJSONObject(i).optString("LocalizedName") + ", " +
                                data.optJSONObject(i).optJSONObject("AdministrativeArea").optString("LocalizedName") + " "
                                + data.optJSONObject(i).optJSONObject("Country").optString("LocalizedName"));
                    }
                } catch (JSONException e){
                    error = e;
                }
                callback.setCityList(cityList);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(error != null){
                    callback.serviceFailure(error);
                    return;
                }
                callback.searchCity();
                callback.hideLoadingDialog();
            }
        }.execute(l);
    }

    public void refreshInitialCityByValues(double lat, double lng){
        locationJSON = new CityLocation();
        final double  latit = lat;
        final double longit = lng;
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String endpoint = "http://dataservice.accuweather.com/locations/v1/cities/geoposition/search" +
                        "?apikey=Olfi1F5YOGygHV3B3lReTkCgGN1UBuHl&q=" +
                        longit + "%2C%20" + latit;
                String keyCityLocationQuery;
                keyCityLocationQuery = getJSONQuery(endpoint);
                try  {
                    //this query defaults at the top of the array, needs to let user select the correct city
                    JSONObject data = new JSONObject(keyCityLocationQuery);
                    locationJSON.populate(data);
                    locationKey = locationJSON.getKey();
                    //set the location key right here into the string
                }

                catch(JSONException e) {
                    e.printStackTrace();
                }

                String weatherEndpoint = String.format("http://dataservice.accuweather.com/forecasts/v1/daily/1day/"
                        + locationKey + "?apikey=Olfi1F5YOGygHV3B3lReTkCgGN1UBuHl&details=true");
                String result = getJSONQuery(weatherEndpoint);
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                if(s == null || error != null){
                    callback.serviceFailure(error);
                    return;
                }

                try {
                    //gets the json object
                    JSONObject data = new JSONObject(s);
                    //searches the json object daily forecast array
                    JSONArray queryResults = data.optJSONArray("DailyForecasts");

                    //searches the json object airandpollen array
                    JSONObject object = queryResults.optJSONObject(0);
                    JSONArray airAndPollen = object.optJSONArray("AirAndPollen");

                    //air quality always first
                    AirQuality airQuality = new AirQuality();
                    airQuality.populate(airAndPollen.optJSONObject(0));

                    //search for value with tree in it in JSON array
                    int treeIndex = 1;
                    boolean found = false;
                    while(treeIndex < airAndPollen.length() && !found){
                        if(airAndPollen.optJSONObject(treeIndex).optString("Name").equals("Tree")){
                            found = true;
                        } else {
                            treeIndex++;
                        }
                    }

                    Tree tree = new Tree();
                    tree.populate(airAndPollen.optJSONObject(treeIndex));
                    callback.serviceSuccess(tree);
                    callback.serviceSuccess(airQuality);
                    callback.serviceSuccess(locationJSON);
                    callback.hideLoadingDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onPreExecute() {
                callback.showLoadingDialog();
            }
        }.execute();
    }

    public void refreshCityLocation(String l, int cityIndex){
        this.location = replaceSpaces(l);
        locationJSON = new CityLocation();
        final int ctyIdx = cityIndex;
        new AsyncTask<String, Void, String>(){
            @Override
            protected void onPreExecute() {
                callback.showLoadingDialog();
            }

            @Override
            protected String doInBackground(String... strings){
                String city = replaceSpaces(strings[0]);
                //checks if the string is empty, defaults to Seattle
//                if(strings[0] == "" || strings[0] == null){
//                    city = "Honolulu";
//                }
                String cityEndpoint = "http://dataservice.accuweather.com/locations/v1/" +
                        "cities/autocomplete?apikey=Olfi1F5YOGygHV3B3lReTkCgGN1UBuHl&q=" + city;
//                saves the query
                String keyCityLocationQuery;
                //gets the key location from the city typed in
                keyCityLocationQuery = getJSONQuery(cityEndpoint);
                //search for key location
                try{
                    //this query defaults at the top of the array, needs to let user select the correct city
                    JSONArray data = new JSONArray(keyCityLocationQuery);
                    //get the correct city array int
                    JSONObject key = data.optJSONObject(ctyIdx);
                    locationJSON.populate(key);
                    locationKey = locationJSON.getKey();
                    //set the location key right here into the string
                } catch (JSONException e){

                }
                String weatherEndpoint = String.format("http://dataservice.accuweather.com/forecasts/v1/daily/1day/"
                        + locationKey + "?apikey=Olfi1F5YOGygHV3B3lReTkCgGN1UBuHl&details=true");
                String result = getJSONQuery(weatherEndpoint);
                return result;
            }

            @Override
            protected void onPostExecute(String s){
                if(s == null || error != null){
                    callback.serviceFailure(error);
                    return;
                }

                try {
                    //gets the json object
                    JSONObject data = new JSONObject(s);
                    //searches the json object daily forecast array
                    JSONArray queryResults = data.optJSONArray("DailyForecasts");

                    //searches the json object airandpollen array
                    JSONObject object = queryResults.optJSONObject(0);
                    JSONArray airAndPollen = object.optJSONArray("AirAndPollen");

                    //air quality always first
                    AirQuality airQuality = new AirQuality();
                    airQuality.populate(airAndPollen.optJSONObject(0));

                    //search for value with tree in it in JSON array
                    int treeIndex = 1;
                    boolean found = false;
                    while(treeIndex < airAndPollen.length() && !found){
                        if(airAndPollen.optJSONObject(treeIndex).optString("Name").equals("Tree")){
                            found = true;
                        } else {
                            treeIndex++;
                        }
                    }

                    Tree tree = new Tree();
                    tree.populate(airAndPollen.optJSONObject(treeIndex));

                    Ragweed weed = new Ragweed();
                    weed.populate(airAndPollen.optJSONObject(treeIndex));

                    Grass grass = new Grass();
                    grass.populate(airAndPollen.optJSONObject(treeIndex));


                    callback.serviceSuccess(tree);
                    callback.serviceSuccess(airQuality);
                    callback.serviceSuccess(grass);
                    callback.serviceSuccess(weed);
                    callback.serviceSuccess(locationJSON);
                    callback.hideLoadingDialog();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(location);
    }
    //method that takes a url and turns it into JSON string
    private String getJSONQuery(String endpoint) {
        StringBuilder result = new StringBuilder();
        try {
            //gets the URL and parses JSON data
            URL url = new URL(endpoint);
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while(((line = reader.readLine()) != null)){
                result.append(line);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return result.toString();
    }

    public String replaceSpaces(String s){
        StringBuilder sb = new StringBuilder();
        if(s == null){
            return "";
        }
        //checking for spaces except for the last character
        for(int i = 0; i < s.length()-1; i++){
            char c = (s.charAt(i));
            if(c != ' ') {
                sb.append(c);
            } else {
                sb.append("%20");
            }
        }
        return sb.toString();
    }
}


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

import samueliox.allergycheck.data.AirQuality;
import samueliox.allergycheck.data.Location;
import samueliox.allergycheck.data.Tree;

/**
 * @author samuel
 * @version 5/26/2016
 */
public class AccuWeatherService {

    private AllergyServiceCallback callback;
    private String location, locationKey;
    private Location locationJSON;
    private Exception error;

    public AccuWeatherService(AllergyServiceCallback callback){
        this.callback = callback;
    }

    public String getLocation(){
        return location;
    }

    public String getLocationKey(){
        return locationKey;
    }

    public void refreshLocation(String l){
        this.location = replaceSpaces(l);
        locationJSON = new Location();
        new AsyncTask<String, Void, String>(){
            @Override
            protected String doInBackground(String... strings){
                String city = replaceSpaces(strings[0]);
                //checks if the string is empty, defaults to Seattle
                if(strings[0] == "" || strings[0] == null){
                    city = "Honolulu";
                }
                String cityEndpoint = "http://dataservice.accuweather.com/locations/v1/" +
                        "cities/autocomplete?apikey=Olfi1F5YOGygHV3B3lReTkCgGN1UBuHl&q=" + city;
//                saves the query
                String keyLocationQuery;
                //gets the key location from the city typed in
                keyLocationQuery = getKeyLocation(cityEndpoint);
                //search for key location
                try{
                    JSONArray data = new JSONArray(keyLocationQuery);
                    JSONObject key = data.optJSONObject(0);
                    locationJSON.populate(key);
                    locationKey = locationJSON.getKey();
                    //set the location key right here into the string
                } catch (JSONException e){

                }
                String weatherEndpoint = String.format("http://dataservice.accuweather.com/forecasts/v1/daily/1day/"
                        + locationKey + "?apikey=Olfi1F5YOGygHV3B3lReTkCgGN1UBuHl&details=true");
                String result = getKeyLocation(weatherEndpoint);
                return result;
            }

            //method that takes a url and turns it into JSON string
            private String getKeyLocation(String endpoint) {
                try {
                    URL url = new URL(endpoint);
                    URLConnection connection = url.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while(((line = reader.readLine()) != null)){
                        result.append(line);
                    }
                    return result.toString();
                } catch (Exception e){
//                    System.out.println("somethign went wrong");
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s){
                if(s == null || error != null){
                    callback.serviceFailure(error);
                    return;
                }

                try {
                    JSONObject data = new JSONObject(s);
                    JSONArray queryResults = data.optJSONArray("DailyForecasts");
//                    int count = queryResults.optInt("count");
//                    if(count == 0){
//                      //  callback.serviceFailure(new LocationWeatherException("No weather information found for " + location));
//                    }
                    JSONObject object = queryResults.optJSONObject(0);

                    JSONArray airAndPollen = object.optJSONArray("AirAndPollen");
                    Tree tree = new Tree();
                    tree.populate(airAndPollen.optJSONObject(0));
                    callback.serviceSuccess(tree);
                    AirQuality airQuality = new AirQuality();
                    airQuality.populate(airAndPollen.optJSONObject(4));
                    callback.serviceSuccess(airQuality);

                    callback.serviceSuccess(locationJSON);
//

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(location);
    }

    private String replaceSpaces(String s){
        StringBuilder sb = new StringBuilder();
        if(s == null){
            return "";
        }
        for(int i = 0; i < s.length(); i++){
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


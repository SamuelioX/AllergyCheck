package samueliox.allergycheck.data;

import org.json.JSONObject;

/**
 * @author samuel
 * @version 5/29/2016
 */
public class CityLocation implements JSONPopulator {
    private String city, state, key, country;

    public String getKey() {
        return key;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry(){ return country;}

    @Override
    public void populate(JSONObject data) {
        city = data.optString("LocalizedName");
        state = data.optJSONObject("AdministrativeArea").optString("ID");
        country = data.optJSONObject("Country").optString("LocalizedName");
        key = data.optString("Key");
    }
}
package samueliox.allergycheck.data;

import org.json.JSONObject;

/**
 * @author samuel
 * @version 5/29/2016
 */
public class Location implements JSONPopulator {
    private String city, state, key;

    public String getKey() {
        return key;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    @Override
    public void populate(JSONObject data) {
        city = data.optString("LocalizedName");
        state = data.optJSONObject("AdministrativeArea").optString("ID");
        key = data.optString("Key");
    }
}

package samueliox.allergycheck.data;

import org.json.JSONObject;

/**

 * @author samuel
 * @version 5/26/2016
 */
public class AirQuality implements JSONPopulator {
    private String name, category, type;

    @Override
    public void populate(JSONObject data){
        name = data.optString("Name");
        category = data.optString("Category");
        type = data.optString("type");
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public String getName() {

        return name;
    }
}

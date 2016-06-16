package samueliox.allergycheck.data;

import org.json.JSONObject;

import org.json.JSONObject;

/**
 * @author samuel
 * @version 5/26/2016
 */
public class Grass implements JSONPopulator{
    private String name, category;

    public String getName(){
        return name;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public void populate(JSONObject data){
        name = data.optString("Name");
        category = data.optString("Category");
    }
}

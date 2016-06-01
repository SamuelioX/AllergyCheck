package samueliox.allergycheck.service;

import samueliox.allergycheck.data.AirQuality;
import samueliox.allergycheck.data.Location;
import samueliox.allergycheck.data.Tree;

/**
 * @author samuel
 * @version 5/26/2016
 */
    public interface AllergyServiceCallback {
    void serviceSuccess(Tree tree);
    void serviceSuccess(AirQuality airQuality);
    void serviceSuccess(Location location);
    void serviceFailure(Exception e);
}

package samueliox.allergycheck;

/**
 * @author samuel
 * @version 5/30/2016
 */
public interface LocationListener {
    public void updateLocation(String city);
    public void refresh(String location);
}

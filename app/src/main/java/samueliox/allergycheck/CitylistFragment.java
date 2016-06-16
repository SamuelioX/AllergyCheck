package samueliox.allergycheck;

import android.app.Dialog;
import android.content.Context;
//import android.location.LocationListener;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import samueliox.allergycheck.data.CityLocation;

/**
 * Fragment that holds the lists of citys from a JSON script when location key is looked up
 * @author samuel
 * @version 5/29/2016
 */
public class CitylistFragment extends DialogFragment{
    CityLocationListener activityCommander;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try {
            activityCommander = (CityLocationListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString());
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
//        ad.setIcon(R.drawable.ic_launcher);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1);
        ArrayList<String> cityList = activityCommander.getCityList();
        for(int i = 0; i < cityList.size(); i++){
            arrayAdapter.add(cityList.get(i));
        }
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());

        //add items from the array list
//        arrayAdapter.add("Seattle");
//        arrayAdapter.add("Lake Forest Park");
//        arrayAdapter.add("South Park");
//        arrayAdapter.add("Chicago");
//        arrayAdapter.add("Gatti");

        ad.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        ad.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String city = activityCommander.getCityLocation();
                        //needs to get the city from the earlier search
                        activityCommander.updateCityLocation(city);
                        activityCommander.refresh(city, which);
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(getActivity());
                        builderInner.setMessage(arrayAdapter.getItem(which));
                        builderInner.setTitle("Your Selected Item is");
                        builderInner.setPositiveButton(
                                "Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builderInner.show();
                    }
                });
        ad.setTitle("Did you mean...");
        return ad.create();
    }
}

package com.SDH3.VCA;

import android.content.Context;
import android.support.annotation.MainThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Alex on 10/11/2017.
 */


// procedurally apply list of businesses to a list layout by creating a custom ArrayAdapter
public class BusinessAdapter extends ArrayAdapter<Business> {

    /*This is bad design (tight coupling), however, given the not-so-OO nature of the MainActivity class,
    *  keeping a reference to MainActivity is the best way of calling it's specialised methods*/
    private MainActivity mainActivity;

    public BusinessAdapter (Context context, ArrayList<Business> businesses, MainActivity mainActivity) {
        super(context, 0, businesses);
        this.mainActivity = mainActivity;
    }



    // this method dictates how the Business data should be mapped to a layout
    @Override
    public View getView(int position, View convertedView, ViewGroup parent){
        /* getItem is a method from the parent class - the object is essentially
        being fetched from the original arraylist */
        final Business business = getItem(position);
        if (convertedView == null){
            // if the passed view to map is null, initialise it as the list_element layout template
            convertedView = LayoutInflater.from( getContext() ).inflate(R.layout.list_element, parent, false);
        }
        // get references to the widgets in the template layout
        TextView businessName = (TextView) convertedView.findViewById(R.id.item_name);
        Button visitWebsiteButton = (Button) convertedView.findViewById(R.id.visit_business_website);
        Button callBusiness = (Button) convertedView.findViewById(R.id.call_business);

        // if the business does not have a website / phone number on the db, keep the buttons disabled
        businessName.setText(business.getName());
        if (business.getWebsite() != null) {
            visitWebsiteButton.setEnabled(true);
            visitWebsiteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.openWebpage(business.getWebsite());
                }
            });
        }
        if (business.getPhoneNumber() != null) {
            callBusiness.setEnabled(true);
            callBusiness.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.callNumberButtonOnClick(business.getPhoneNumber());
                }
            });
        }

        return convertedView;
    }
}

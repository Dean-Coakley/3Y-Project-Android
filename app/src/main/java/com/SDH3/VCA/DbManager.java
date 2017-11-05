package com.SDH3.VCA;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Alex on 15/10/2017.
 */

public class DbManager {

    // DB tags (other than user-specific ones) are likely to change as the project
    // develops. When using the db, be sure not to hardcode the tags (such as "Patients")
    // into the request. Instead, reference these Strings.
    // This will make it easier to accommodate db changes.     ~Alex
    static final String PATIENT_LONGITUDE_DB_TAG = "long";
    static final String PATIENT_LATITUDE_DB_TAG = "lat";
    static final String PATIENT_AGE_DB_TAG = "age";
    static final String PATIENT_CONDITION_DB_TAG = "condition";
    static final String PATIENT_FIRST_NAME_DB_TAG = "fname";
    static final String PATIENT_LAST_NAME_DB_TAG = "lname";
    static final String CARERS_DB_TAG = "Carers";
    static final String PATIENTS_DB_TAG = "Patients";

    private DatabaseReference patientsDB;

    public DbManager(){
        patientsDB = FirebaseDatabase.getInstance().getReference();
    }


    public boolean setPatientCoordinates(double lat, double lon, String carerName, String patientName){
        boolean valid = false;
        boolean conditions = (
                lat < 90 && lat > -90
                && lon < 90 && lon > -90
                && carerName != null
                && patientName != null
        );

        if (conditions) {
            // when all conditions are met, send off the updates coords to Firebase
            patientsDB.child(CARERS_DB_TAG)
                    .child(carerName)
                    .child(PATIENTS_DB_TAG)
                    .child(patientName)
                    .child(PATIENT_LATITUDE_DB_TAG)
                    .setValue(lat);

            patientsDB.child(CARERS_DB_TAG)
                    .child(carerName)
                    .child(PATIENTS_DB_TAG)
                    .child(patientName)
                    .child(PATIENT_LONGITUDE_DB_TAG)
                    .setValue(lon);

            return true;
        }else return false;
    }

}
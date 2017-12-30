package com.SDH3.VCA;

import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Alex on 15/10/2017.
 */

public class DbManager {

    // DB tags (other than user-specific ones) are likely to change as the project
    // develops. When using the db, be sure not to hardcode the tags (such as "Patients")
    // into the request. Instead, reference these Strings.
    // This will make it easier to accommodate db changes, avoid typos, and will allow query-writing
    // without switching between code and browser to check tags   ~Alex
    static final String PATIENT_LONGITUDE_DB_TAG = "long";
    static final String PATIENT_LATITUDE_DB_TAG = "lat";
    static final String PATIENT_AGE_DB_TAG = "age";
    static final String PATIENT_CONDITION_DB_TAG = "condition";
    static final String PATIENT_CARER_ID_DB_TAG = "carerID";
    static final String PATIENT_FIRST_NAME_DB_TAG = "fname";
    static final String PATIENT_LAST_NAME_DB_TAG = "lname";
    static final String CARERS_DB_TAG = "Carers";
    static final String PATIENTS_DB_TAG = "Patients";
    static final String PATIENTS_FLATTENED_DB_TAG = "patients_flattened";

    //Geofence-related tags
    static final String PATIENT_GEOFENCE_LAT_TAG = "geofenceLat";
    static final String PATIENT_GEOFENCE_LONG_TAG = "geofenceLong";
    static final String PATIENT_GEOFENCE_RADIUS_TAG = "geofenceRadius";

    // business-related tags
    static final String BUSINESSES_DB_TAG = "businesses";
    static final String RESTAURANTS_DB_TAG = "restaurants";
    static final String SHOPPING_DB_TAG = "shopping";
    static final String TAXIS_DB_TAG = "taxis";
    static final String BUSINESS_NAME_DB_TAG = "business_name";
    static final String PHONE_NUMBER_DB_TAG = "phone_number";
    static final String WEBSITE_DB_TAG = "website";

    private DatabaseReference firebaseDB;

    // lists of businesses
    private ArrayList<Business> restaurants;
    private ArrayList<Business> taxis;
    private ArrayList<Business> shopping;

    // this number is used to track if the user's attributes have been fully loaded. ( 0 = ready)
    public static int userAttributeReadyCount= 8;

    //Geofence-related tags
    static final String PATIENT_GEOFENCE_LAT_TAG = "geofenceLat";
    static final String PATIENT_GEOFENCE_LONG_TAG = "geofenceLong";
    static final String PATIENT_GEOFENCE_RADIUS_TAG = "geofenceRadius";

    // this number is used to track if the user's attributes have been fully loaded. ( 0 = ready)
    public static int userAttributeReadyCount= 8;

    public DbManager() {
        firebaseDB = FirebaseDatabase.getInstance().getReference();
    }


    public UserProfile initUser(final UserProfile user, String uID, final MainActivity mainActivity) {

        // fill the user object with all the relevant data, starting with the uID
        user.setuID(uID);

        //get a reference to the user's entry in the database
        DatabaseReference userDBReference = firebaseDB.child(PATIENTS_FLATTENED_DB_TAG).child(uID);

        // instantiate a listener that will be passed new data from the database
        ValueEventListener userDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get the key of the entry that the snapshot is referring to.
                String keyBeingChanged = dataSnapshot.getKey();

                // when this number reaches 0, main will be notified that the user's data is ready
                // depending on what attribute the key is targeting, update the user's data accordingly
                // the switch uses the generic tags that are declared at the top of this class
                // WARNING: all numbers from the database are passed as Longs, so they must be parsed accordingly
                switch (keyBeingChanged) {
                    case PATIENT_AGE_DB_TAG:
                        // you cannot cast directly from Long to int, due to data loss
                        // so, I've convoluted the process. We don't need to worry about data loss.
                        Long age = (Long) dataSnapshot.getValue();
                        String s_age = String.valueOf(age);
                        int i_age = Integer.valueOf(s_age);
                        user.setAge(i_age);
                        break;
                    case PATIENT_CARER_ID_DB_TAG:
                        user.setCARER_ID((String) dataSnapshot.getValue());
                        break;
                    case PATIENT_CONDITION_DB_TAG:
                        user.setCondition((String) dataSnapshot.getValue());
                        break;
                    case PATIENT_LAST_NAME_DB_TAG:
                        user.setLName((String) dataSnapshot.getValue());
                        break;
                    case PATIENT_FIRST_NAME_DB_TAG:
                        user.setFName((String) dataSnapshot.getValue());
                        break;
                    case PATIENT_GEOFENCE_LAT_TAG:
                        user.setGeofenceLatitude((Double) dataSnapshot.getValue());
                        break;
                    case PATIENT_GEOFENCE_LONG_TAG:
                        user.setGeofenceLongitude((Double) dataSnapshot.getValue());
                        break;
                    case PATIENT_GEOFENCE_RADIUS_TAG:
                        user.setGeofenceRadius((Double) dataSnapshot.getValue());
                        break;
                }

                userAttributeReadyCount--;
                if (userAttributeReadyCount == 0){
                    mainActivity.notifyUserDataReady(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                /* To cancel existence, or not to cancel existence: that is the question:
                Whether ‘tis nobler in the mind to suffer
                The slings and arrows of outrageous fortune,
                        Or to take arms against a sea of troubles,
                And by opposing end them? To die: to sleep;
                No more; and by a sleep to say we end
                The heart-ache and the thousand natural shocks
                That flesh is heir to, ‘tis a consummation
                Devoutly to be wish’d. To die, to sleep;
                To sleep: perchance to dream: ay, there’s the rub;
                For in that sleep of death what dreams may come
                When we have shuffled off this mortal coil,
                Must give us pause: there’s the respect
                That makes calamity of so long life;
                For who would bear the whips and scorns of time,
                The oppressor’s wrong, the proud man’s contumely,
                The pangs of despised love, the law’s delay,
                The insolence of office and the spurns
                That patient merit of the unworthy takes,
                        When he himself might his quietus make
                With a bare bodkin? who would fardels bear,
                To grunt and sweat under a weary life,
                But that the dread of something after death,
                The undiscover’d country from whose bourn
                No traveller returns, puzzles the will
                And makes us rather bear those ills we have
                Than fly to others that we know not of?
                        Thus conscience does make cowards of us all;
                And thus the native hue of resolution
                Is sicklied o’er with the pale cast of thought,
                        And enterprises of great pith and moment
                With this regard their currents turn awry,
                        And lose the name of action.—Soft you now!
                        The fair Ophelia! Nymph, in thy orisons
                Be all my sins remember’d. */
            }
        };

        // attach the above listener to all database attributes that we want to keep track of.
        // this method will only be updated once, directly after being called, however
        // continual-updates can also be requested by using 'addValueEventListener' instead.
        userDBReference.child(PATIENT_FIRST_NAME_DB_TAG).addListenerForSingleValueEvent(userDataListener);
        userDBReference.child(PATIENT_LAST_NAME_DB_TAG).addListenerForSingleValueEvent(userDataListener);
        userDBReference.child(PATIENT_AGE_DB_TAG).addListenerForSingleValueEvent(userDataListener);
        userDBReference.child(PATIENT_CONDITION_DB_TAG).addListenerForSingleValueEvent(userDataListener);
        userDBReference.child(PATIENT_CARER_ID_DB_TAG).addListenerForSingleValueEvent(userDataListener);
        userDBReference.child(PATIENT_GEOFENCE_LAT_TAG).addListenerForSingleValueEvent(userDataListener);
        userDBReference.child(PATIENT_GEOFENCE_LONG_TAG).addListenerForSingleValueEvent(userDataListener);
        userDBReference.child(PATIENT_GEOFENCE_RADIUS_TAG).addListenerForSingleValueEvent(userDataListener);

        // cache lists of businesses that are available to the patient
        getBusinesses(uID, TAXIS_DB_TAG);
        getBusinesses(uID, RESTAURANTS_DB_TAG);
        getBusinesses(uID, SHOPPING_DB_TAG);

        return user;
    }

    public boolean setPatientCoordinates(double lat, double lon, String carerUID, String patientUID) {
        boolean valid = false;
        boolean conditions = (
                lat < 90 && lat > -90
                        && lon < 90 && lon > -90
                        && carerUID != null
                        && patientUID != null
        );

        if (conditions) {
            // when all conditions are met, send off the updates coords to Firebase
            firebaseDB.child(PATIENTS_FLATTENED_DB_TAG).child(patientUID).child(PATIENT_LATITUDE_DB_TAG).setValue(lat);
            firebaseDB.child(PATIENTS_FLATTENED_DB_TAG).child(patientUID).child(PATIENT_LONGITUDE_DB_TAG).setValue(lon);
            return true;
        } else return false;
    }

    public ArrayList<Business> getBusinesses(String uID, final String businessType){
        // this method takes a user's uID, to accommodate the possibility of tailoring search-results in the future
        // businessType should be one of the static strings listed above. This will get plugged into the query.

        // if the lists of available businesses haven't been cached, get them
        if (this.restaurants == null || this.taxis == null || this.shopping == null) {
            final ArrayList<Business> businesses = new ArrayList<>();
            DatabaseReference restaurantsReference = firebaseDB.child(BUSINESSES_DB_TAG).child(businessType);
            restaurantsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot entry : dataSnapshot.getChildren()) {
                        Business business = new Business();
                        business.setName((String) entry.child(BUSINESS_NAME_DB_TAG).getValue());
                        business.setPhoneNumber((String) entry.child(PHONE_NUMBER_DB_TAG).getValue());
                        business.setWebsite((String) entry.child(WEBSITE_DB_TAG).getValue());
                        business.setType(businessType);
                        businesses.add(business);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(MainActivity.PACKAGE_NAME,
                            databaseError.getMessage() + "\n" + databaseError.getDetails());
                }
            });

            switch (businessType) {
                case TAXIS_DB_TAG:
                    this.taxis = businesses;
                    break;
                case SHOPPING_DB_TAG:
                    this.shopping = businesses;
                    break;
                case RESTAURANTS_DB_TAG:
                    this.restaurants = businesses;
                    break;
            }

            return businesses;
        }
        else{ // if the lists have been cached, return them instantly without contacting the DB
            ArrayList<Business> businesses = null;
            switch (businessType) {
                case TAXIS_DB_TAG:
                    businesses = this.taxis;
                    break;
                case SHOPPING_DB_TAG:
                    businesses = this.shopping;
                    break;
                case RESTAURANTS_DB_TAG:
                    businesses = this.restaurants;
                    break;
            }
            return businesses;
        }
    }
}
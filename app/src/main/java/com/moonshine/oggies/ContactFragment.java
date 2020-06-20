package com.moonshine.oggies;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moonshine.oggies.models.LocationModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1 ;
    private AutocompleteSupportFragment autocompleteFragment;
    public ContactFragment() {
        // Required empty public constructor

    }
    private FusedLocationProviderClient mFuesedLocationProviderCLient;
    private Marker marcador;
    private LatLng defaultLatLng = new LatLng(9.9333296, -84.0833282);
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private float defaultZoom = 16;
    private AutoCompleteTextView mSearchText;
    private ImageView mGps;
    private Marker mmaker;
    private Map<Marker, Class<List>> allMarkersMap = new HashMap<Marker, Class<List>>();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference referencePlaces;
    private String url="";
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40,-168), new LatLng(71,136));
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        autocompleteFragment = (AutocompleteSupportFragment)this.getChildFragmentManager()
                .findFragmentById(R.id.autocomplete_fragment);
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.input_search);
        autocompleteFragment.setHint("Search New Location");


        mGps = (ImageView) view.findViewById(R.id.ic_gps);

        if (!Places.isInitialized()) {
            Places.initialize(getContext(), "AIzaSyA_Ml0GGg_kynTr8QCdlrezp88Dh7hK9uU");
        }

        autoCompleteTextView.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                isClicked(getContext());
            }
        });

        return view;

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);

        LatLng sydney = new LatLng(9.9333296, -84.0833282);
        mMap.addMarker(new MarkerOptions().position(sydney).title("San José"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, defaultZoom));

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        init();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                onSelectPlace(place);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }
    private void init() {
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length == 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    LatLng sydney = new LatLng(9.9333296, -84.0833282);
                    mMap.addMarker(new MarkerOptions().position(sydney).title("San José"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, defaultZoom));
                    mMap.setMyLocationEnabled(true);

                } else {
                    Toast.makeText(getActivity(), "No se va a mostrar la ubicación actual, por favor activar los permisos.", Toast.LENGTH_LONG ).show();
                    LatLng sydney = new LatLng(9.9333296, -84.0833282);
                    mMap.addMarker(new MarkerOptions().position(sydney).title("San José"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, defaultZoom));
                }

            }
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getActivity(), "Mi ubicación", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getActivity(), "Ir a mi ubicación", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    private void getDeviceLocation(){
        mFuesedLocationProviderCLient = LocationServices.getFusedLocationProviderClient(getContext());
        try {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Task location = mFuesedLocationProviderCLient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Location currentLocation = (Location) task.getResult();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()), defaultZoom));
                    }else{
                        Toast.makeText(getContext(), "Debes habilitar el gps", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (SecurityException e){
            Log.e(TAG,"GetDeviceLocation: Security exception: " + e.getMessage());
        }
    }
    public void isClicked(Context context) {
        //execute our method for searching
        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS,
                Place.Field.OPENING_HOURS,
                Place.Field.RATING,
                Place.Field.PHONE_NUMBER,
                Place.Field.PRICE_LEVEL,
                Place.Field.PHOTO_METADATAS);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setCountry("CR")
                .build(getContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);


    }
    public void onSelectPlace(final Place place){


        String snippet = "Address: " + place.getAddress() + "\n" +
                "Phone Number: " + place.getPhoneNumber() + "\n" +
                "Price Rating: " + place.getRating() + "\n";

        MarkerOptions options = new MarkerOptions()
                .position( place.getLatLng())
                .title( place.getName())
                .snippet(snippet);
        mmaker = mMap.addMarker(options);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), defaultZoom));


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                final View mView = getLayoutInflater().inflate(R.layout.custom_address,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.CustomDialog);
                LayoutInflater inflater = getActivity().getLayoutInflater();
                builder.setView(inflater.inflate(R.layout.custom_address, null));

                TextView mainTitle = mView.findViewById(R.id.title);
                TextView address = mView.findViewById(R.id.address);

                mainTitle.setText(mmaker.getTitle());
                address.setText(mmaker.getSnippet());
                String image = mmaker.getSnippet();
                ImageView imgModal = mView.findViewById(R.id.image);
                Picasso.with(getContext()).load(place.getPhotoMetadatas().get(0).getAttributions()).into(imgModal);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();


                //Cerrar modal
                ImageButton btnCerrar = mView.findViewById(R.id.cerrarModal);
                btnCerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                //Marcar posicion
                ImageButton btnMarcarVisita= mView.findViewById(R.id.favorite);
                btnMarcarVisita.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                       // onCreatePlaces(marker.getTitle(),marker.getPosition().latitude,marker.getPosition().longitude,marker.getSnippet(), finalPointsMuseum);

                    }
                });

                return true;
            }
        });
    }


    public void onCreatePlaces(Place place){
        firebaseDatabase = FirebaseDatabase.getInstance();
        referencePlaces = firebaseDatabase.getReference("places");
        LocationModel location = new LocationModel(place.getId(),
                place.getName(),
                place.getAddress(),
                place.getPhoneNumber(),
                place.getLatLng(),
                place.getOpeningHours(),
                place.getRating(),
                place.getPriceLevel(),
                place.getPhotoMetadatas());
        referencePlaces.child(place.getId()).setValue(location);

    }
    private void loadImageFromUrl(String url) {

    }


}
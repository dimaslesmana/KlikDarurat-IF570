package id.ac.umn.if570.titik.koma.klikdarurat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.chip.Chip;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmergencyServiceLocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmergencyServiceLocationFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng currentLocation = new LatLng(-6.2561671110001, 106.6184075241022);
    private static final int PROXIMITY_RADIUS_METERS = 15000;
    private Chip chipHospital;
    private Chip chipPolice;
    private Chip chipFireStation;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EmergencyServiceLocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EmergencyServiceLocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EmergencyServiceLocationFragment newInstance(String param1, String param2) {
        EmergencyServiceLocationFragment fragment = new EmergencyServiceLocationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            MapsJsonParser mapsJsonParser = new MapsJsonParser();
            List<HashMap<String, String>> mapList = null;
            JSONObject object = null;

            try {
                object = new JSONObject(strings[0]);
                mapList = mapsJsonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("GOOGLE-MAPS", "ParserTask Error");
            }

            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            googleMap.clear();

            googleMap.addMarker(new MarkerOptions()
                    .position(currentLocation)
                    .title("Anda")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            for (int i = 0; i < hashMaps.size(); i++) {
                HashMap<String, String> hashMapList = hashMaps.get(i);
                double lat = Double.parseDouble(hashMapList.get("lat"));
                double lng = Double.parseDouble(hashMapList.get("lng"));
                String name = hashMapList.get("name");
                LatLng latLng = new LatLng(lat, lng);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(name);

                googleMap.addMarker(markerOptions);
            }

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
        }
    }

    private class PlaceTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String data = null;

            try {
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("GOOGLE-MAPS", e.toString());
            }

            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            new ParserTask().execute(s);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency_service_location, container, false);

        this.chipHospital = view.findViewById(R.id.chip_emergency_service_location_hospital);
        this.chipPolice = view.findViewById(R.id.chip_emergency_service_location_police);
        this.chipFireStation = view.findViewById(R.id.chip_emergency_service_location_fire_station);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Check permission
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

//        this.spType.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, placeNameList));
        this.mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_emergency_service_location_map);
        this.chipHospital.setOnClickListener(this);
        this.chipPolice.setOnClickListener(this);
        this.chipFireStation.setOnClickListener(this);

        if (this.mapFragment != null) {
            this.mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.addMarker(new MarkerOptions()
                .position(this.currentLocation)
                .title("Anda")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(this.currentLocation, 80));
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == this.chipHospital.getId()) {
            this.chipHospital.setChecked(true);
            this.chipPolice.setChecked(false);
            this.chipFireStation.setChecked(false);

            new PlaceTask().execute(getNearbySearchUrl("hospital"));
        } else if (viewId == this.chipPolice.getId()) {
            this.chipHospital.setChecked(false);
            this.chipPolice.setChecked(true);
            this.chipFireStation.setChecked(false);

            new PlaceTask().execute(getNearbySearchUrl("police"));
        } else if (viewId == this.chipFireStation.getId()) {
            this.chipHospital.setChecked(false);
            this.chipPolice.setChecked(false);
            this.chipFireStation.setChecked(true);

            new PlaceTask().execute(getNearbySearchUrl("fire_station"));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.googleMap != null) {
            this.googleMap.clear();
        }
    }

    private String downloadUrl(String string) throws IOException {
        URL url = new URL(string);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.connect();

        InputStream stream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        String line = "";

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        String data = builder.toString();
        reader.close();
        stream.close();
        connection.disconnect();
        return data;
    }

    private void getCurrentLocation() {
        Task<Location> task = this.fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions()
                            .position(currentLocation)
                            .title("Anda")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 80));
                }
            }
        });
    }

    private String getNearbySearchUrl(String types) {
        StringBuilder nearbySearchApiUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json");

        nearbySearchApiUrl.append("?location=" + currentLocation.latitude + "," + currentLocation.longitude);
        nearbySearchApiUrl.append("&radius=" + PROXIMITY_RADIUS_METERS);
        nearbySearchApiUrl.append("&types=" + types);
        nearbySearchApiUrl.append("&sensor=true");
        nearbySearchApiUrl.append("&key=" + BuildConfig.MAPS_API_KEY);

        return nearbySearchApiUrl.toString();
    }
}
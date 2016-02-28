package com.example.surjit.mymapsapplication.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.support.v4.app.FragmentManager;

import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.surjit.mymapsapplication.R;
import com.example.surjit.mymapsapplication.helpers.helperDate;
import com.example.surjit.mymapsapplication.helpers.helperGeo;
import com.example.surjit.mymapsapplication.models.Comment;
import com.example.surjit.mymapsapplication.models.Incident;
import com.example.surjit.mymapsapplication.models.constants;
import com.example.surjit.mymapsapplication.sync.ChaukasAPI;
import com.example.surjit.mymapsapplication.sync.DataDownloadListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , menuBottom.OnFragmentInteractionListener {

    private GoogleMap mMap;
    private Marker marker;

    long LOCATION_REFRESH_TIME=1000;
    float LOCATION_REFRESH_DISTANCE=1;
    float ZOOM_LEVEL=10;
    float MIN_DISTANCE_FOR_REFRESH=25 ; //meter;
    int NUMBER_OF_DAYS=1;
    public LatLng mapCenter;



    public final static String apiURL = "http://192.168.1.71:3000/api/incidents";
    // public final static String apiURL = "https://chaukas.herokuapp.com/api/incidents";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnCameraChangeListener(new myCameraChangeListner());
        subscribeToOnCurrentLocationChanged();
        mMap.setInfoWindowAdapter(new ChaukasInfoWindowAdapter());

    }

    private void subscribeToOnCurrentLocationChanged(){
        LocationManager locationManager;
        locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);
        myLocationListener mLocationListener=new myLocationListener();

        if(locationManager!=null && getPackageManager().checkPermission("android.permission.ACCESS_FINE_LOCATION",getPackageName())== PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, mLocationListener);
        }

    }

    private void loadIncidents(boolean ignoreDistance){
        ChaukasAPI chaukasAPI = new ChaukasAPI((ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE));

        Log.d(constants.TAG_MapsActivity,"API Loading =" +Boolean.toString(  ChaukasAPI.loading));

        if(chaukasAPI.isConnected() && !ChaukasAPI.loading){

            LatLngBounds viewPort= mMap.getProjection().getVisibleRegion().latLngBounds;
            if(viewPort!=null){
                if(mapCenter!=null && !ignoreDistance){
                    //Calculate distance
                   if( helperGeo.calculateDistance(mapCenter,viewPort.getCenter())>MIN_DISTANCE_FOR_REFRESH){
                        mapCenter=viewPort.getCenter();
                        callChaukasAPI(chaukasAPI,viewPort);
                   }
                }
                else {
                    mapCenter = viewPort.getCenter();
                    callChaukasAPI(chaukasAPI,viewPort);
                }
            }
        }
        else{
            //TODO: error message NO connectivity
        }

        chaukasAPI.setDataDownloadListener(new DataDownloadListener() {
            @Override
            public void dataDownloadedSuccessfully(ArrayList<Incident> data) {
                Log.d(constants.TAG_MapsActivity, data.size() + " incidents received");
                mergeIncidents(data);
            }

            @Override
            public void dataDownloadFailed(String errorMsg) {
                // handler failure (e.g network not available etc.)
                //TODO: show error message
                Log.e(constants.TAG_MapsActivity, errorMsg);
            }
        });
    }

    private void callChaukasAPI(ChaukasAPI chaukasAPI, LatLngBounds viewPort){
        //Query String format is ?sw=swLng,swLat&ne=neLng,neLat&filter=UTCStartDate,UTCEndDate
        String queryString="?sw=" + viewPort.southwest.longitude + "," + viewPort.southwest.latitude +
                "&ne=" + viewPort.northeast.longitude + "," + viewPort.northeast.latitude +
                "&filter=" + helperDate.GetPastUTCDateTimeAsString(NUMBER_OF_DAYS) + "," + helperDate.GetCurrentUTCDateTimeAsString();
        chaukasAPI.execute(apiURL + queryString);
    }

    //region Manage Map Markers
    HashMap<String, IncidentMarker> markerIncidents = new HashMap<String, IncidentMarker>();

    private void mergeIncidents(ArrayList<Incident> incidentList){
        for(Incident incident:incidentList){
            addNewIncident(incident);
        }

        for(String incidentId: markerIncidents.keySet()){
            boolean found=false;
            for(Incident incident:incidentList){
                if(incident.getId().equals(incidentId)){
                    found=true;
                    break;
                }
            }
            markerIncidents.get(incidentId).getMarker().setVisible(found);
        }
    }
    private void addNewIncident(Incident incident){
        LatLng loc=new LatLng(incident.getLocation().getLatitude(), incident.getLocation().getLongitude());
        if(!markerIncidents.keySet().contains(incident.getId())) {
            markerIncidents.put(incident.getId(),new IncidentMarker(incident, mMap.addMarker(new MarkerOptions()
                    .position(loc)
                    .title(incident.getTitle())
                    .snippet(incident.getId()))));
        }
    }

    private Marker currentLocationMaker;
    private void addCurrentLocationMarker(LatLng lastKnownLatLng){
        int reportIncidentMarkerIcon= R.drawable.report_inc_marker;
        currentLocationMaker= mMap.addMarker(new MarkerOptions()
                .position(lastKnownLatLng)
                .title("Current Location")
                .icon(BitmapDescriptorFactory.fromResource(reportIncidentMarkerIcon))
                .snippet("Current location marked"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLatLng, ZOOM_LEVEL));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLatLng, ZOOM_LEVEL));
    }

    @Override
    public void onFragmentInteraction(int id  ) {
        switch (id){
            case R.id.btnToday:
                NUMBER_OF_DAYS=1;
                break;
            case R.id.btnWeek:
                NUMBER_OF_DAYS=7;
                break;
            case R.id.btnMonth:
                NUMBER_OF_DAYS=30;
                break;
            case R.id.btnYear:
                NUMBER_OF_DAYS=365;
                break;
        }
        loadIncidents(true);
    }

    //endregion

    // region Listeners Classes

    private class myLocationListener implements  LocationListener  {
        @Override
        public void onLocationChanged(final Location location) {

//            Log.d(constants.TAG_MapsActivity,"onLocationChanged");
            double lat = location.getLatitude();
            double lng = location.getLongitude();


            if(currentLocationMaker!=null){
                currentLocationMaker.remove();
            }
            LatLng lastKnownLatLng = new LatLng(lat, lng);
            addCurrentLocationMarker(lastKnownLatLng);

            //Load incidents in the view port
            loadIncidents(false);
        }
        //@Override
        public void onProviderDisabled(String provider) {}

        //@Override
        public void onProviderEnabled(String provider) {}

        //@Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}


    };

    private class myCameraChangeListner implements GoogleMap.OnCameraChangeListener{
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            loadIncidents(false);
//            Log.d(constants.TAG_MapsActivity,"onCameraChange");
        }

    }
    //endregion

    //region Chaukas Info Window
    private class ChaukasInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{
        private View view;

        public ChaukasInfoWindowAdapter() {

//            Log.d(constants.TAG_MapsActivity,"ChaukasInfoWindowAdapter called");
            view = getLayoutInflater().inflate(R.layout.fragment_chaukas_info_window,
                    null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            Log.d(constants.TAG_MapsActivity,"getInfoContents called");
            MapsActivity.this.marker = marker;

            Incident incident = null;

            if (marker.getSnippet() != null && markerIncidents != null && markerIncidents.size() > 0) {
                Log.d(constants.TAG_MapsActivity, "marker ID: " + marker.getSnippet());
                incident = markerIncidents.get(marker.getSnippet()).getIncident();

                final TextView tvTitle = ((TextView) view.findViewById(R.id.tvTitle));
                tvTitle.setText(incident.getTitle());

                final TextView tvDesc = ((TextView) view.findViewById(R.id.tvDesc));
                if (  incident.getDesc()!=null ) {
                    tvDesc.setText(incident.getDesc());
                    tvDesc.setVisibility(View.VISIBLE);
                } else {
                    tvDesc.setVisibility(View.GONE);
                }

                final TextView tvAddress = ((TextView) view.findViewById(R.id.tvAddress));
                if (incident.getAddress() != null ) {
                    tvAddress.setText(incident.getAddress());
                    tvAddress.setVisibility(View.VISIBLE);
                } else {
                    tvAddress.setVisibility(View.GONE);
                }
                final TextView tvDate = ((TextView) view.findViewById(R.id.tvDate));
                if (incident.getDate() != null) {
                    tvDate.setText(incident.getDate() + " " + incident.getReportedBy());
                    tvDate.setVisibility(View.VISIBLE);
                } else {
                    tvDate.setVisibility(View.GONE);
                }
                final TextView tvComments = ((TextView) view.findViewById(R.id.tvComments));
                if (incident.getCommentsLabel() != null) {
                    tvComments.setText(incident.getCommentsLabel());
                    tvComments.setVisibility(View.VISIBLE);
                } else {
                    tvComments.setVisibility(View.GONE);
                }

                final TableLayout tblComments = ((TableLayout) view.findViewById(R.id.tblComments));

                ArrayList<Comment> comments = incident.getComments();
                tblComments.removeAllViews();
                for (Comment comment : comments) {
                    TableRow row = (TableRow) LayoutInflater.from(getApplicationContext()).inflate(R.layout.comment, null);

                    row.setLayoutParams(createCommentRowLayout());
                    String commentText = "<b>" + comment.getUser().getDisplayName() + ": </b>" + comment.getComment();
                    ((TextView) row.findViewById(R.id.tvComment)).setText(Html.fromHtml(commentText));
                    ((TextView) row.findViewById(R.id.tvDate)).setText(comment.getDate());
                    ((TextView) row.findViewById(R.id.tvUserPic)).setText(comment.getUser().getDisplayName().substring(0, 1));

                    tblComments.addView(row);
                }
            }
            return view;

        }

        @Override
        public View getInfoWindow(final Marker marker) {
            Log.d(constants.TAG_MapsActivity,"getInfoWindow called");
            return null;
        }

        public TableLayout.LayoutParams createCommentRowLayout() {
            TableLayout.LayoutParams tableRowParams =
                    new TableLayout.LayoutParams
                            (TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);

            int leftMargin = 0;
            int topMargin = 2;
            int rightMargin = 0;
            int bottomMargin = 2;

            tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            return tableRowParams;
        }
    }
    //endregion


    private class IncidentMarker{
        private  Marker marker;
        private Incident incident;

        public IncidentMarker(Incident i,Marker m){
            this.incident=i;
            this.marker=m;
        }
        public Incident getIncident() {
            return incident;
        }

        public void setIncident(Incident incident) {
            this.incident = incident;
        }

        public Marker getMarker() {
            return marker;
        }

        public void setMarker(Marker marker) {
            this.marker = marker;
        }
    }
}

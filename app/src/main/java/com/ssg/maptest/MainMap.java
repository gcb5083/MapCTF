package com.ssg.maptest;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainMap extends FragmentActivity implements OnMapReadyCallback, LocationListener {


    private GoogleMap lmap;

    private double elementlength = 100 / 364567.2;
    private int iter = 13;

    private static final int STROKE_COLOR = 0x999f87af;
    private static final int FILL_COLOR = 0x4488527f;
    private static final int ACTIVE_COLOR = 0x66fcff6c;
    private static final int STROKE_WIDTH = 3;
    private double[] coordinates = new double[2];
    HashMap<LatLng, Polygon> hexagons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        lmap = googleMap;

        coordinates[0] = 40.8;
        coordinates[1] = -77.86;
        int offset = (int) (iter * elementlength * 1.2);
        Log.d("LAT", Double.toString(coordinates[0]));
        Log.d("LONG", Double.toString(coordinates[1]));
        LatLng location = new LatLng(coordinates[0], coordinates[1]);
        hexagons = plotHexMesh(coordinates, 10);
        lmap.moveCamera(CameraUpdateFactory.newLatLng(location));
        lmap.setMaxZoomPreference(18);
        lmap.setMinZoomPreference(14);
    }

    private LatLng getCenter(List<LatLng> points){
        float sumLat = 0;
        float sumLong = 0;
        for ( int i =0; i< points.size(); i++){
            sumLat += points.get(i).latitude;
            sumLong += points.get(i).longitude;
        }

        return new LatLng( sumLat/points.size(), sumLong/points.size());
    }

    private HashMap<LatLng, Polygon> plotHexMesh(double[] city, int iterationnumber) {
//            Log.d("A","Ran plotHexMesh");
        int scalefactor = 1000;
        hexagons = new HashMap<LatLng, Polygon>();
        double[] center;
        Polygon centerhex = lmap.addPolygon(new PolygonOptions().clickable(true).fillColor(ACTIVE_COLOR).
                strokeWidth(STROKE_WIDTH).strokeColor(STROKE_COLOR).add (
                new LatLng(city[0] + elementlength * Math.pow(3, 0.5), city[1] + elementlength),
                new LatLng(city[0], city[1] + elementlength * 2),
                new LatLng(city[0] - elementlength * Math.pow(3, 0.5), city[1] + elementlength),
                new LatLng(city[0] - elementlength * Math.pow(3, 0.5), city[1] - elementlength),
                new LatLng(city[0], city[1] - elementlength * 2),
                new LatLng(city[0] + elementlength * Math.pow(3, 0.5), city[1] - elementlength)));

        hexagons.put(getCenter(centerhex.getPoints()),centerhex);
        for (double shell = 1; shell < iterationnumber; shell ++) {
            for (int side = 0; side < 6; side ++) {
                for (double hexagon = 0; hexagon < shell - 1; hexagon ++) {
                    center = findCenter(city, elementlength * Math.pow(3, 0.5) / 2, shell, side, hexagon);
                    Polygon cityhex = lmap.addPolygon(new PolygonOptions().clickable(true).fillColor(FILL_COLOR).
                            strokeWidth(STROKE_WIDTH).strokeColor(STROKE_COLOR).add(
                            new LatLng(center[0] + elementlength * Math.pow(3, 0.5), center[1] + elementlength),
                            new LatLng(center[0], center[1] + elementlength * 2),
                            new LatLng(center[0] - elementlength * Math.pow(3, 0.5), center[1] + elementlength),
                            new LatLng(center[0] - elementlength * Math.pow(3, 0.5), center[1] - elementlength),
                            new LatLng(center[0], center[1] - elementlength * 2),
                            new LatLng(center[0] + elementlength * Math.pow(3, 0.5), center[1] - elementlength)));
                    hexagons.put(getCenter(cityhex.getPoints()),cityhex);
                }

            }
        }
        return hexagons;

    }

    private double[] findCenter(double[] city, double elementlength, double shell, int side, double hexagon) {
        double[] center = new double[2];
        elementlength = elementlength * 2;
        shell --;
        if (side == 0) {
            center[0] = city[0] + (shell * 2 - hexagon) * elementlength;
            center[1] = city[1] + hexagon * elementlength * Math.pow(3, 0.5);
        }
        else if (side == 1) {
            center[0] = city[0] + (shell - hexagon * 2) * elementlength;
            center[1] = city[1] + shell * elementlength * Math.pow(3, 0.5);
        }
        else if (side == 2) {
            center[0] = city[0] - (shell + hexagon) * elementlength;
            center[1] = city[1] + (shell - hexagon) * elementlength * Math.pow(3, 0.5);
        }
        else if (side == 3) {
            center[0] = city[0] + (hexagon - shell * 2) * elementlength;
            center[1] = city[1] - hexagon * elementlength * Math.pow(3, 0.5);
        }
        else if (side == 4) {
            center[0] = city[0] + (hexagon * 2 - shell) * elementlength;
            center[1] =  city[1] - shell * elementlength * Math.pow(3, 0.5);
        }
        else {
            center[0] = city[0] + (shell + hexagon) * elementlength;
            center[1] = city[1] + (hexagon - shell) * elementlength * Math.pow(3, 0.5);
        }
        return center;
    }

    private double dist_sq(LatLng one, LatLng two){
        return Math.pow(one.latitude - two.latitude, 2) + Math.pow(one.longitude - two.longitude, 2);
    }

    private void setCurrentPolyColored(LatLng latlng){
        //TODO:uncolor previous active one
        Polygon closest = null;
        double min_dist_sq = Integer.MAX_VALUE;
        for( LatLng l : hexagons.keySet()){
//            double dist_sq = l;
            double dist_sq = dist_sq(l, latlng);
            if ( dist_sq < min_dist_sq){
                min_dist_sq = dist_sq;
                closest = hexagons.get(l);
            }
        }

        closest.setFillColor(ACTIVE_COLOR);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("INFO:","onLocationChanged called");
        setCurrentPolyColored(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

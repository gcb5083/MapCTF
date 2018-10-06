package com.ssg.maptest;

import android.content.Context;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

public class MainMap extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap lmap;

    private double elementlength = 0.0025;

    private static final int STROKE_COLOR = 0x999f87af;
    private static final int FILL_COLOR = 0x4488527f;
    private static final int ACTIVE_COLOR = 0x66fcff6c;
    private static final int STROKE_WIDTH = 3;
    private double[] coordinates = new double[2];

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
        Log.d("LAT", Double.toString(coordinates[0]));
        Log.d("LONG", Double.toString(coordinates[1]));
        ArrayList<Polygon> hexagons = plotHexMesh(coordinates, 1000);
        lmap.addPolygon(hexagons);
    }

    private ArrayList<Polygon> plotHexMesh(double[] city, int iterationnumber) {
            int scalefactor = 1000;
            ArrayList<Polygon> cityhexagons = new ArrayList<>();
            double[] center;
            Polygon centerhex = lmap.addPolygon(new PolygonOptions().clickable(true).fillColor(ACTIVE_COLOR).
                    strokeWidth(STROKE_WIDTH).strokeColor(STROKE_COLOR).add (
                    new LatLng(city[0] + elementlength * Math.pow(3, 0.5), city[1] + elementlength),
                    new LatLng(city[0], city[1] + elementlength * 2),
                    new LatLng(city[0] - elementlength * Math.pow(3, 0.5), city[1] + elementlength),
                    new LatLng(city[0] - elementlength * Math.pow(3, 0.5), city[1] - elementlength),
                    new LatLng(city[0], city[1] - elementlength * 2),
                    new LatLng(city[0] + elementlength * Math.pow(3, 0.5), city[1] - elementlength)));
            cityhexagons.add(centerhex);
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
                        cityhexagons.add(cityhex);
                    }
            }
        }
        return cityhexagons;

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
}

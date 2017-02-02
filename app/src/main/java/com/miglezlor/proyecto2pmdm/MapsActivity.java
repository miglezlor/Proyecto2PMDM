package com.miglezlor.proyecto2pmdm;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final int LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    public static double latitud, longitud;
    public static double latitudDestino =42.236812;
    public static double longitudDestino = -8.714332;
    //coordenadas redond 42.279393, -8.612143
    //coordenadas castelao 42.236812, -8.714332
    //coordenadas arenal 42.238983, -8.716033
    
    public static Marker marcador;
    LatLng cCentro = new LatLng(42.237181, -8.714672);
    //circulo vigo 42.236954, -8.712717
    //circulo redond 42.279380, -8.612173
    int rCirc = 130;


    private GoogleApiClient apiClient;
    private static final String LOGTAG = "android-location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //necesario el apiclient para el funcionamiento
        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);

        LatLng LocalizFinal = new LatLng(latitudDestino, longitudDestino);

        marcador = mMap.addMarker(new MarkerOptions().position(LocalizFinal).title("Llega aquí").snippet("Búscate la vida"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LocalizFinal));
        marcador.setVisible(false);
        // Controles UI
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar diálogo explicativo
            } else {
                // Solicitar permiso
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);

        CircleOptions circleOptions = new CircleOptions()
                .center(cCentro)
                .radius(rCirc)
                .strokeColor(Color.parseColor("#00BFFF"))
                .strokeWidth(4)
                .fillColor(Color.argb(32, 33, 150, 243));
        // lo añadimos al mapa
        Circle circle = mMap.addCircle(circleOptions);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            // ¿Permisos asignados?
            if (permissions.length > 0 &&
                    permissions[0].equals(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(this, "Error de permisos", Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {


        Log.e(LOGTAG, "Connection interrupted");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


        Log.e(LOGTAG, "Connection error with Google Play Services");
    }



    @Override
    public void onMapClick(LatLng latLng) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        updateUI(lastLocation);
        calcularDistancia();
    }
    private void updateUI(Location loc) {

        if (loc != null) {
            latitud=loc.getLatitude();
            longitud=loc.getLongitude();
        } else {

            Toast.makeText(this, "Error en latitud y longitud", Toast.LENGTH_LONG).show();

        }


    }
    public void calcularDistancia() {

        double radioTierra = 6372.795477598;

        double dLat = Math.toRadians(latitud- latitudDestino);
        double dLng = Math.toRadians(longitud- longitudDestino);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(latitudDestino)) * Math.cos(Math.toRadians(latitud)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = radioTierra * c;
        double distMet=dist*1000;
        String distancia=String.valueOf(distMet);

        Toast.makeText(this, distancia+" metros de distancia", Toast.LENGTH_LONG).show();

        if(distMet<=30){
            marcador.setVisible(true);
        }else {
            marcador.setVisible(false);
        }

    }
}

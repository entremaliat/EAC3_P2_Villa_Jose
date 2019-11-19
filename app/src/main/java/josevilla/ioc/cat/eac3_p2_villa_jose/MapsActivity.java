package josevilla.ioc.cat.eac3_p2_villa_jose;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //Constants
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private LatLng UBICACIO_PARC = new LatLng(41.214270, 1.726160);
    protected Float ZOOM_INICIAL = 13f;

    //Variables de classe
    private GoogleMap mMap;
    private Location mLastLocation;
    private FusedLocationProviderClient mfusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mfusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    /**
     * Habilita el seguiment d'ubicació
     * Si tenim els permisos corresponents, s'habilita el botó
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_PERMISSION);
        }
    }

    /**
     * Mirem els permisos en temps d'execució
     */
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {

            mfusedLocationClient.getLastLocation().addOnSuccessListener(
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            String missatge;
                            if (location != null) {
                                mLastLocation = location;

                                //Obtenim les coordenades i las posem en un String
                                missatge = getString(R.string.location_text,
                                        mLastLocation.getLatitude(),
                                        mLastLocation.getLongitude());

                            } else {
                                //Preparem el missatge de no ubicació
                                missatge = getString(R.string.no_location);

                            }

                            //Mostra missatge amb les coordenades
                            Toast.makeText(getApplicationContext(), missatge, Toast.LENGTH_SHORT).show();

                        }
                    }
            );
        }

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

        // Afegim una marca al parc
        mMap.addMarker(new MarkerOptions().position(UBICACIO_PARC).title("Parc"));

        // Movem la càmara i el zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UBICACIO_PARC, ZOOM_INICIAL));

        // Indiquem el tipus de mapa a terreny
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        // Habilita la meva ubicació
        enableMyLocation();

        // Posem els controls del zoom
        UiSettings mapUiSettings = mMap.getUiSettings();
        mapUiSettings.setZoomControlsEnabled(true);
        mapUiSettings.setMapToolbarEnabled(true);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        //Verifica si es donen permisos d'ubicació i s'habilita si és el cas
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            }
        }

        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }
}

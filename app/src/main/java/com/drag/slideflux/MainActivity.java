package com.drag.slideflux;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String EXTRA_MESSAGE = "com.drag.slideflux.MESSAGE";
    protected Connection connection;
    private volatile boolean clientConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void scanQR(View view) {
        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
        integrator.initiateScan();
    }

    private boolean validateFields() {
        EditText userName = (EditText)findViewById(R.id.user_name);
        if( userName.getText().toString().length() == 0 ) {
            userName.setError("Your name is required!");
            return false;
        }

        EditText QRCode = (EditText)findViewById(R.id.etQRCode);
        if( QRCode.getText().toString().length() == 0 ) {
            QRCode.setError("Code is required!");
            return false;
        }

        return true;
    }

    private void openConnection() throws Exception
    {
        while (true) {
            AuthPacketProtos.AuthPacket response = connection.receive();

            if (response != null && response.getType() == MessageType.NAME_REQUEST) {
                connection.send(new Message(MessageType.USER_NAME, getUserName()));
            } else if (response != null && response.getType() == MessageType.NAME_ACCEPTED) {
                notifyConnectionStatusChanged(true);
                break;
            } else {
                throw new IOException("Unexpected MessageType");
            }
        }
    }

    public void connectToPresentation(View view) {
        if (validateFields()) {
            EditText userName = (EditText)findViewById(R.id.user_name);
            EditText QRCode = (EditText)findViewById(R.id.etQRCode);

//            tw.setText(R.string.bad_presentation);
            TextView tw = (TextView) findViewById(R.id.etQRCode);
            Intent presentationIntent = new Intent(this, PresentationActivity.class);
            presentationIntent.putExtra(EXTRA_MESSAGE, tw.getText());
            startActivity(presentationIntent);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            TextView tw = (TextView) findViewById(R.id.etQRCode);
            tw.setText(scanResult.getContents());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
/*        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

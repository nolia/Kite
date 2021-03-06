package org.kite.sample;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.kite.wire.Wire;
import org.kite.annotations.Wired;

public class MainActivity extends FragmentActivity {

    private Wire wire;

    @Wired
    public CalcInterface calculator;

    private View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int five = calculator.add(2, 3);
            Toast.makeText(MainActivity.this, " 2 + 3 = " + five, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_add_add_sync)
            .setOnClickListener(mButtonListener);
        startService(new Intent(this, SampleService.class));

        wire = Wire.with(this).from(SampleService.class).to(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        wire.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        wire.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

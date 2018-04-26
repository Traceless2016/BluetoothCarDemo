package com.example.trace.myapplication;

import android.app.FragmentTransaction;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ViewAnimator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "MainActivity";

    // Whether the Log Fragment is currently shown
    private boolean mLogShown;
    private ImageButton Ibtbluetooth,Ibtairplane;
    public static BluetoothChatFragment fragment;
    /*
    声明一个类AFactory，里面有静态变量public static Activity A；在A中调用
AFactory.A = this;
这样在b中就可以直接调用AFactory.A.function();就行了
静态方法进行调用！！
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        if (savedInstanceState == null){
           android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
             fragment = new BluetoothChatFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }
        intiView();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
        logToggle.setVisible(findViewById(R.id.sample_output) instanceof ViewAnimator );
        logToggle.setTitle(mLogShown ? R.string.sample_hide_log : R.string.sample_show_log);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_toggle_log:
                mLogShown = !mLogShown;
                ViewAnimator output = (ViewAnimator) findViewById(R.id.sample_output);
                if (mLogShown) {
                    output.setDisplayedChild(1);
                } else {
                    output.setDisplayedChild(0);
                }
                supportInvalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Create a chain of targets that will receive log data */

    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);


        LogFragment logFragment;
        logFragment = (LogFragment) getFragmentManager().findFragmentById( R.id.log_fragment );
        msgFilter.setNext(logFragment.getLogView());

        Log.i(TAG, "Ready");

    }
    /*
    监听点击事件
     */
    public void intiView(){
        Ibtbluetooth = findViewById( R.id.bluetoothcar2 );
        Ibtairplane = findViewById( R.id.airplane );
        Ibtbluetooth.setOnClickListener( this );
        Ibtairplane.setOnClickListener( this );


    }
    /*
    *点击事件
    *
     */
    public void onClick(View view){
        switch (view.getId()){
            case R.id.bluetoothcar2:
                Toast.makeText( this,"您点击了一个ImageView",Toast.LENGTH_SHORT ).show();
                Intent intent = new Intent(  );
                intent.setClass( this,bluetoothcar.class );
                startActivity( intent );
                break;
            case R.id.airplane:
                Toast.makeText( this,"您点击了airplane",Toast.LENGTH_SHORT ).show();
                Intent intent1 = new Intent(  );
                intent1.setClass( this,airplaneActivity.class );
                startActivity( intent1 );
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

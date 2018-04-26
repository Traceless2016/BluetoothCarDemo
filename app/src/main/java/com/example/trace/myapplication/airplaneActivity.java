package com.example.trace.myapplication;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class airplaneActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private SensorManager manager;
    private String TAG = "MainActivity";
    private ImageView Ibtcompass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
        setContentView( R.layout.activity_airplane );
        initView();
    }
    private void initView(){

        //获取系统服务（SENSOR_SERVICE)返回一个SensorManager 对象
        sensorManager = (SensorManager) getSystemService( Context.SENSOR_SERVICE );
        /*
         * 获取方向传感器
         * 通过SensorManager对象获取相应的Sensor类型的对象
         */
        Sensor magneticSensor = sensorManager.getDefaultSensor( Sensor.TYPE_MAGNETIC_FIELD );
        //应用在前台时候注册监听器
        Sensor accelerometerSensor = sensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
        sensorManager.registerListener( listener,magneticSensor,SensorManager.SENSOR_DELAY_GAME );
        sensorManager.registerListener( listener,accelerometerSensor,SensorManager.SENSOR_DELAY_GAME );
        Ibtcompass =  findViewById( R.id.compass1 );

    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (sensorManager != null){
            sensorManager.unregisterListener( listener );
        }
    }
    private SensorEventListener listener = new SensorEventListener() {
        float[] accelerometerValues = new float[3];
        float[] magneticValues = new float[3];
        private float lastRotateDegree;
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            //判断当前是加速度传感器还是地磁传感器
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                //注意赋值时要调用clone()方法
                accelerometerValues = sensorEvent.values.clone();

            }else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                //注意赋值时要调用clone()方法
                magneticValues = sensorEvent.values.clone();
            }
            float[] R = new float[9];
            float[] values = new float[3];
            SensorManager.getRotationMatrix( R,null,accelerometerValues,magneticValues );
            SensorManager.getOrientation( R, values );
//            Log.d( TAG, "value[0] is " + Math.toDegrees( values[0] ) );
            //将计算出来的旋转角度去翻，用于旋转指南针背景图
            float rotateDegreee = -(float) Math.toDegrees( values[0] );
            if (Math.abs( rotateDegreee - lastRotateDegree )>4){
                RotateAnimation animation = new RotateAnimation( lastRotateDegree, rotateDegreee, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f );
                animation.setFillAfter( true );
                Ibtcompass.startAnimation( animation );
                lastRotateDegree = rotateDegreee;
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };
}

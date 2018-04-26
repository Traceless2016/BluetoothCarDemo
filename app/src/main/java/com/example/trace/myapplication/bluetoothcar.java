package com.example.trace.myapplication;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.*;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class bluetoothcar extends AppCompatActivity implements View.OnClickListener{
    private Button mbtSend;
    private SeekBar seekBar1,seekBar2;
    private TextView tv_seekBar1,tv_seekBar2;
    private ImageView forward2,back2,left2,right2;
    private String TAG = "this is bluetoothcar!";
    private int cntF = 0;
    private int cntR = 0;
    private String leftRightMessage ="L:00S" ,forewardBackMessage = "F:00S";//S is the stop byte
    private String leftRightMessageChar ="L:00S" ,forewardBackMessageChar = "F:00S";
    private BluetoothChatFragment2 fragment;
    private Timer mtimer2 = null;//只执行一次的定时任务！
    private TimerTask mtimer2Task = null;

    private Drawable startBtnBackGround,stopBtnBackGround;
    private boolean mutualFlag =true;

    private boolean pressBtnState = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );//横屏操作
        setContentView( R.layout.activity_bluetoothcar );

        intiView();


    }
    /*
    进行界面的初始化操作！！
     */
    public void intiView(){
        seekBar1 = findViewById( R.id.left_right);
        setSeekBarClickableStop(seekBar1,true);//true 为停止
        seekBar1.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
//                if (Math.abs(leftRight -progress) >= cnt){
//                    Log.e( TAG, "onProgressChanged: leftRight "+progress );
//                    leftRight = progress;//更新leftright的值
//                    String message = "左右"+String.valueOf( progress );
//
//                    if (MainActivity.fragment.mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
//                        MainActivity.fragment.sendMessage(message);
//                    }
//                    else {
//
//                        // TODO: 2018/4/11
//                        // TODO: 2018/4/11 此时没有连接应该进行连接操作
//                    }
//                }
                if(progress > 90){
                    if ((progress -90)<10 ){
                        leftRightMessage = "R:"+"0"+String.valueOf( (progress-90) );

                    }else {
                        leftRightMessage = "R:"+String.valueOf( (progress-90) );
                    }


                    tv_seekBar2.setText("右："+(progress-90)+"°");
                    left2.setAlpha( 0.2f );
                    right2.setAlpha( 1.0f );
                }
                else if (progress == 90){
                    leftRightMessage = "L:00";
                    tv_seekBar2.setText("angle: "+"0"+"°");

                    left2.setAlpha( 0.2f );
                    right2.setAlpha( 0.2f );

                }else {
                    if ((90-progress)<10 ){
                        leftRightMessage = "L:"+"0"+String.valueOf( (90-progress) );

                    }else {
                        leftRightMessage = "L:"+String.valueOf( (90-progress) );
                    }

                    tv_seekBar2.setText("左："+(90-progress)+"°");
                    left2.setAlpha( 1.0f );
                    right2.setAlpha( 0.2f );
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e( TAG, "onStartTrackingTouch: 按住seekbar1" );

            }

            @Override
            /*
            *如果松开进度条则定时器发送终止，因为此时没有改变进度条的值
            * 这样可以减轻手机的负担
             */
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d( TAG, "onStopTrackingTouch: 松开seekbar2" );
                try {
                    // TODO: 2018/4/16 应该使得seekbar回到中间位置 不知此时会不会触发change事件
                    seekBar1.setProgress( 90 );//此时也要更新一下信息的！！虽然控制信息已经更改但是需要发送一下

                }catch (Exception e)
                {
                    // TODO: 2018/4/15 进行异常处理函数
                }

            }
        } );
        seekBar2 = findViewById( R.id.foreward_back );
        setSeekBarClickableStop(seekBar2,true);//true 为停止
        seekBar2.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                if(progress > 90){
                    if ((progress -90)<10 ){
                        forewardBackMessage = "F:"+"0"+String.valueOf( (progress-90) );

                    }else {
                        forewardBackMessage = "F:"+String.valueOf( (progress-90) );
                    }
                    tv_seekBar1.setText( "前："+(progress-90)+"°" );
                    back2.setAlpha( 0.2f );//大于1就表示100%的透明度
                    forward2.setAlpha( 1.0f );//
                }
                else if (progress == 90){
                    forewardBackMessage ="F:00";
                    tv_seekBar1.setText("Speed: "+"0"+"°");
                    back2.setAlpha( 0.2f );
                    forward2.setAlpha( 0.2f );

                }else {
                    if ((90-progress)<10 ){
                        forewardBackMessage = "B:"+"0"+String.valueOf( (90-progress) );
                    }else {
                        forewardBackMessage = "B:"+String.valueOf( (90-progress) );
                    }
                    tv_seekBar1.setText("后："+(90-progress)+"°");
                    back2.setAlpha( 1.0f );
                    forward2.setAlpha( 0.2f );

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d( TAG, "onStartTrackingTouch: 按住seekbar2" );

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                android.util.Log.d( TAG,"松开seekbar2");
                try {
                    seekBar2.setProgress( 90 );//此时也要更新一下信息的！！虽然控制信息已经更改但是需要发送一下


                }catch (Exception e)
                {
                    // TODO: 2018/4/15 进行异常处理函数
                }

            }
        } );
        tv_seekBar1 = findViewById( R.id.tv_seekBar1 );
        tv_seekBar2 = findViewById( R.id. left_right_text);
        forward2 = findViewById( R.id.foreward3 );
        back2 = findViewById( R.id.back3 );
        left2 = findViewById( R.id.left_icon2 );
        right2 =findViewById( R.id.right_icon2 );
        mbtSend = findViewById( R.id.start );
        mbtSend.setOnClickListener( this );
        startBtnBackGround = getDrawable( R.drawable.btn_nor_down_start );

        stopBtnBackGround = getDrawable( R.drawable.btn_nor_down_stop );
    }
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.start:{
                startButton();

                break;
            }
        }
    }
    protected void startButton(){
        if (!pressBtnState){
            mbtSend.setText( R.string.stop );
            mbtSend.setBackground( stopBtnBackGround );
            pressBtnState = true;//改变状态为已经点击
            Log.e( TAG, "startButton: stopBtnBackGround" );
            // TODO: 2018/4/16 启动两个进度条（seekbar）
            setSeekBarClickableStop( seekBar1,!pressBtnState );//false 为启动
            setSeekBarClickableStop( seekBar2,!pressBtnState );//true 为开始
            if (mtimer2Task == null){
                mtimer2Task = new TimerTask() {
                    @Override
                    public void run() {
                        if (mutualFlag){
                            updateLeftRightInfo();
                        }else{
                            updateForwBackInfo();
                        }

                    }
                };
            }

           //发送控制信息 左右
            if (mtimer2 == null){
                mtimer2 = new Timer(  );
                mtimer2.schedule( mtimer2Task,10,30 );
            }

        }else {
            mbtSend.setText( R.string.start );
            mbtSend.setBackground( startBtnBackGround );
            pressBtnState = false;//改变状态为未点击
            Log.e( TAG, "startButton: startBtnBackGround" );
            // TODO: 2018/4/16 禁止两个进度条进行移动
            setSeekBarClickableStop( seekBar1,!pressBtnState );
            setSeekBarClickableStop( seekBar2,!pressBtnState );
            if (mtimer2 != null) {
                mtimer2.cancel();
                mtimer2 = null;
            }
            if (mtimer2Task != null) {
                mtimer2Task.cancel();
                mtimer2Task = null;
            }
        }
    }
    private void setSeekBarClickableStop(SeekBar seekbar,boolean state){
        if (state){//禁止seekbar
            seekbar.setClickable( false );
            seekbar.setEnabled( false );
            seekbar.setSelected( false );
            seekbar.setFocusable( false );
//            Drawable drawable = getDrawable( R.drawable. );
//            seekbar.setThumb(  );//设置按钮状态
            seekbar.setProgress( 90 );//中间位置此时会触发中断响应，更新一下要发送的数组

        }else {
            seekbar.setClickable( true );
            seekbar.setEnabled( true );
            seekbar.setSelected( true );
            seekbar.setFocusable( true );
            seekbar.setProgress( 90 );
        }

    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // 要做的事情
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (mtimer2 != null) {
            mtimer2.cancel();
            mtimer2 = null;
        }
        if (mtimer2Task != null) {
            mtimer2Task.cancel();
            mtimer2Task = null;
        }

    }
    private void updateLeftRightInfo(){
        if (cntR < 5){//这样经常会发生数组越界异常的
            char ch[] = leftRightMessageChar.toCharArray();
            String leftRightMessageChar1;
            if (ch != null) {
                leftRightMessageChar1 = String.valueOf( ch[cntR] );
            }
            else {
                leftRightMessageChar1 = String.valueOf( 'E' );
            }

            if (MainActivity.fragment.mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
                MainActivity.fragment.sendMessage2(leftRightMessageChar1);
                Log.d( TAG, "onProgressChanged: "+leftRightMessageChar1 );
            }
            cntR = cntR+1;
        }else{
            cntR = 0;
            mutualFlag =false;//mutual信号量

            if (leftRightMessageChar.length()<4){//avoid array index out of bounds
                leftRightMessageChar = leftRightMessageChar +"s";
            }
            leftRightMessageChar = leftRightMessage+"S";//进行重装载值

        }

    }
    private void updateForwBackInfo(){
        if (cntF < 5){//这样经常会发生数组越界异常的
            char ch[] = forewardBackMessageChar.toCharArray();
            String forwBackMessageChar1;
            if (ch != null) {
                forwBackMessageChar1 = String.valueOf( ch[cntF] );
            }
            else {
                forwBackMessageChar1 = String.valueOf( 'E' );
            }


            if (MainActivity.fragment.mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
                MainActivity.fragment.sendMessage2(forwBackMessageChar1);
                Log.d( TAG, "onProgressChanged: "+forwBackMessageChar1 );
            }
            cntF = cntF+1;
        }else{
            cntF = 0;//此时的命令发送完成，需要重新开始
            mutualFlag = true;

            if (forewardBackMessageChar.length()<4){//avoid array index out of bounds
                forewardBackMessageChar = forewardBackMessageChar +"s";
            }
            forewardBackMessageChar = forewardBackMessage+"S";

        }

    }

}



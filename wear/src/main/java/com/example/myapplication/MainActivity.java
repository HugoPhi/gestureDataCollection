package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.wear.ambient.AmbientModeSupport;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.example.java2.R;

public class MainActivity extends FragmentActivity
        implements AmbientModeSupport.AmbientCallbackProvider, SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor accelerometer, mGyro;
    private boolean isGyroPresent = false;
    private boolean isAccPresent = false;

    private TextView tdate;
    private Button save, record;
    private Vibrator vibrator;

    private float x, y, z, x_gy, y_gy, z_gy, x_lin, y_lin, z_lin;
    private String x_val, y_val, z_val, xG_val, yG_val, zG_val, a_Mag, g_Mag, activityInput;
    private String modified_DATA = "";
    private String dateCurrent;
    private String dateCurrentTemp = "";
    private int CounterForSave = 0;
    private int SamplingRate;
    private boolean permission_to_record = false;

    private ScalarKalmanFilter[] mFiltersCascade = new ScalarKalmanFilter[3];
    private CountDownTimer mCountDownTimer;
    private TextView mTextViewCountDown;
    private long mTimeLeftInMillis = 4000; // 设置为4秒
    private boolean mTimerRunning;

    double Mag_accel, Mag_gyro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // 注册传感器监听器
        if (accelerometer != null) {
            mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            isAccPresent = true;
        }
        if (mGyro != null) {
            mSensorManager.registerListener(this, mGyro, SensorManager.SENSOR_DELAY_FASTEST);
            isGyroPresent = true;
        }

        // 初始化卡尔曼滤波器
        for (int i = 0; i < 3; i++) {
            mFiltersCascade[i] = new ScalarKalmanFilter(1, 1, 0.01f, 0.0025f);
        }

        // 初始化按钮
        save = findViewById(R.id.Save);
        record = findViewById(R.id.Record);

        // 初始化下拉菜单
        Spinner dropdown = findViewById(R.id.spinner1);
        Spinner dropdown3 = findViewById(R.id.activity);
        String[] items1 = new String[]{"20 dps", "25 dps", "30 dps"};
        String[] items3 = new String[]{"Gesture 1", "Gesture 2", "Gesture 3", "Gesture 4", "Gesture 5", "Gesture 6", "Gesture 7", "Gesture 8", "Gesture 9"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items1);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items3);

        dropdown.setAdapter(adapter);
        dropdown3.setAdapter(adapter3);

        // 设置下拉菜单监听器
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        SamplingRate = 20;
                        break;
                    case 1:
                        SamplingRate = 25;
                        break;
                    case 2:
                        SamplingRate = 30;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        dropdown3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        activityInput = "Gesture 1";
                        break;
                    case 1:
                        activityInput = "Gesture 2";
                        break;
                    case 2:
                        activityInput = "Gesture 3";
                        break;
                    case 3:
                        activityInput = "Gesture 4";
                        break;
                    case 4:
                        activityInput = "Gesture 5";
                        break;
                    case 5:
                        activityInput = "Gesture 6";
                        break;
                    case 6:
                        activityInput = "Gesture 7";
                        break;
                    case 7:
                        activityInput = "Gesture 8";
                        break;
                    case 8:
                        activityInput = "Gesture 9";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 设置按钮点击事件
        record.setOnClickListener(v -> {
            permission_to_record = true;

            if (!"wait".equals(record.getText())) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            vibrator.vibrate(500);
            if (mTimerRunning) {
                pauseTimer();
            } else {
                startTimer();
                Toast.makeText(MainActivity.this, "Touch Screen Disabled", Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "Start Recording...", Toast.LENGTH_SHORT).show();
                record.setBackgroundColor(Color.RED);
            }
        });

        save.setOnClickListener(v -> {
            resetTimer();
            record.setBackgroundColor(Color.DKGRAY);
        });

        updateCountDownText();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss");
        dateCurrent = sdf.format(date);

        if (tdate != null) {
            tdate.setText(dateCurrent);
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            x = filter(event.values[0]);
            y = filter(event.values[1]);
            z = filter(event.values[2]);

            x_val = String.valueOf(x);
            y_val = String.valueOf(y);
            z_val = String.valueOf(z);

            Mag_accel = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
            a_Mag = String.valueOf(Mag_accel);
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            x_gy = filter(event.values[0]);
            y_gy = filter(event.values[1]);
            z_gy = filter(event.values[2]);

            xG_val = String.valueOf(x_gy);
            yG_val = String.valueOf(y_gy);
            zG_val = String.valueOf(z_gy);

            Mag_gyro = Math.sqrt(Math.pow(x_gy, 2) + Math.pow(y_gy, 2) + Math.pow(z_gy, 2));
            g_Mag = String.valueOf(Mag_gyro);
        }

        if (!dateCurrentTemp.equals(dateCurrent)) {
            dateCurrentTemp = dateCurrent;
            CounterForSave = 0;
        }

        if (CounterForSave < SamplingRate && permission_to_record) {
            double alpha = 0.8;
            x_lin = (float) (alpha * x_gy + (1 - alpha) * x);
            y_lin = (float) (alpha * y_gy + (1 - alpha) * y);
            z_lin = (float) (alpha * z_gy + (1 - alpha) * z);

            x_lin = x - x_lin;
            y_lin = y - y_lin;
            z_lin = z - z_lin;

            String DATA = dateCurrent + "," + x_val + "," + y_val + "," + z_val + ","
                                            + xG_val + "," + yG_val + "," + zG_val + ","
                                            + x_lin + "," + y_lin + "," + z_lin + ","
                                            + a_Mag + "," + g_Mag + "," + activityInput + "\n";

            modified_DATA += DATA;
            CounterForSave++;
        }
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                permission_to_record = false;

                save.setBackgroundColor(Color.GREEN);
                record.setBackgroundColor(Color.DKGRAY);
                Toast.makeText(MainActivity.this, "Touch screen enabled", Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "File Created & Saved", Toast.LENGTH_SHORT).show();

                resetTimer();

                // 开启保存数据的线程
                new Thread(mutiThread).start();

                vibrator.vibrate(1500);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                record.setText("Record");
                save.setVisibility(View.INVISIBLE);
            }
        }.start();
        mTimerRunning = true;
        record.setText("wait");
        save.setVisibility(View.INVISIBLE);
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        record.setText("Start");
        save.setVisibility(View.VISIBLE);
    }

    private void resetTimer() {
        mTimeLeftInMillis = 4000; // 重置为初始时间
        updateCountDownText();
        save.setVisibility(View.INVISIBLE);
        record.setVisibility(View.VISIBLE);
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        // 确保mTextViewCountDown不为null
        if (mTextViewCountDown != null) {
            mTextViewCountDown.setText(timeLeftFormatted);
        }
    }

    private Runnable mutiThread = new Runnable() {
        @Override
        public void run() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HH-mm-ss", Locale.getDefault());
            String dateString = sdf.format(System.currentTimeMillis());

            String theFileName = activityInput + ".csv";
            String thePath = "/storage";
            String theData = "DATE,TIME,ax,ay,az,gx,gy,gz,lx,ly,lz,ma,mg,label\n" + modified_DATA;
            InputStream theInput = new ByteArrayInputStream(theData.getBytes());

            File folder = getExternalFilesDir(thePath);
            File gpxfile = new File(folder, theFileName);
            try (FileWriter writer = new FileWriter(gpxfile, true)) {
                writer.write(theData);
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error saving!", Toast.LENGTH_SHORT).show());
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_STEM_1) {
            Toast.makeText(this, "Touch screen enabled", Toast.LENGTH_SHORT).show();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isAccPresent) {
            mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (isGyroPresent) {
            mSensorManager.registerListener(this, mGyro, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isAccPresent) {
            mSensorManager.unregisterListener(this, accelerometer);
        }
        if (isGyroPresent) {
            mSensorManager.unregisterListener(this, mGyro);
        }
    }

    @Override
    public AmbientModeSupport.AmbientCallback getAmbientCallback() {
        return new MyAmbientCallback();
    }

    private class MyAmbientCallback extends AmbientModeSupport.AmbientCallback {
        @Override
        public void onEnterAmbient(Bundle ambientDetails) {
            super.onEnterAmbient(ambientDetails);
        }

        @Override
        public void onUpdateAmbient() {
            super.onUpdateAmbient();
        }

        @Override
        public void onExitAmbient() {
            super.onExitAmbient();
        }
    }

    private float filter(float measurement) {
        float f1 = mFiltersCascade[0].correct(measurement);
        float f2 = mFiltersCascade[1].correct(f1);
        float f3 = mFiltersCascade[2].correct(f2);
        return f3;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 不需要做什么
    }
}

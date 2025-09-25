package com.example.tareaaa_nuevaaa;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final int JOB_ID = 1;
    private TextView textView2;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView2 = findViewById(R.id.textView2);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }


    private BroadcastReceiver airplaneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            executorService.execute(() -> {
                boolean isAirplaneModeOn = intent.getBooleanExtra("state", false);
                String mensaje;
                if (isAirplaneModeOn) {
                    mensaje = "Modo avion activado";
                } else {
                    mensaje = "Modo avion desactivado";
                }

                String finalMensaje = mensaje;
                mainHandler.post(() -> {
                    Toast.makeText(context, finalMensaje, Toast.LENGTH_SHORT).show();
                    textView2.setText(isAirplaneModeOn ? "Activado" : "Desactivado");
                });

                mainHandler.post(() -> {
                    JobInfo jobInfo = getJobInfo(MainActivity.this);
                    JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                    if (scheduler != null) {
                        scheduler.cancel(JOB_ID);
                        int result = scheduler.schedule(jobInfo);
                        if (result == JobScheduler.RESULT_SUCCESS) {
                            Toast.makeText(context, "Job programado al cambiar modo avion", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Fallo al programar el job en el servicio", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            });
        }
    };
    

    private static JobInfo getJobInfo(MainActivity mainActivity) {
        ComponentName componentName = new ComponentName(mainActivity, JobNotificacion.class);
        return new JobInfo.Builder(JOB_ID, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(200)
                .setOverrideDeadline(5000)
                .setPersisted(false)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        registerReceiver(airplaneReceiver, filter);
    }


    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(airplaneReceiver);
    }
}
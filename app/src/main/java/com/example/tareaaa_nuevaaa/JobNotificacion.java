package com.example.tareaaa_nuevaaa;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Looper;
import android.util.Log;
import android.os.Handler;
import android.widget.Toast;


public class JobNotificacion extends JobService {

    @Override
    public boolean onStartJob(final JobParameters params) {
        Thread backgroundThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(3000);
                }catch (InterruptedException e){
                    Log.e("JobNotificacionStatus","Hilo interrumpido, hubo un error",e);
                }
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(JobNotificacion.this, "JobNotificacion", Toast.LENGTH_SHORT).show();
                    }
                });
                jobFinished(params,false);
            }
        });
        backgroundThread.start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}

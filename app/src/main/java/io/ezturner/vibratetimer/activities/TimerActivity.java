package io.ezturner.vibratetimer.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.crashlytics.android.Crashlytics;
import io.ezturner.vibratetimer.R;
import io.ezturner.vibratetimer.services.TimerService;
import io.fabric.sdk.android.Fabric;

public class TimerActivity extends AppCompatActivity {

    private TimerService timerService;

    @BindView(R.id.time_input)
    EditText timerInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_timer);

        ButterKnife.bind(this);

        timerService = TimerService.getInstance();
        if(timerService == null)
            startTimerService();
    }

    private void startTimerService(){
        startService(new Intent(getApplicationContext(), TimerService.class));
    }

    private TimerService getService() {
        TimerService service = TimerService.getInstance();

        if(service == null)
            startTimerService();

        try {
            synchronized (this) {
                this.wait(5);
            }
        } catch (InterruptedException e) {

        }
        service = TimerService.getInstance();


        return service;
    }

    @OnClick(R.id.start_timer)
    public void startTimerClick(){
        timerService = getService();
        timerService.startTimer(getTimerLength());
    }

    /**
     * Returns the length of the timer in seconds
     * @return timerLength
     */
    private int getTimerLength(){
        return Integer.parseInt(timerInput.getText().toString());
    }
}

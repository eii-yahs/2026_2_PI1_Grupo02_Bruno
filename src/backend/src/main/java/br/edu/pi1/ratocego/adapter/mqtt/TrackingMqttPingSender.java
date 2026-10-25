package br.edu.pi1.ratocego.adapter.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPingSender;
import org.eclipse.paho.client.mqttv3.internal.ClientComms;

import java.util.Timer;
import java.util.TimerTask;

public final class TrackingMqttPingSender
        implements MqttPingSender {

    private final MqttActivity activity;

    private ClientComms comms;
    private Timer timer;

    public TrackingMqttPingSender(MqttActivity activity) {
        this.activity = activity;
    }

    @Override
    public void init(ClientComms comms) {
        this.comms = comms;
    }

    @Override
    public synchronized void start() {
        stop();

        timer = new Timer(
                "mqtt-ping-" + comms.getClient().getClientId(),
                true
        );

        schedule(comms.getKeepAlive());
    }

    @Override
    public synchronized void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public synchronized void schedule(long delayInMilliseconds) {
        if (timer == null) {
            return;
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkActivity();
            }
        }, Math.max(1L, delayInMilliseconds));
    }

    private void checkActivity() {
        try {
            comms.checkForActivity(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken token) {
                    activity.receivedControlPacket();
                }

                @Override
                public void onFailure(
                        IMqttToken token,
                        Throwable exception
                ) {
                    // O encerramento da conexão é tratado pelo Paho.
                }
            });
        } catch (MqttException exception) {
            comms.shutdownConnection(null, exception);
        }
    }
}
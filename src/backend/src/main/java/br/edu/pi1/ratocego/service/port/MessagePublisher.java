package br.edu.pi1.ratocego.service.port;

/** Port for application services that publish messages. */
public interface MessagePublisher {

    void publish(String topic, String payload);
}

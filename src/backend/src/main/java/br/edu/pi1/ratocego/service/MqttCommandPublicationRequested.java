package br.edu.pi1.ratocego.service;

public record MqttCommandPublicationRequested(String topic, String payload) {
}

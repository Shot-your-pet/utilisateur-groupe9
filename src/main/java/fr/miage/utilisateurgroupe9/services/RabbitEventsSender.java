package fr.miage.utilisateurgroupe9.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.UUID;

@Service
public class RabbitEventsSender {

    private final RabbitTemplate rabbitTemplate;

    public RabbitEventsSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public record Message<T>(UUID idDemande, UUID idReponse, T data) implements Serializable {}

    public <T> void send(UUID idDemande, UUID idReponse, T data){
        Message message = new Message(idDemande, idReponse, data);
        this.rabbitTemplate.convertAndSend("exchange", "routingkey", message);
    }
}

package fr.miage.utilisateurgroupe9.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RabbitEventsSenderTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitEventsSender rabbitEventsSender;

    @Test
    public void testSendOK() {
        UUID idDemande = UUID.randomUUID();
        UUID idReponse = UUID.randomUUID();
        String data = "grtgtrhytrjdyjt";

        rabbitEventsSender.send(idDemande, idReponse, data);

        ArgumentCaptor<RabbitEventsSender.Message> messageCaptor = ArgumentCaptor.forClass(RabbitEventsSender.Message.class);
        verify(rabbitTemplate).convertAndSend(eq("exchange"), eq("routingkey"), messageCaptor.capture());

        RabbitEventsSender.Message capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.idDemande()).isEqualTo(idDemande);
        assertThat(capturedMessage.idReponse()).isEqualTo(idReponse);
        assertThat(capturedMessage.data()).isEqualTo(data);
    }
}
package ru.nsu.kravchenko.crackhash.centralmanager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.schema.crack_hash_request.CentralManagerRequest;
import ru.nsu.kravchenko.crackhash.centralmanager.model.repository.RequestsRepository;
import ru.nsu.kravchenko.crackhash.centralmanager.model.requeststatus.Request;

import java.util.List;

@Service
@Slf4j
public class RabbitMQProducer implements ConnectionListener {

    private final AmqpTemplate amqpTemplate;

    @Value("${centralManagerService.queue.output}")
    String outputQueue;

    @Autowired
    private RequestsRepository crackTaskRequestRepository;

    public RabbitMQProducer(AmqpTemplate amqpTemplate,
                            ConnectionFactory connectionFactory) {
        this.amqpTemplate = amqpTemplate;
        connectionFactory.addConnectionListener(this);
    }

    public boolean trySendMessage(CentralManagerRequest request) {
        try {
            amqpTemplate.convertAndSend(outputQueue, request);
            log.info("Set {} part of {} task request was sent", request.getPartNumber(), request.getRequestId());
            return true;
        } catch (AmqpException ex) {
            log.error("Failed to send request '{}', cached message", request.getRequestId());
            return false;
        }
    }

    @Override
    public void onCreate(Connection connection) {
        List<Request> requests = crackTaskRequestRepository.findAll();
        for (var request : requests) {
            trySendMessage(request.getRequest());
            crackTaskRequestRepository.delete(request);
        }
    }

}

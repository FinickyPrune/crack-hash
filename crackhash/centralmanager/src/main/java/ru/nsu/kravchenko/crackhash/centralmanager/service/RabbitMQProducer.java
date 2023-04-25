package ru.nsu.kravchenko.crackhash.centralmanager.service;

import com.rabbitmq.client.ShutdownSignalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageDeliveryMode;
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

    @Value("${worker.input.exchange}")
    private String outputExchange;
    @Value("${worker.input.routing}")
    private String outputRouting;
    @Autowired
    private RequestsRepository crackTaskRequestRepository;

    public RabbitMQProducer(AmqpTemplate amqpTemplate,
                            ConnectionFactory connectionFactory) {
        this.amqpTemplate = amqpTemplate;
        connectionFactory.addConnectionListener(this);
    }

    public boolean trySendMessage(CentralManagerRequest request) {
        try {
            amqpTemplate.convertAndSend(outputExchange, outputRouting, request, message -> {
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message;
            });

            log.info("Set {} part of {} task request was sent", request.getPartNumber(), request.getRequestId());
            return true;
        } catch (AmqpException ex) {
            log.error("Failed to send request '{}'", request.getRequestId());
            return false;
        }
    }

    @Override
    public void onCreate(Connection connection) {
        crackTaskRequestRepository.findAll().forEach(request -> {
            trySendMessage(request.getRequest());
        });
    }

    @Override
    public void onClose(Connection connection) {
        log.debug("Connection closed {}", connection.toString());
    }

    @Override
    public void onShutDown(ShutdownSignalException signal) {
        log.debug("Connection shutdown {}", signal.getReason());
    }

}

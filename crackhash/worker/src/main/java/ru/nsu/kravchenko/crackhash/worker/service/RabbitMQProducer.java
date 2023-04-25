package ru.nsu.kravchenko.crackhash.worker.service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ShutdownSignalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.schema.crack_hash_response.WorkerResponse;

import java.io.IOException;

@Service
@Slf4j
public class RabbitMQProducer {

    private final AmqpTemplate amqpTemplate;

    @Value("${centralManagerService.input.exchange}")
    private String outputExchange;
    @Value("${centralManagerService.input.routing}")
    private String outputRouting;

    public RabbitMQProducer(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public boolean produce(WorkerResponse response) {
        try {
            amqpTemplate.convertAndSend(outputExchange, outputRouting, response, message -> {
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message;
            });
            log.info("Sent [{}] part of [{}] task.", response.getPartNumber(), response.getRequestId());
            return true;
        } catch (AmqpException ex) {
            log.error("Failed to send request [{}]", response.getRequestId());
            return false;
        }
    }

}

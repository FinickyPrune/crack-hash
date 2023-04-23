package ru.nsu.kravchenko.crackhash.worker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.schema.crack_hash_response.WorkerResponse;

@Service
@Slf4j
public class RabbitMQProducer {

    private final AmqpTemplate amqpTemplate;

    @Value("${crackHashService.manager.queue.output}")
    String outputQueue;

    public RabbitMQProducer(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void produce(WorkerResponse response) {
        try {
            amqpTemplate.convertAndSend(outputQueue, response);
            log.info("Set {} part of {} task request was sent", response.getPartNumber(), response.getRequestId());
        } catch (AmqpException ex) {
            log.error("Failed to send request '{}', cached message", response.getRequestId());
        }
    }

}

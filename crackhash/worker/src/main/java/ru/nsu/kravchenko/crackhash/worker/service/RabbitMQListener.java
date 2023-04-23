package ru.nsu.kravchenko.crackhash.worker.service;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.schema.crack_hash_request.CentralManagerRequest;
import ru.nsu.ccfit.schema.crack_hash_response.WorkerResponse;

import java.io.IOException;

@Service
@Slf4j
@EnableRabbit //нужно для активации обработки аннотаций @RabbitListener
public class RabbitMQListener {

    @Autowired
    WorkerService workerService;

    @RabbitListener(queues = "${crackHashService.worker.queue.input}")
    public void processMessage(CentralManagerRequest message) {
        log.info("Message received: [{}]", message);
        workerService.processTask(message);
    }
}

package ru.nsu.kravchenko.crackhash.centralmanager.service;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.schema.crack_hash_response.WorkerResponse;


import java.io.IOException;

@Service
@Slf4j
@EnableRabbit
public class RabbitMQListener {

    @Autowired
    CrackHashService crackHashService;

    @RabbitListener(queues = "${centralManagerService.input.queue}", ackMode = "AUTO")
    public void processMessage(WorkerResponse message) {
        log.info("Message received: [{}]", message);
        crackHashService.handleWorkerResponse(message);
    }
}

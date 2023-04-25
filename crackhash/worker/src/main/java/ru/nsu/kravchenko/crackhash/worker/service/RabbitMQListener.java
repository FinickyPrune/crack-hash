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
@EnableRabbit
public class RabbitMQListener {

    @Autowired
    WorkerService workerService;

    @RabbitListener(queues = "${crackHashService.worker.queue.input}", ackMode = "MANUAL")
    public void processMessage(CentralManagerRequest message, Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        log.info("Message received: [{}]", message);
        var response = workerService.processTask(message);
        try {
            channel.basicAck(tag, false);
            workerService.sendResponse(response);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}

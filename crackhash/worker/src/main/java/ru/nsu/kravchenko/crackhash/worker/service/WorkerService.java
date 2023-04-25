package ru.nsu.kravchenko.crackhash.worker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.schema.crack_hash_request.CentralManagerRequest;
import ru.nsu.ccfit.schema.crack_hash_response.WorkerResponse;
import ru.nsu.kravchenko.crackhash.worker.model.cracker.HashCracker;
import ru.nsu.kravchenko.crackhash.worker.service.utils.WorkerResponseBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
@Slf4j
@EnableScheduling
public class WorkerService {

    @Autowired
    RabbitMQProducer rabbitProducer;

     List<WorkerResponse> tasksToSend = Collections.synchronizedList(new ArrayList<>());

    public WorkerResponse processTask(CentralManagerRequest request) {
        return crackCode(request);
    }

    private WorkerResponse crackCode(CentralManagerRequest request) {
        log.info("Started processing task: {}", request.getRequestId());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            log.error(e.getLocalizedMessage());
        }

        List<String> answers = HashCracker.crack(
                request.getHash(),
                request.getMaxLength(),
                request.getPartNumber(),
                request.getPartCount(),
                request.getAlphabet().getSymbols()
        );

        log.info("Finished processing task : {}", request.getRequestId());
        var response = WorkerResponseBuilder.buildResponse(
                request.getRequestId(),
                request.getPartNumber(),
                answers
        );
        tasksToSend.add(response);
        return response;
    }

    public void sendResponse(WorkerResponse response) {
        if (rabbitProducer.produce(response)) {
            tasksToSend.remove(response);
        }
    }

}

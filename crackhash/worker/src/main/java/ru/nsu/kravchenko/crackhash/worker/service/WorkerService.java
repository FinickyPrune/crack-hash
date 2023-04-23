package ru.nsu.kravchenko.crackhash.worker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.nsu.ccfit.schema.crack_hash_request.CentralManagerRequest;
import ru.nsu.ccfit.schema.crack_hash_response.WorkerResponse;
import ru.nsu.kravchenko.crackhash.worker.model.cracker.HashCracker;
import ru.nsu.kravchenko.crackhash.worker.model.dto.OkResponseDTO;
import ru.nsu.kravchenko.crackhash.worker.service.utils.WorkerResponseBuilder;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
@EnableScheduling
public class WorkerService {

    ExecutorService executors = Executors.newFixedThreadPool(5);

    @Autowired
    RabbitMQProducer rabbitProducer;

    public void processTask(CentralManagerRequest request) {
        executors.execute(() -> { crackCode(request); });
    }

    private void crackCode(CentralManagerRequest request){
        log.info("Started processing task: {}", request.getRequestId());

        List<String> answers = HashCracker.crack(
                request.getHash(),
                request.getMaxLength(),
                request.getPartNumber(),
                request.getPartCount(),
                request.getAlphabet().getSymbols()
        );

        log.info("Finished processing task : {}", request.getRequestId());
        sendResponse(WorkerResponseBuilder.buildResponse(request.getRequestId(), request.getPartNumber(), answers));
    }

    private void sendResponse(WorkerResponse response) {
        rabbitProducer.produce(response);
    }

}

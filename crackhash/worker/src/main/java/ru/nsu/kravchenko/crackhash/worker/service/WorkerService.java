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

    @Value("${crackHashService.manager.ip}")
    private String managerIp;
    @Value("${crackHashService.manager.port}")
    private Integer managerPort;

    ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Autowired
    private RestTemplate restTemplate;

    public void processTask(CentralManagerRequest request) {
        executorService.execute(() -> { crackCode(request); });
    }

    private void crackCode(CentralManagerRequest request){
        log.info("Started processing task: {}", request.getRequestId());

        List<String> answers = HashCracker.crack(
                request.getHash(),
                request.getMaxLength(),
                request.getAlphabet().getSymbols()
        );

        log.info("Finished processing task : {}", request.getRequestId());
        sendResponse(WorkerResponseBuilder.buildResponse(request.getRequestId(), request.getPartNumber(), answers));
    }

    private void sendResponse(WorkerResponse response) {
        String url = String.format("http://%s:%s/api/internal/manager/hash/crack/request", managerIp, managerPort);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<WorkerResponse> entity = new HttpEntity<>(response, headers);

        restTemplate.patchForObject(
                url,
                entity,
                OkResponseDTO.class
        );
    }

}

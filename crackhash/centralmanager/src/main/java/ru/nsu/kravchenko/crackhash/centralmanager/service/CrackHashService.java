package ru.nsu.kravchenko.crackhash.centralmanager.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.nsu.ccfit.schema.crack_hash_request.CentralManagerRequest;
import ru.nsu.ccfit.schema.crack_hash_response.WorkerResponse;
import ru.nsu.kravchenko.crackhash.centralmanager.model.dto.OkResponseDTO;
import ru.nsu.kravchenko.crackhash.centralmanager.model.dto.RequestStatusDTO;
import ru.nsu.kravchenko.crackhash.centralmanager.model.repository.RequestStatusRepository;
import ru.nsu.kravchenko.crackhash.centralmanager.model.repository.RequestsRepository;
import ru.nsu.kravchenko.crackhash.centralmanager.model.requeststatus.Request;
import ru.nsu.kravchenko.crackhash.centralmanager.model.requeststatus.RequestStatus;
import ru.nsu.kravchenko.crackhash.centralmanager.model.requeststatus.RequestStatusMapper;
import ru.nsu.kravchenko.crackhash.centralmanager.model.requeststatus.Status;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;


@Service
@Slf4j
@EnableScheduling
public class CrackHashService {

    private final RequestStatusMapper requestStatusMapper = RequestStatusMapper.INSTANCE;

    private final CentralManagerRequest.Alphabet alphabet = new CentralManagerRequest.Alphabet();

    @Value("${centralManagerService.worker.ip}")
    private String workerIp;
    @Value("${centralManagerService.worker.port}")
    private Integer workerPort;
    @Value("${centralManagerService.manager.expireTimeMinutes}")
    private Integer expireTimeMinutes;
    @Value("${centralManagerService.alphabet}")
    private String alphabetString;
    @Value("${centralManagerService.workersCount}")
    private Integer workersCount;

    @Autowired
    private RabbitMQProducer rabbitProducer;

    @Autowired
    private RequestStatusRepository requestStatusRepository;

    @Autowired
    private RequestsRepository requestsRepository;


    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    private void init() {
        List.of(alphabetString.split("")).forEach(alphabet.getSymbols()::add);
    }

    public String crackHash(String hash, int maxLength) {

        var requestStatus = requestStatusRepository.insert(new RequestStatus(workersCount));
        IntStream.range(0, workersCount).forEachOrdered(i -> {
            var managerRequest = createCentralManagerRequest(
                    hash,
                    maxLength,
                    requestStatus.getRequestId(),
                    i
            );
            trySendTask(managerRequest);
        });
        return requestStatus.getRequestId();
    }

    public RequestStatusDTO getStatus(String requestId) {
        return requestStatusMapper.toRequestStatusDTO(requestStatusRepository.findByRequestId(requestId));
    }

    public void handleWorkerResponse(WorkerResponse workerResponse) {
        log.info("Received response from worker");
        var requestStatus = requestStatusRepository.findByRequestId(workerResponse.getRequestId());
        if (requestStatus.getStatus() == Status.IN_PROGRESS) {
            if (workerResponse.getAnswers() != null) {
                requestStatus.getData().addAll(workerResponse.getAnswers().getWords());
                requestStatus.getNotAnsweredWorkers().remove(workerResponse.getPartNumber());
                log.info("Response answer: {}", workerResponse.getAnswers().getWords());
                if (requestStatus.getNotAnsweredWorkers().isEmpty()) {
                    requestStatus.setStatus(Status.READY);
                }
                requestStatusRepository.save(requestStatus);
            }
        }
    }

    @Scheduled(fixedDelay = 60 * 1000)
    private void expireRequests() {

        requestStatusRepository.findAllByUpdatedBeforeAndStatusEquals(
                new Date(System.currentTimeMillis() - expireTimeMinutes * 60 * 1000),
                Status.IN_PROGRESS).forEach(requestStatus -> {

                requestStatus.setStatus(Status.ERROR);
                requestStatusRepository.save(requestStatus);

        });
    }

    private CentralManagerRequest createCentralManagerRequest(String hash,
                                                              int maxLength,
                                                              String id,
                                                              int partNumber) {

        CentralManagerRequest crackHashManagerRequest = new CentralManagerRequest();
        crackHashManagerRequest.setHash(hash);
        crackHashManagerRequest.setMaxLength(maxLength);
        crackHashManagerRequest.setRequestId(id);
        crackHashManagerRequest.setPartNumber(partNumber);
        crackHashManagerRequest.setPartCount(workersCount);
        crackHashManagerRequest.setAlphabet(alphabet);

        return  crackHashManagerRequest;
    }

    private void trySendTask(CentralManagerRequest crackHashManagerRequest) {
        if (!rabbitProducer.trySendMessage(crackHashManagerRequest)) {
            requestsRepository.insert(new Request(crackHashManagerRequest));
        }
    }
}
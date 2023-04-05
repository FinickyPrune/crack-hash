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
import ru.nsu.kravchenko.crackhash.centralmanager.model.requeststatus.RequestStatus;
import ru.nsu.kravchenko.crackhash.centralmanager.model.requeststatus.RequestStatusMapper;
import ru.nsu.kravchenko.crackhash.centralmanager.model.requeststatus.Status;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@Service
@Slf4j
@EnableScheduling
public class CrackHashService {

    private final Map<String, RequestStatus> requests = new ConcurrentHashMap<>();
    private final RequestStatusMapper requestStatusMapper = RequestStatusMapper.INSTANCE;

    private final ArrayDeque<Pair<String, Timestamp>> pendingRequests = new ArrayDeque<>();
    private final CentralManagerRequest.Alphabet alphabet = new CentralManagerRequest.Alphabet();

    @Value("${centralManagerService.worker.ip}")
    private String workerIp;
    @Value("${centralManagerService.worker.port}")
    private Integer workerPort;
    @Value("${centralManagerService.manager.expireTimeMinutes}")
    private Integer expireTimeMinutes;
    @Value("${centralManagerService.alphabet}")
    private String alphabetString;

    @Autowired
    private RestTemplate restTemplate;

    @PostConstruct
    private void init() {
        List.of(alphabetString.split("")).forEach(alphabet.getSymbols()::add);
    }

    public String crackHash(String hash, int maxLength) {
        var id = UUID.randomUUID().toString().substring(0, 7);
        requests.put(id, new RequestStatus());
        var managerRequest = createCentralManagerRequest(hash, maxLength, id);
        try {
            log.info("Sending request to worker: {}", managerRequest);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            restTemplate.exchange(
                    String.format("http://%s:%s/internal/api/worker/hash/crack/task", workerIp, workerPort),
                    HttpMethod.POST,
                    new HttpEntity<>(managerRequest, headers),
                    OkResponseDTO.class);

        } catch (Exception e) {
            log.error("Error while sending request to worker", e);
            return null;
        }
        pendingRequests.add(Pair.of(id, new Timestamp(System.currentTimeMillis())));
        return id;
    }

    public RequestStatusDTO getStatus(String requestId) {
        return requestStatusMapper.toRequestStatusDTO(requests.get(requestId));
    }

    public void handleWorkerResponse(WorkerResponse workerResponse) {
        log.info("Received response from worker");
        RequestStatus requestStatus = requests.get(workerResponse.getRequestId());
        if (requestStatus.getStatus() == Status.IN_PROGRESS) {
            if (workerResponse.getAnswers() != null) {
                requestStatus.getData().addAll(workerResponse.getAnswers().getWords());
                log.info("Response answer: {}", workerResponse.getAnswers().getWords());
                requestStatus.setStatus(Status.READY);
            }
        }
    }

    @Scheduled(fixedDelay = 10000)
    private void expireRequests() {
        pendingRequests.removeIf(pair -> {
            if (System.currentTimeMillis() - pair.getSecond().getTime() > expireTimeMinutes * 60 * 1000) {
                requests.computeIfPresent(pair.getFirst(), (s, requestStatus) -> {
                    if (requestStatus.getStatus().equals(Status.IN_PROGRESS)) {
                        requestStatus.setStatus(Status.ERROR);
                    }
                    return requestStatus;
                });
                return true;
            }
            return false;
        });
    }

    private CentralManagerRequest createCentralManagerRequest(String hash,
                                                              int maxLength,
                                                              String id) {
        CentralManagerRequest crackHashManagerRequest = new CentralManagerRequest();
        crackHashManagerRequest.setHash(hash);
        crackHashManagerRequest.setMaxLength(maxLength);
        crackHashManagerRequest.setRequestId(id);
        crackHashManagerRequest.setPartNumber(1);
        crackHashManagerRequest.setPartCount(1);
        crackHashManagerRequest.setAlphabet(alphabet);

        return  crackHashManagerRequest;
    }
}
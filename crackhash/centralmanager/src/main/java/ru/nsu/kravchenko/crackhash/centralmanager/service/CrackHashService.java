package ru.nsu.kravchenko.crackhash.centralmanager.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.ccfit.schema.crack_hash_request.CentralManagerRequest;
import ru.nsu.ccfit.schema.crack_hash_response.WorkerResponse;
import ru.nsu.kravchenko.crackhash.centralmanager.model.dto.RequestStatusDTO;
import ru.nsu.kravchenko.crackhash.centralmanager.model.repository.RequestStatusRepository;
import ru.nsu.kravchenko.crackhash.centralmanager.model.repository.RequestsRepository;
import ru.nsu.kravchenko.crackhash.centralmanager.model.requeststatus.Request;
import ru.nsu.kravchenko.crackhash.centralmanager.model.requeststatus.RequestStatus;
import ru.nsu.kravchenko.crackhash.centralmanager.model.requeststatus.RequestStatusMapper;
import ru.nsu.kravchenko.crackhash.centralmanager.model.requeststatus.Status;
import ru.nsu.kravchenko.crackhash.centralmanager.service.utils.CentralManagerRequestBuilder;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.IntStream;


@Service
@Slf4j
@EnableScheduling
public class CrackHashService {

    private final CentralManagerRequest.Alphabet alphabet = new CentralManagerRequest.Alphabet();

    @Value("${centralManagerService.alphabet}")
    private String alphabetString;
    @Value("${centralManagerService.workersCount}")
    private Integer workersCount;

    @Autowired
    private RabbitMQProducer rabbitProducer;

    @Autowired
    private RequestStatusRepository statusRepository;

    @Autowired
    private RequestsRepository requestsRepository;

    @PostConstruct
    private void init() { List.of(alphabetString.split("")).forEach(alphabet.getSymbols()::add); }

    public String crackHash(String hash, int maxLength) {

        var requestStatus = statusRepository.insert(new RequestStatus(workersCount));
        IntStream.range(0, workersCount).forEach(i -> {
            var managerRequest = CentralManagerRequestBuilder.build(
                    hash,
                    maxLength,
                    requestStatus.getRequestId(),
                    i,
                    workersCount,
                    alphabet
            );
            requestsRepository.insert(new Request(managerRequest));
            trySendTask(managerRequest);
        });
        return requestStatus.getRequestId();
    }

    public RequestStatusDTO getStatus(String requestId) {
        return RequestStatusMapper.INSTANCE.toRequestStatusDTO(statusRepository.findByRequestId(requestId));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void handleWorkerResponse(WorkerResponse workerResponse) {
        log.info("Received response from worker");
        var requestStatus = statusRepository.findByRequestId(workerResponse.getRequestId());
        if (requestStatus.getStatus() == Status.IN_PROGRESS) {
            if (workerResponse.getAnswers() != null) {
                if (!requestStatus.getData().containsAll((workerResponse.getAnswers().getWords()))) { // in case Rabbit restarts and make already ready request
                    requestStatus.getData().addAll(workerResponse.getAnswers().getWords());
                    log.info("Response answer: {}", workerResponse.getAnswers().getWords());
                }
                requestStatus.getNotAnsweredWorkers().remove(workerResponse.getPartNumber());

                requestsRepository.deleteRequestsByRequestIdAndPartNumber(
                        requestStatus.getRequestId(),
                        workerResponse.getPartNumber()
                );

                if (requestStatus.getNotAnsweredWorkers().isEmpty()) {
                    requestStatus.setStatus(Status.READY);
                }
                statusRepository.save(requestStatus);
            }
        }
    }

    private void trySendTask(CentralManagerRequest crackHashManagerRequest) {
        rabbitProducer.trySendMessage(crackHashManagerRequest);
    }

}
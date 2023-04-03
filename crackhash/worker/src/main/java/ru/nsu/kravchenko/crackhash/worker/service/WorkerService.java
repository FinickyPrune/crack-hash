package ru.nsu.kravchenko.crackhash.worker.service;

import lombok.extern.slf4j.Slf4j;
import org.paukov.combinatorics.CombinatoricsFactory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
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
import ru.nsu.ccfit.schema.crack_hash_response.WorkerResponse.Answers;
import ru.nsu.kravchenko.crackhash.worker.model.dto.OkResponseDTO;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.paukov.combinatorics.CombinatoricsFactory.createMultiCombinationGenerator;

@Service
@Slf4j
@EnableScheduling
public class WorkerService {

    @Value("${crackHashService.manager.ip}")
    String managerIp;
    @Value("${crackHashService.manager.port}")
    Integer managerPort;

    ExecutorService executorService = Executors.newFixedThreadPool(10);
    @Autowired
    private RestTemplate restTemplate;

    public void putTask(CentralManagerRequest request) {
        executorService.execute(() -> {
            crackCode(request);
        });
    }

    private void sendResponse(WorkerResponse response) {
        String url = String.format("http://%s:%s/api/internal/manager/hash/crack/request", managerIp, managerPort);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<WorkerResponse> entity = new HttpEntity<>(response, headers);
        restTemplate.patchForObject(url, entity, OkResponseDTO.class);
    }

    private WorkerResponse buildResponse(String requestId, int partNumber, List<String> answers) {
        Answers answer = new Answers();
        answer.getWords().addAll(answers);
        WorkerResponse response = new WorkerResponse();
        response.setRequestId(requestId);
        response.setPartNumber(partNumber);
        response.setAnswers(answer);
        return response;
    }

    private void crackCode(CentralManagerRequest request){
        log.info("start processing task: {}", request.getRequestId());
        ICombinatoricsVector<String> vector = CombinatoricsFactory.createVector(request.getAlphabet().getSymbols());
        List<String> answers = new ArrayList<>();
        for (int i = 1; i <= request.getMaxLength(); i++) {
            Generator<String> gen = CombinatoricsFactory.createPermutationWithRepetitionGenerator(vector, i);
            for (var string : gen) {
                MessageDigest md5 = null;
                try {
                    md5 = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
                String inputString = String.join("", string.getVector());
                String hash = (new HexBinaryAdapter()).marshal(md5.digest(inputString.getBytes()));
                if (request.getHash().equalsIgnoreCase(hash)) {
                    answers.add(String.join("", string.getVector()));
                    log.info("added answer : {}", String.join("", string.getVector()));
                }
            }
        }
        log.info("end processing task : {}", request.getRequestId());
        sendResponse(buildResponse(request.getRequestId(), request.getPartNumber(), answers));
    }

}

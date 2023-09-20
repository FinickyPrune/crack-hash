package ru.nsu.kravchenko.crackhash.worker.service.utils;

import ru.nsu.ccfit.schema.crack_hash_response.WorkerResponse;

import java.util.List;

public class WorkerResponseBuilder {

    public static WorkerResponse buildResponse(String requestId, int partNumber, List<String> answers) {
        WorkerResponse.Answers answer = new WorkerResponse.Answers();
        answer.getWords().addAll(answers);
        WorkerResponse response = new WorkerResponse();
        response.setRequestId(requestId);
        response.setPartNumber(partNumber);
        response.setAnswers(answer);

        return response;
    }

    }

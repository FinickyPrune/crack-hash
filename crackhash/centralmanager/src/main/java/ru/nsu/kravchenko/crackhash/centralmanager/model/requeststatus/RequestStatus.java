package ru.nsu.kravchenko.crackhash.centralmanager.model.requeststatus;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.*;


@Data
public class RequestStatus {

    @Id
    private String requestId;

    private Status status;

    private ArrayList<String> data;

    private HashSet<Integer> notAnsweredWorkers;

    private Date updated;

    public RequestStatus (int workersCount) {
        this.requestId = UUID.randomUUID().toString();
        this.status = Status.IN_PROGRESS;
        this.updated = new Date(System.currentTimeMillis());
        data = new ArrayList<>();
        notAnsweredWorkers = new HashSet<>();
        for (int i = 0; i < workersCount; i++) {
            notAnsweredWorkers.add(i);
        }
    }
}

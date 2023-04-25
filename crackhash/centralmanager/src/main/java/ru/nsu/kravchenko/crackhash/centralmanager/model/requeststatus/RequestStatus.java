package ru.nsu.kravchenko.crackhash.centralmanager.model.requeststatus;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;


@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Document("RequestStatus")
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

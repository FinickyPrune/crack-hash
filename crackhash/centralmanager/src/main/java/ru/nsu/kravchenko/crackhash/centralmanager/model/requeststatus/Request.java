package ru.nsu.kravchenko.crackhash.centralmanager.model.requeststatus;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.nsu.ccfit.schema.crack_hash_request.CentralManagerRequest;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Document(collection = "requests")
public class Request {
    @Id
    private String id;
    private String requestId;
    private Integer partNumber;
    private CentralManagerRequest request;

    private Date updated;

    public Request(CentralManagerRequest request) {
        this.id = UUID.randomUUID().toString();
        this.request = request;
        this.requestId = request.getRequestId();
        this.partNumber = request.getPartNumber();
        this.updated = new Date(System.currentTimeMillis());
    }
}
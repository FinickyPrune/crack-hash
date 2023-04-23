package ru.nsu.kravchenko.crackhash.centralmanager.model.requeststatus;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.nsu.ccfit.schema.crack_hash_request.CentralManagerRequest;

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
    private CentralManagerRequest request;

    public Request(CentralManagerRequest request) {
        this.id = request.getRequestId();
        this.request = request;
    }
}
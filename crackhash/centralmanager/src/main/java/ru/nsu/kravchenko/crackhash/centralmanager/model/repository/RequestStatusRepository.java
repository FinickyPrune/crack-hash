package ru.nsu.kravchenko.crackhash.centralmanager.model.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nsu.kravchenko.crackhash.centralmanager.model.requeststatus.RequestStatus;
import ru.nsu.kravchenko.crackhash.centralmanager.model.requeststatus.Status;

import java.util.Collection;
import java.util.Date;

public interface RequestStatusRepository extends MongoRepository<RequestStatus, String> {
    RequestStatus findByRequestId(String requestId);
    Collection<RequestStatus> findAllByUpdatedBeforeAndStatusEquals(Date timestamp, Status status);
}
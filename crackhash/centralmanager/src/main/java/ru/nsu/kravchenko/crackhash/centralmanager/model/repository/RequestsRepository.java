package ru.nsu.kravchenko.crackhash.centralmanager.model.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nsu.kravchenko.crackhash.centralmanager.model.requeststatus.Request;
import ru.nsu.kravchenko.crackhash.centralmanager.model.requeststatus.RequestStatus;

import java.util.Collection;
import java.util.Date;

public interface RequestsRepository extends MongoRepository<Request, String> {
     void deleteRequestsByRequestIdAndPartNumber(String requestId, Integer partNumber);
     Collection<Request> findByRequestIdAndPartNumber(String requestId, Integer partNumber);
     Collection<Request> findAllByUpdatedBefore(Date updated);
}
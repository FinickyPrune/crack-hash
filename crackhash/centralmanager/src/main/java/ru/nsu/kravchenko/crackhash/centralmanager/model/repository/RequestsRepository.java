package ru.nsu.kravchenko.crackhash.centralmanager.model.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nsu.kravchenko.crackhash.centralmanager.model.requeststatus.Request;

public interface RequestsRepository extends MongoRepository<Request, String> {
     void deleteRequestsByRequestIdAndPartNumber(String requestId, Integer partNumber);
}
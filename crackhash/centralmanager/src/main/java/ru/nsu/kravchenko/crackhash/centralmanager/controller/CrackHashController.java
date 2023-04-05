package ru.nsu.kravchenko.crackhash.centralmanager.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.nsu.kravchenko.crackhash.centralmanager.model.dto.RequestIdDTO;
import ru.nsu.kravchenko.crackhash.centralmanager.model.dto.RequestStatusDTO;
import ru.nsu.kravchenko.crackhash.centralmanager.model.dto.WorkerRequestDTO;
import ru.nsu.kravchenko.crackhash.centralmanager.service.CrackHashService;


@Slf4j
@Controller
@RequestMapping("/api/hash")
public class CrackHashController {

    @Autowired
    CrackHashService crackHashService;

    @PostMapping("/crack")
    public ResponseEntity<RequestIdDTO> crackHash(@RequestBody WorkerRequestDTO request) {
        log.info("Received request to crack: {}", request);
        return new ResponseEntity<>(
                new RequestIdDTO(crackHashService.crackHash(request.getHash(), request.getMaxLength())), HttpStatus.OK);
    }

    @GetMapping("/status/{requestId}")
    public ResponseEntity<RequestStatusDTO> getStatus(@PathVariable String requestId) {
        log.info("Received statusRequest of request: {}", requestId);
        return new ResponseEntity<>(crackHashService.getStatus(requestId), HttpStatus.OK);
    }
}
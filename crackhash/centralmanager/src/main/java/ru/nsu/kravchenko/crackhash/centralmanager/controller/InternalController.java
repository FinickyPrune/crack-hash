package ru.nsu.kravchenko.crackhash.centralmanager.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.nsu.ccfit.schema.crack_hash_response.WorkerResponse;
import ru.nsu.kravchenko.crackhash.centralmanager.model.dto.OkResponseDTO;
import ru.nsu.kravchenko.crackhash.centralmanager.service.CrackHashService;


@Controller
@Slf4j
@RequestMapping("/api/internal/manager")
public class InternalController {
    @Autowired
    CrackHashService crackHashService;

    @PatchMapping(value = "/hash/crack/request",  consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<OkResponseDTO> workerCallbackHandler(@RequestBody WorkerResponse crackHashWorkerResponse) {
        log.info("Received worker response: {}", crackHashWorkerResponse);
        crackHashService.handleWorkerCallback(crackHashWorkerResponse);
        return new ResponseEntity<>(new OkResponseDTO(), HttpStatus.OK);
    }
}

package ru.nsu.kravchenko.crackhash.worker.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.nsu.ccfit.schema.crack_hash_request.CentralManagerRequest;
import ru.nsu.kravchenko.crackhash.worker.model.dto.OkResponseDTO;
import ru.nsu.kravchenko.crackhash.worker.service.WorkerService;

@Slf4j
@Controller
@RequestMapping("/internal/api/worker")
public class WorkerController {

    @Autowired
    private WorkerService workerService;

    @PostMapping(value = "/hash/crack/task", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<OkResponseDTO> getTask(@RequestBody CentralManagerRequest request) {
        log.info("getTask() : {}", request);
        workerService.putTask(request);
        return ResponseEntity.status(HttpStatus.OK).body(new OkResponseDTO());
    }
}

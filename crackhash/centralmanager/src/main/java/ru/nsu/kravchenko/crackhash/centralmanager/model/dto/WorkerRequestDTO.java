package ru.nsu.kravchenko.crackhash.centralmanager.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class WorkerRequestDTO {

    @JsonProperty(value = "hash", required = true)
    private String hash;

    @JsonProperty(value = "maxLength", required = true)
    private int maxLength;
}
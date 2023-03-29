package ru.nsu.kravchenko.crackhash.centralmanager.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class RequestStatus {
    private Status status = Status.IN_PROGRESS;
    private List<String> result = new ArrayList<>();
}

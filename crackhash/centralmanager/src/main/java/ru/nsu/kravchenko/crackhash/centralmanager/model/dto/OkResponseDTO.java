package ru.nsu.kravchenko.crackhash.centralmanager.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
//@JsonDeserialize(builder = OkResponseDTO.OkResponseDTOBuilder.class)
@Jacksonized
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
@Builder
public class OkResponseDTO {

}
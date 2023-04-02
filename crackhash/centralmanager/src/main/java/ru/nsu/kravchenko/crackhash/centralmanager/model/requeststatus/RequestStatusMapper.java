package ru.nsu.kravchenko.crackhash.centralmanager.model.requeststatus;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.nsu.kravchenko.crackhash.centralmanager.model.dto.RequestStatusDTO;

@Mapper
public interface RequestStatusMapper {
    RequestStatusMapper INSTANCE = Mappers.getMapper(RequestStatusMapper.class);

    RequestStatusDTO toRequestStatusDTO(RequestStatus requestStatus);

    @InheritInverseConfiguration
    RequestStatus toRequestStatus(RequestStatusDTO requestStatusDTO);
}

package ru.nsu.kravchenko.crackhash.centralmanager.service.utils;

import ru.nsu.ccfit.schema.crack_hash_request.CentralManagerRequest;

public class CentralManagerRequestBuilder {

    public static CentralManagerRequest build(String hash,
                                              int maxLength,
                                              String id,
                                              int partNumber,
                                              int workersCount,
                                              CentralManagerRequest.Alphabet alphabet) {

        CentralManagerRequest crackHashManagerRequest = new CentralManagerRequest();
        crackHashManagerRequest.setHash(hash);
        crackHashManagerRequest.setMaxLength(maxLength);
        crackHashManagerRequest.setRequestId(id);
        crackHashManagerRequest.setPartNumber(partNumber);
        crackHashManagerRequest.setPartCount(workersCount);
        crackHashManagerRequest.setAlphabet(alphabet);

        return  crackHashManagerRequest;
    }
}

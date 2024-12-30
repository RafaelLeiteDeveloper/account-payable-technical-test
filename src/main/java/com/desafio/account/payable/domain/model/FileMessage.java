package com.desafio.account.payable.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileMessage implements Serializable {
    private String fileType;
    private byte[] fileContent;
    private String idProcess;
}

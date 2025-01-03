package com.desafio.account.payable.application.service;

import com.desafio.account.payable.application.dto.request.AccountRequest;
import com.desafio.account.payable.application.dto.response.AccountResponse;
import com.desafio.account.payable.application.mapper.AccountMapper;
import com.desafio.account.payable.domain.model.AccountModel;
import com.desafio.account.payable.domain.model.AccountStatus;
import com.desafio.account.payable.domain.model.FileMessage;
import com.desafio.account.payable.domain.repository.AccountRepository;
import com.desafio.account.payable.infrastructure.repository.AccountSpecification;
import com.desafio.account.payable.infrastructure.util.CurrencyFormatterUtil;
import com.desafio.account.payable.infrastructure.util.FileUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {
    private final AmqpTemplate amqpTemplate;

    private static final String QUEUE_NAME = "import-file";

    private static final String QUEUE_NAME_DLQ = "import-file-dlq";

    public void sendToToQueueDlq(InputStream inputStream, String processId) throws IOException {
        log.warn("Sending file to DLQ for processId: {}", processId);
        byte[] fileBytes = FileUtil.convertInputStreamToByteArray(inputStream);

        FileMessage fileMessage = FileMessage.builder()
                .fileType("csv")
                .idProcess(processId)
                .fileContent(fileBytes).build();

        amqpTemplate.convertAndSend(QUEUE_NAME_DLQ, fileMessage);
        log.info("Message sent to DLQ for processId: {}", processId);
    }

    public void sendToQueue(String fileType, InputStream fileInputStream) {
        log.info("Sending file to queue with file type: {}", fileType);
        try {
            byte[] fileBytes = FileUtil.convertInputStreamToByteArray(fileInputStream);

            FileMessage fileMessage = FileMessage.builder()
                    .fileType(fileType)
                    .fileContent(fileBytes).build();

            MessagePostProcessor messagePostProcessor = message -> {
                MessageProperties properties = message.getMessageProperties();
                properties.setHeader("x-process-id", UUID.randomUUID().toString());
                return message;
            };

            amqpTemplate.convertAndSend(QUEUE_NAME, fileMessage, messagePostProcessor);
            log.info("File sent to queue with file type: {}", fileType);

        } catch (IOException e) {
            log.error("Error sending file to queue for file type: {}", fileType, e);
        }
    }
}

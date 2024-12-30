package com.desafio.account.payable.application.service.impl;

import com.desafio.account.payable.application.service.QueueService;
import com.desafio.account.payable.domain.model.FileMessage;
import com.desafio.account.payable.infrastructure.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class QueueServiceImpl implements QueueService {
    private final AmqpTemplate amqpTemplate;

    private static final String QUEUE_NAME = "import-file";
    private static final String QUEUE_NAME_DLQ = "import-file-dlq";

    @Override
    public void sendToToQueueDlq(InputStream inputStream, String processId) throws IOException {
        log.warn("Sending file to DLQ for processId: {}", processId);
        byte[] fileBytes = FileUtil.convertInputStreamToByteArray(inputStream);

        FileMessage fileMessage = FileMessage.builder()
                .fileType("csv")
                .idProcess(processId)
                .fileContent(fileBytes).build();

        this.amqpTemplate.convertAndSend(QUEUE_NAME_DLQ, fileMessage);
        log.info("Message sent to DLQ for processId: {}", processId);
    }

    @Override
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


            this.amqpTemplate.convertAndSend(QUEUE_NAME, fileMessage, messagePostProcessor);
            log.info("File sent to queue with file type: {}", fileType);

        } catch (IOException e) {
            log.error("Error sending file to queue for file type: {}", fileType, e);
        }
    }

}

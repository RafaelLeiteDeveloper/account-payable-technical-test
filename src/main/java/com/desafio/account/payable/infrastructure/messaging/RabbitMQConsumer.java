package com.desafio.account.payable.infrastructure.messaging;

import com.desafio.account.payable.application.service.AccountImportService;
import com.desafio.account.payable.application.service.QueueService;
import com.desafio.account.payable.domain.model.FileMessage;
import com.desafio.account.payable.infrastructure.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQConsumer {

    private final AccountImportService accountImportService;
    private final QueueService queueService;

    @RabbitListener(queues = "import-file")
    public void importAccounts(@Payload FileMessage fileMessage, Message message) throws IOException {
        MessageProperties properties = message.getMessageProperties();
        String processId = (String) properties.getHeaders().get("x-process-id");

        log.info("Received message for processId: {}", processId);
        log.info("Starting account import for processId: {}", processId);
        accountImportService.importAccounts(fileMessage, processId);
        log.info("Account import completed successfully for processId: {}", processId);

    }
}

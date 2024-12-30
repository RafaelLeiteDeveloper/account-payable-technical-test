package com.desafio.account.payable.application.service;

import java.io.IOException;
import java.io.InputStream;

public interface QueueService {
    void sendToToQueueDlq(InputStream inputStream, String processId) throws IOException;
    void sendToQueue(String fileType, InputStream fileInputStream);
}

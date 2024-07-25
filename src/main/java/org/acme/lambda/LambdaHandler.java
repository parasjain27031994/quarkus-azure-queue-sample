package org.acme.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.azure.storage.queue.QueueClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class LambdaHandler implements RequestHandler<JsonNode, String> {

    private static final Logger logger = LoggerFactory.getLogger(LambdaHandler.class);

    @Inject
    QueueClient queueClient;

    @Inject
    ObjectMapper mapper;

    @Override
    public String handleRequest(JsonNode input, Context context) {
        return sendMetadataToAzureStorage(input);
    }


    public String sendMetadataToAzureStorage(JsonNode input) {
        String base64TranscriptMetadataStr = Base64.getEncoder().encodeToString(toJsonString(input).getBytes(StandardCharsets.UTF_8));
        queueClient.sendMessage(base64TranscriptMetadataStr);
        return "Message sent successfully..!!";
    }


    private String toJsonString(JsonNode node) {
        try {
            String mappedRecordJson = mapper.writeValueAsString(node);
            logger.info(mappedRecordJson);
            return mappedRecordJson;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}

package br.com.rafaellino.handler;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.BasicConfigurator;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class LambdaHandler implements RequestHandler<Map<String, Object>, LambdaHandler.CustomResponse> {

  private final static String TABLE_NAME = "my-simple-forms";

  static {
    // necessary to load log4j logger configuration in order to avoid warnings
    BasicConfigurator.configure();
  }

  @Override
  public CustomResponse handleRequest(final Map<String, Object> input, final Context context) {

    LambdaLogger logger = context.getLogger();
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> req = null;

    try {
      req = mapper.readValue((String) input.get("body"), Map.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    logger.log("body: " + req.toString());

    Map<String, AttributeValue> itemToSave = parse(req);
    itemToSave.put("id", AttributeValue.builder().s(UUID.randomUUID().toString()).build());
    itemToSave.put("date", AttributeValue.builder().s(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).toString()).build());

    logger.log("to save: " + itemToSave.toString());
    try (DynamoDbClient ddb = DynamoDbClient.builder().build()) {

      ddb.putItem(PutItemRequest.builder()
          .tableName(TABLE_NAME)
          .item(itemToSave).build());

    } catch (Exception ex) {
      logger.log("error: " + ex);
      return new CustomResponse(500, "internal error");
    }
    logger.log("Success");
    return new CustomResponse(201, "posted successfully");
  }

  private static Map<String, AttributeValue> parse(Map<String, Object> req) {
    Map<String, AttributeValue> parsedItem = new HashMap<>();
    parsedItem.put("children_not_paying", AttributeValue.builder().s(String.valueOf(req.get("children_not_paying"))).build());
    parsedItem.put("children_pay_full", AttributeValue.builder().s(String.valueOf(req.get("children_pay_full"))).build());
    parsedItem.put("children_pay_half", AttributeValue.builder().s(String.valueOf(req.get("children_pay_half"))).build());
    parsedItem.put("email", AttributeValue.builder().s(String.valueOf(req.get("email"))).build());
    parsedItem.put("guests", AttributeValue.builder().s(String.valueOf(req.get("guests"))).build());
    parsedItem.put("nome", AttributeValue.builder().s(String.valueOf(req.get("nome"))).build());
    parsedItem.put("phone", AttributeValue.builder().s(String.valueOf(req.get("phone"))).build());
    parsedItem.put("quantity_adults", AttributeValue.builder().s(String.valueOf(req.get("quantity_adults"))).build());
    return parsedItem;
  }

  static class CustomResponse {
    int statusCode;
    String message;

    public CustomResponse(int statusCode, String message) {
      this.statusCode = statusCode;
      this.message = message;
    }

    public int getStatusCode() {
      return statusCode;
    }

    public void setStatusCode(int statusCode) {
      this.statusCode = statusCode;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }
  }
}

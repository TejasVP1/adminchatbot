package com.ailoanchatbot.Ai.Powered.Loan.ChatBot;

import com.ailoanchatbot.Ai.Powered.Loan.ChatBot.service.ExcelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import chatbot.ChatbotServiceGrpc;
import chatbot.Chatbot;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("genericchatbot")
public class GenericChatbot {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ExcelService excelService;

   // Inject the Excel service

    private final Map<String, List<Map<String, Object>>> queryResultsMap = new HashMap<>();

    private final ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
            .usePlaintext()
            .build();
    private final ChatbotServiceGrpc.ChatbotServiceBlockingStub chatbotStub = ChatbotServiceGrpc.newBlockingStub(channel);
    @GetMapping
    public Map<String, String> generichatbot(@RequestParam String userInput) {
        Map<String, String> response = new HashMap<>();

        if (userInput == null || userInput.isBlank()) {  // Java 11+ (use trim().isEmpty() for Java 8)
            response.put("response", "You need to enter details");
            return response;
        }

        try {
            System.out.println(userInput);
            System.out.println("Started processing user input...");


//            In programming, an API stub is a mock implementation of an API that is used for testing and development purposes. It acts as a placeholder for the actual API, allowing developers to simulate the behavior of the API without needing access to the real implementation. This is particularly useful when the actual API is not yet available, or when testing needs to be done in isolation from external dependencies
            // Call gRPC service to generate SQL
            Chatbot.SQLResponse sqlResponse = chatbotStub.generateSQL(
                    Chatbot.UserQuery.newBuilder().setUserInput(userInput).build()
            );


            String sqlQuery = sqlResponse.getSqlQuery();
            if (sqlQuery.equals("unwanted") ) {
                return Collections.singletonMap("message", "Sorry, I only answer loans related question" );
            }
            else if(sqlQuery.equals("restricted")){
                return Collections.singletonMap("message", "You cant create , update or delete data , you can only read or view the data" );
            }
            else if(sqlQuery.equals("sensitive")){
                return Collections.singletonMap("message", "i think you are asking for sensitive information , so i cant provide that" );
            }

            System.out.println("Generated SQL Query: " + sqlQuery);

            if (sqlQuery.startsWith("Error")) {
                return Collections.singletonMap("message", sqlQuery);
            }

            // Execute SQL query
            List<Map<String, Object>> queryResults = jdbcTemplate.queryForList(sqlQuery);
            String queryId = UUID.randomUUID().toString();
            queryResultsMap.put(queryId, queryResults);

            // Check the number of rows returned
            if (queryResults.size() > 50) {
                // Generate Excel asynchronously
                CompletableFuture.runAsync(() -> {
                    System.out.println("Starting Excel generation...");
                    try {
                        excelService.generateExcel(queryResults, queryId);
                        System.out.println("Excel file generated!");
                    } catch (Exception e) {
                        System.err.println("Error generating Excel: " + e.getMessage());
                    }
                });

                return Map.of(
                        "message", "You can download the Excel file.",
                        "queryId", queryId
                );
            }

            // Convert query results to JSON for gRPC call
            ObjectMapper mapper = new ObjectMapper();
            String jsonResults = mapper.writeValueAsString(queryResults);

            // Call gRPC service to format results
            System.out.println("Calling gRPC formatResults...");
            Chatbot.FormattedResponse formattedResponse = chatbotStub.formatResults(
                    Chatbot.QueryResults.newBuilder().setJsonData(jsonResults).build()
            );
            System.out.println("gRPC formatResults completed!");
            excelService.generateExcel(queryResults, queryId);

            return Map.of(
                    "response", formattedResponse.getFormattedText(),
                    "queryId", queryId
            );

        } catch (Exception e) {
            return Collections.singletonMap("message", "Error processing query: " + e.getMessage());
        }
    }

}

package com.finance.dashboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DashboardApplicationTests {

    @LocalServerPort
    private int port;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Test
    void healthEndpointIsPublic() throws Exception {
        HttpResponse<String> response = sendRequest("GET", "/api/health", null, null, null);

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"status\":\"UP\""));
    }

    @Test
    void viewerCanReadDashboardButNotRecords() throws Exception {
        HttpResponse<String> summaryResponse = sendRequest("GET", "/api/dashboard/summary", null, "viewer@finance.local", "Viewer@123");
        HttpResponse<String> recordsResponse = sendRequest("GET", "/api/records", null, "viewer@finance.local", "Viewer@123");

        assertEquals(200, summaryResponse.statusCode());
        assertTrue(summaryResponse.body().contains("\"totalIncome\":"));
        assertTrue(countOccurrences(summaryResponse.body(), "\"id\":") >= 5);
        assertEquals(403, recordsResponse.statusCode());
    }

    @Test
    void analystCanReadRecordsButCannotCreateThem() throws Exception {
        HttpResponse<String> recordsResponse = sendRequest("GET", "/api/records", null, "analyst@finance.local", "Analyst@123");
        HttpResponse<String> createResponse = sendRequest(
                "POST",
                "/api/records",
                """
                        {
                          "amount": 99.99,
                          "type": "EXPENSE",
                          "category": "Snacks",
                          "date": "2026-03-30",
                          "notes": "Team treats"
                        }
                        """,
                "analyst@finance.local",
                "Analyst@123"
        );

        assertEquals(200, recordsResponse.statusCode());
        assertEquals(5, countOccurrences(recordsResponse.body(), "\"id\":"));
        assertEquals(403, createResponse.statusCode());
    }

    @Test
    void adminCanCreateUpdateAndDeleteRecords() throws Exception {
        HttpResponse<String> createResponse = sendRequest(
                "POST",
                "/api/records",
                """
                        {
                          "amount": 999.50,
                          "type": "INCOME",
                          "category": "Bonus",
                          "date": "2026-03-28",
                          "notes": "Quarterly performance bonus"
                        }
                        """,
                "admin@finance.local",
                "Admin@123"
        );

        String id = extractNumber(createResponse.body(), "\"id\":(\\d+)");

        HttpResponse<String> updateResponse = sendRequest(
                "PUT",
                "/api/records/" + id,
                """
                        {
                          "amount": 875.00,
                          "type": "INCOME",
                          "category": "Bonus",
                          "date": "2026-03-28",
                          "notes": "Adjusted quarterly bonus"
                        }
                        """,
                "admin@finance.local",
                "Admin@123"
        );

        HttpResponse<String> deleteResponse = sendRequest("DELETE", "/api/records/" + id, null, "admin@finance.local", "Admin@123");

        assertEquals(201, createResponse.statusCode());
        assertTrue(createResponse.body().contains("\"category\":\"Bonus\""));
        assertEquals(200, updateResponse.statusCode());
        assertTrue(updateResponse.body().contains("\"amount\":875.00"));
        assertEquals(204, deleteResponse.statusCode());
    }

    @Test
    void adminCanManageUsers() throws Exception {
        HttpResponse<String> createUserResponse = sendRequest(
                "POST",
                "/api/users",
                """
                        {
                          "name": "New Analyst",
                          "email": "new.analyst@finance.local",
                          "password": "Secure@123",
                          "role": "ANALYST",
                          "status": "ACTIVE"
                        }
                        """,
                "admin@finance.local",
                "Admin@123"
        );

        assertEquals(201, createUserResponse.statusCode());
        assertTrue(createUserResponse.body().contains("\"email\":\"new.analyst@finance.local\""));
    }

    @Test
    void invalidPayloadReturnsBadRequest() throws Exception {
        HttpResponse<String> response = sendRequest(
                "POST",
                "/api/records",
                """
                        {
                          "amount": 0,
                          "type": "EXPENSE",
                          "category": "",
                          "date": "2030-01-01"
                        }
                        """,
                "admin@finance.local",
                "Admin@123"
        );

        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("\"message\":\"Validation failed\""));
        assertTrue(response.body().contains("amount: Amount must be greater than zero"));
        assertTrue(response.body().contains("category: Category is required"));
        assertTrue(response.body().contains("date: Date cannot be in the future"));
    }

    private HttpResponse<String> sendRequest(String method, String path, String body, String username, String password)
            throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .header("Accept", MediaType.APPLICATION_JSON_VALUE);

        if (body != null) {
            builder.header("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        }

        if (username != null && password != null) {
            String token = Base64.getEncoder()
                    .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
            builder.header("Authorization", "Basic " + token);
        }

        builder.method(method, body == null
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(body));

        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private int countOccurrences(String input, String token) {
        int count = 0;
        int index = 0;
        while ((index = input.indexOf(token, index)) != -1) {
            count++;
            index += token.length();
        }
        return count;
    }

    private String extractNumber(String input, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(input);
        if (!matcher.find()) {
            throw new IllegalStateException("Pattern not found in response: " + regex);
        }
        return matcher.group(1);
    }
}

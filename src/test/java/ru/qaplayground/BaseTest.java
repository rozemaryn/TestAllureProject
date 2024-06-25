package ru.qaplayground;

import java.util.Map;

public class BaseTest {
    public static String baseURL = "https://release-gs.qa-playground.com/api/v1/";
    //devURL "https://dev-gs.qa-playground.com/api/v1";

    public static Map<String, String> headers = Map.of("Authorization",
            "Bearer eyJhbGciOiJIUzI1NiIsImtpZCI6IldGZlRBQ0hzYUhvQ3VML1MiLCJ0eXAiOiJKV1QifQ.eyJhdWQiOiJhdXRoZW50aWNhdGVkIiwiZXhwIjoxNzE5NTcyOTA1LCJpYXQiOjE3MTg5NzI5MDUsImlzcyI6Imh0dHBzOi8vbXlrb3RxYm9ja3p2emFjY2N1Ynouc3VwYWJhc2UuY28vYXV0aC92MSIsInN1YiI6ImRlMDU5M2YwLTE4YTAtNDViMy1iYWVmLTI3NDQ2YTc1MjUzZCIsImVtYWlsIjoiYW5pbWFsaXRvc0B5YW5kZXgucnUiLCJwaG9uZSI6IiIsImFwcF9tZXRhZGF0YSI6eyJwcm92aWRlciI6ImVtYWlsIiwicHJvdmlkZXJzIjpbImVtYWlsIl19LCJ1c2VyX21ldGFkYXRhIjp7ImVtYWlsIjoiYW5pbWFsaXRvc0B5YW5kZXgucnUiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsImZ1bGxfbmFtZSI6ItCc0LDRgNC40Y8iLCJwaG9uZV92ZXJpZmllZCI6ZmFsc2UsInN1YiI6ImRlMDU5M2YwLTE4YTAtNDViMy1iYWVmLTI3NDQ2YTc1MjUzZCJ9LCJyb2xlIjoiYXV0aGVudGljYXRlZCIsImFhbCI6ImFhbDEiLCJhbXIiOlt7Im1ldGhvZCI6ImVtYWlsL3NpZ251cCIsInRpbWVzdGFtcCI6MTcxODM3MTQ4MH1dLCJzZXNzaW9uX2lkIjoiNDIyMDMyMzgtNmZlMS00MGI4LThkOWEtZDIwYTUzZmIwZDJhIiwiaXNfYW5vbnltb3VzIjpmYWxzZX0.eSAIbKM_n1m0gL5rFZCWcYn7YU-3Lt9zTv6WWYT9chg",
            "X-Task-Id", "API-1",
            "Content-Type", "application/json",
            "accept", "application/json");
}

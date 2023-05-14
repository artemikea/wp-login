package org.example;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        Map<String, String> formData = new HashMap<>();
        formData.put("log", "log");
        formData.put("pwd", "pass");
        formData.put("wp-submit", "Log In");
        formData.put("redirect_to", "http://localhost/wordpress/wp-admin/");
        formData.put("testcookie", "1");

        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).build();

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(getFormDataAsString(formData)))
                .header("Cookie", "wordpress_test_cookie=WP%20Cookie%20check;")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create("http://localhost/wordpress/wp-login.php"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode());

        String cookies = response.headers().allValues("Set-Cookie").toString().replaceAll(",", ";");
        request = HttpRequest.newBuilder()
                .GET()
                .header("Cookie", cookies)
                .uri(URI.create("http://localhost/wordpress/wp-admin/"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode());
    }

    private static String getFormDataAsString(Map<String, String> formData) {
        StringBuilder formBodyBuilder = new StringBuilder();
        for (Map.Entry<String, String> singleEntry : formData.entrySet()) {
            if (formBodyBuilder.length() > 0) {
                formBodyBuilder.append("&");
            }
            formBodyBuilder.append(URLEncoder.encode(singleEntry.getKey(), StandardCharsets.UTF_8));
            formBodyBuilder.append("=");
            formBodyBuilder.append(URLEncoder.encode(singleEntry.getValue(), StandardCharsets.UTF_8));
        }
        return formBodyBuilder.toString();
    }
}
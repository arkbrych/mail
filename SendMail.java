package com.mail;

import com.configuration.ApplicationConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class SendMail {

    private final ApplicationConfig aConfig;

    public void sendMailMethod(String subject, String html, List<String> recipient, String from) throws IOException {
        try {

            // setup variables
            String appKey = aConfig.getEmailLabs().getAppKey();
            String secretKey = aConfig.getEmailLabs().getSecretKey();

            String userpass = appKey + ":" + secretKey;
            String basicAuth = "Basic "
                    + Base64.getEncoder().encodeToString(userpass.getBytes(StandardCharsets.UTF_8));

            InputStream imageStream = getClass().getClassLoader().getResourceAsStream("templates/LOGO.png");
            if (imageStream == null) {
                throw new IOException("Nie można znaleźć pliku: templates/LOGO.png w resources");
            }
            byte[] imageData = imageStream.readAllBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageData);
            imageStream.close();
            // set params
            HashMap<String, String> params = new HashMap<>();
            params.put("smtp_account", aConfig.getEmailLabs().getSmtpAccount());
            params.put("subject", subject);
            params.put("html", html);
            params.put("from", from + "@com.pl");
            params.put("to[" + recipient.getFirst() + "][message_id]", "msgid-001@domena.com");
            params.put("files[0][name]", "LOGO.png");
            params.put("files[0][mime]", "image/png");
            params.put("files[0][content]", base64Image);
            params.put("files[0][inline]", "LOGO.png");

            // build query
            StringBuilder query = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    query.append("&");
                query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
                query.append("=");
                query.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }

            // setup connection
            URL url = new URL(aConfig.getEmailLabs().getApiUrl());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", basicAuth);
            connection.setDoOutput(true);

            // send data
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(query.toString());
            out.close();

            int status = connection.getResponseCode();
            InputStream responseStream = (status < HttpURLConnection.HTTP_BAD_REQUEST)
                    ? connection.getInputStream()
                    : connection.getErrorStream();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    log.debug(line);
                }
            }

        } catch (MalformedURLException e) {
            throw new MalformedURLException("Niepoprawny adres URL: " + e.getMessage());
        } catch (ProtocolException e) {
            throw new ProtocolException("Błąd protokołu HTTP: " + e.getMessage());
        } catch (SocketTimeoutException e) {
            throw new SocketTimeoutException("Przekroczono czas oczekiwania na odpowiedź: " + e.getMessage());
        } catch (IOException e) {
            throw new IOException("Błąd wejścia/wyjścia podczas komunikacji: " + e.getMessage());
        }
    }
}


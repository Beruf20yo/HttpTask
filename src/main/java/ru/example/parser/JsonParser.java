package ru.example.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import ru.example.posts.NasaPost;
import ru.example.posts.VotesPost;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class JsonParser{
    private InputStream content;

    private void makeConnection(String remoteServiceUrl) {
        CloseableHttpClient client = HttpClientBuilder.create()
                .setUserAgent("My Test Service")
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000) // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000) // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();
        HttpGet request = new HttpGet(remoteServiceUrl);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        CloseableHttpResponse response;
        try {
            response = client.execute(request);
            this.content = response.getEntity().getContent();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /*
     Выводим в консоль все утверждения о котах с голосами > 0
     */
    public void parseVotes(String catsRemoteServiceUrl)  throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        makeConnection(catsRemoteServiceUrl);
        List<VotesPost> votesPosts = mapper.readValue(content, new TypeReference<List<VotesPost>>() {});
        votesPosts.stream()
                .filter(vote -> vote.getUpvotes() > 0)
                .map(VotesPost::getText)
                .forEach(System.out::println);
    }

    public void parseNasa(String nasaRemoteServiceUrl) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        makeConnection(nasaRemoteServiceUrl);
        NasaPost nasaPost = mapper.readValue(content, new TypeReference<NasaPost>() {});
        String url = nasaPost.getUrl();
        String fileName = url.substring(url.lastIndexOf("/" ) + 1);
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            System.out.println("Ошибка записи в файл");
        }
    }




}

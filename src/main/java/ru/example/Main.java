package ru.example;

import ru.example.parser.JsonParser;
import ru.example.posts.VotesPost;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        String catsRemoteServiceUrl = "https://raw.githubusercontent.com/netology-code/jd-homeworks/master/http/task1/cats";
        String nasaRemoteServiceUrl = "https://api.nasa.gov/planetary/apod?api_key=9b0A0uKtbf1JE60u6AweSkQ0rJYQjv9EHBurBOkm";
        JsonParser jsonParser= new JsonParser();
        try {
            jsonParser.parseVotes(catsRemoteServiceUrl);
            jsonParser.parseNasa(nasaRemoteServiceUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
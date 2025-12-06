package com.lastminute.recruitment.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lastminute.recruitment.domain.WikiPage;
import com.lastminute.recruitment.domain.error.WikiPageNotFound;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JsonWikiClient  extends BaseWikiClient {

    private static final String BASE_PATH = "wikiscrapper/";

    private final ObjectMapper objectMapper;

    public JsonWikiClient() {
        this(new ObjectMapper());
    }

    public JsonWikiClient(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
    }

    @Override
    public WikiPage fetch(String link) {
        String resourceName = BASE_PATH + getPageFileName(link) + ".json";
        try (InputStream inputStream = openResource(resourceName)) {
            JsonNode root = objectMapper.readTree(inputStream);
            String title = textValue(root, "title");
            String content = textValue(root, "content");
            String selfLink = textValue(root, "selfLink");
            List<String> links = new ArrayList<>();
            JsonNode linksNode = root.path("links");
            if (linksNode.isArray()) {
                linksNode.forEach(node -> links.add(node.asText()));
            }
            return new WikiPage(title, content, selfLink, links);
        } catch (IOException e) {
            throw new WikiPageNotFound("Unable to load wiki page for link: " + link, e);
        }
    }

    private String textValue(JsonNode node, String fieldName) {
        JsonNode valueNode = node.get(fieldName);
        if (valueNode == null || valueNode.isNull()) {
            throw new WikiPageNotFound("Missing field '" + fieldName + "' in wiki page representation");
        }
        return valueNode.asText();
    }
}

package com.lastminute.recruitment.client;

import java.net.URI;
import java.io.InputStream;
import com.lastminute.recruitment.domain.WikiClient;
import com.lastminute.recruitment.domain.error.WikiPageNotFound;

public abstract class BaseWikiClient implements WikiClient {
    private final ClassLoader classLoader;

    public BaseWikiClient() {
        this.classLoader = BaseWikiClient.class.getClassLoader();
    }

    protected String getPageFileName(String link) {
        URI uri;
        try {
            uri = URI.create(link);
        } catch (IllegalArgumentException ex) {

            throw new WikiPageNotFound("Invalid wiki link: " + link, ex);
        }
        if (!uri.isAbsolute() || uri.getScheme() == null || uri.getHost() == null) {
            throw new WikiPageNotFound("Invalid wiki link: " + link);
        }
        String path = uri.getPath();
        if (path == null || path.isBlank()) {
            throw new WikiPageNotFound("Missing path component in link: " + link);
        }

        String sanitized = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
        int lastSlash = sanitized.lastIndexOf('/');
        if (lastSlash < 0) {
            throw new WikiPageNotFound("Unable to determine page name from link: " + link);
        }

        return sanitized.substring(lastSlash + 1);
    }

    protected InputStream openResource(String resourceName) {
        InputStream inputStream = classLoader.getResourceAsStream(resourceName);
        if (inputStream == null) {
            throw new WikiPageNotFound("Resource not found: " + resourceName);
        }
        return inputStream;
    }
}
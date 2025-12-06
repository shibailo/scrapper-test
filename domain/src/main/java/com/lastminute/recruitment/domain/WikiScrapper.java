package com.lastminute.recruitment.domain;

import com.lastminute.recruitment.domain.error.InvalidRootLink;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class WikiScrapper {

    private final WikiClient wikiClient;

    public WikiScrapper(WikiClient wikiClient) {
        this.wikiClient = Objects.requireNonNull(wikiClient, "wikiClient must not be null");
    }

    public List<WikiPage> scrap(String rootLink) throws InvalidRootLink {
        String sanitizedRoot = sanitize(rootLink);

        Set<String> visited = new HashSet<>();
        List<WikiPage> scrapedPages = new ArrayList<>();
        Deque<String> toVisit = new ArrayDeque<>();
        toVisit.add(sanitizedRoot);

        while (!toVisit.isEmpty()) {
            String currentLink = toVisit.removeFirst();
            if (!visited.add(currentLink)) {
                continue;
            }

            WikiPage page = wikiClient.fetch(currentLink);
            scrapedPages.add(page);

            page.getLinks().stream()
                .filter(this::isValidLink)
                .filter(link -> !visited.contains(link))
                .forEach(toVisit::addLast);
        }

        return scrapedPages;
    }

    private String sanitize(String rootLink) throws InvalidRootLink {
        if (!isValidLink(rootLink)) {
            throw new InvalidRootLink("rootLink must not be blank");
        }
        return rootLink.trim();
    }

    private boolean isValidLink(String link) {
        return link != null && !link.isBlank();
    }
}

package com.lastminute.recruitment;

import com.lastminute.recruitment.domain.WikiClient;
import com.lastminute.recruitment.domain.WikiPage;
import com.lastminute.recruitment.domain.WikiScrapper;
import com.lastminute.recruitment.domain.error.InvalidRootLink;
import com.lastminute.recruitment.persistence.WikiPageRepository;

import java.util.List;
import java.util.Objects;

public class PersistingWikiScrapper extends WikiScrapper {

    private final WikiPageRepository repository;

    public PersistingWikiScrapper(WikiClient wikiClient, WikiPageRepository repository) {
        super(wikiClient);
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    @Override
    public List<WikiPage> scrap(String rootLink) throws InvalidRootLink {
        List<WikiPage> pages = super.scrap(rootLink);
        pages.forEach(repository::save);
        return pages;
    }
}

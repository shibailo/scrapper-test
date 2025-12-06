package com.lastminute.recruitment;

import com.lastminute.recruitment.domain.WikiClient;
import com.lastminute.recruitment.domain.WikiPage;
import com.lastminute.recruitment.domain.error.InvalidRootLink;
import com.lastminute.recruitment.persistence.WikiPageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PersistingWikiScrapperTest {

    private WikiClient wikiClient;
    private WikiPageRepository repository;
    private PersistingWikiScrapper scrapper;

    @BeforeEach
    void setUp() {
        wikiClient = mock(WikiClient.class);
        repository = mock(WikiPageRepository.class);
        scrapper = new PersistingWikiScrapper(wikiClient, repository);
    }

    @Test
    void scrap_shouldPersistAllFetchedPages() throws InvalidRootLink {
        var root = page("Root", "Root Content", "http://wikiscrapper.test/root", List.of(
            "http://wikiscrapper.test/child"
        ));
        var child = page("Child", "Child Content", "http://wikiscrapper.test/child", List.of());

        when(wikiClient.fetch("http://wikiscrapper.test/root")).thenReturn(root);
        when(wikiClient.fetch("http://wikiscrapper.test/child")).thenReturn(child);

        var scraped = scrapper.scrap("http://wikiscrapper.test/root");

        assertThat(scraped).containsExactlyInAnyOrder(root, child);
        verify(repository).save(root);
        verify(repository).save(child);
        verifyNoMoreInteractions(repository);
    }

    private static WikiPage page(String title, String content, String selfLink, List<String> links) {
        return new WikiPage(title, content, selfLink, links);
    }
}

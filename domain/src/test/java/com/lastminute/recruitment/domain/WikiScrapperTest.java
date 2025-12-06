package com.lastminute.recruitment.domain;

import com.lastminute.recruitment.domain.error.InvalidRootLink;
import com.lastminute.recruitment.domain.error.WikiPageNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class WikiScrapperTest {

    private WikiClient wikiClient;
    private WikiScrapper scrapper;

    @BeforeEach
    void setUp() {
        wikiClient = mock(WikiClient.class);
        scrapper = new WikiScrapper(wikiClient);
    }

    @Test
    void scrap_shouldTraverseAllReachablePages() throws InvalidRootLink {
        WikiPage page1 = page("Site 1", "Content 1", "http://wikiscrapper.test/site1", List.of(
            "http://wikiscrapper.test/site2",
            "http://wikiscrapper.test/site3"
        ));
        WikiPage page2 = page("Site 2", "Content 2", "http://wikiscrapper.test/site2", List.of());
        WikiPage page3 = page("Site 3", "Content 3", "http://wikiscrapper.test/site3", List.of(
            "http://wikiscrapper.test/site4"
        ));
        WikiPage page4 = page("Site 4", "Content 4", "http://wikiscrapper.test/site4", List.of());

        when(wikiClient.fetch("http://wikiscrapper.test/site1")).thenReturn(page1);
        when(wikiClient.fetch("http://wikiscrapper.test/site2")).thenReturn(page2);
        when(wikiClient.fetch("http://wikiscrapper.test/site3")).thenReturn(page3);
        when(wikiClient.fetch("http://wikiscrapper.test/site4")).thenReturn(page4);

        List<WikiPage> scraped = scrapper.scrap("http://wikiscrapper.test/site1");

        assertEquals(4, scraped.size());
        assertEquals(
            Set.copyOf(List.of(
                "http://wikiscrapper.test/site1",
                "http://wikiscrapper.test/site2",
                "http://wikiscrapper.test/site3",
                "http://wikiscrapper.test/site4"
            )),
            toLinkSet(scraped)
        );

        verify(wikiClient).fetch("http://wikiscrapper.test/site1");
        verify(wikiClient).fetch("http://wikiscrapper.test/site2");
        verify(wikiClient).fetch("http://wikiscrapper.test/site3");
        verify(wikiClient).fetch("http://wikiscrapper.test/site4");
        verifyNoMoreInteractions(wikiClient);
    }

    @Test
    void scrap_shouldHandleCyclesWithoutInfiniteLoop() throws InvalidRootLink {
        WikiPage page1 = page("Site 1", "Content 1", "http://wikiscrapper.test/site1", List.of(
            "http://wikiscrapper.test/site2"
        ));
        WikiPage page2 = page("Site 2", "Content 2", "http://wikiscrapper.test/site2", List.of(
            "http://wikiscrapper.test/site1"
        ));

        when(wikiClient.fetch("http://wikiscrapper.test/site1")).thenReturn(page1);
        when(wikiClient.fetch("http://wikiscrapper.test/site2")).thenReturn(page2);

        List<WikiPage> scraped = scrapper.scrap("http://wikiscrapper.test/site1");

        assertEquals(2, scraped.size());
        assertEquals(
            Set.copyOf(List.of(
                "http://wikiscrapper.test/site1",
                "http://wikiscrapper.test/site2"
            )),
            toLinkSet(scraped)
        );

        verify(wikiClient).fetch("http://wikiscrapper.test/site1");
        verify(wikiClient).fetch("http://wikiscrapper.test/site2");
        verifyNoMoreInteractions(wikiClient);
    }

    @Test
    void scrap_shouldThrowWhenRootLinkMissing() {
        when(wikiClient.fetch("http://wikiscrapper.test/missing"))
            .thenThrow(new WikiPageNotFound("Page not found for link: http://wikiscrapper.test/missing"));

        assertThrows(WikiPageNotFound.class, () -> scrapper.scrap("http://wikiscrapper.test/missing"));

        verify(wikiClient).fetch("http://wikiscrapper.test/missing");
        verifyNoMoreInteractions(wikiClient);
    }

    @Test
    void scrap_shouldRejectBlankRootLink() {
        assertThrows(InvalidRootLink.class, () -> scrapper.scrap("   "));
        verifyNoInteractions(wikiClient);
    }

    private static WikiPage page(String title, String content, String selfLink, List<String> links) {
        return new WikiPage(title, content, selfLink, links);
    }

    private static Set<String> toLinkSet(List<WikiPage> pages) {
        return pages.stream()
            .map(WikiPage::getSelfLink)
            .collect(Collectors.toSet());
    }
}

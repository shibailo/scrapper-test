package com.lastminute.recruitment;

import com.lastminute.recruitment.domain.WikiScrapper;
import com.lastminute.recruitment.domain.error.InvalidRootLink;
import com.lastminute.recruitment.persistence.WikiPageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("json")
class WikiScrapperJsonProfileTest {

    @Autowired
    private WikiScrapper wikiScrapper;

    @Test
    void scrap_shouldLoadAllPagesFromJsonFixtures() throws InvalidRootLink {
        var pages = wikiScrapper.scrap("http://wikiscrapper.test/site2");

        assertThat(pages)
            .hasSize(5)
            .extracting("selfLink")
            .contains(
                "http://wikiscrapper.test/site1",
                "http://wikiscrapper.test/site2",
                "http://wikiscrapper.test/site3",
                "http://wikiscrapper.test/site4",
                "http://wikiscrapper.test/site5"
            );

    }
}

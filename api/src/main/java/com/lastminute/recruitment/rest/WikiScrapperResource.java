package com.lastminute.recruitment.rest;

import com.lastminute.recruitment.domain.WikiScrapper;
import com.lastminute.recruitment.domain.error.InvalidRootLink;
import com.lastminute.recruitment.domain.error.WikiPageNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/wiki")
@RestController
public class WikiScrapperResource {

    private final WikiScrapper wikiScrapper;

    public WikiScrapperResource(WikiScrapper wikiScrapper) {
        this.wikiScrapper = wikiScrapper;
    }

    @PostMapping("/scrap")
    public ResponseEntity<Void> scrapWikipedia(@RequestBody String link) {
        try {
            String sanitizedLink = sanitizeLink(link);
            wikiScrapper.scrap(sanitizedLink);
            
            return ResponseEntity.ok().build();
        } catch (WikiPageNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InvalidRootLink e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private String sanitizeLink(String link) {
        if (link == null) {
            throw new WikiPageNotFound("Invalid wiki link: null");
        }

        return link.trim().replace("\"", "");
    }
}

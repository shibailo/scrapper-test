package com.lastminute.recruitment.client;

import com.lastminute.recruitment.domain.WikiPage;
import com.lastminute.recruitment.domain.error.WikiPageNotFound;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HtmlWikiClient extends BaseWikiClient {

    private static final String BASE_PATH = "wikiscrapper/";

    @Override
    public WikiPage fetch(String link) {
        String resourceName = BASE_PATH + getPageFileName(link) + ".html";
        
        try (InputStream inputStream = openResource(resourceName)) {
            Document document = Jsoup.parse(inputStream, StandardCharsets.UTF_8.name(), "");

            Element meta = document.selectFirst("meta[selfLink]");
            Element titleElement = document.selectFirst(".title");
            Element contentElement = document.selectFirst(".content");

            if (meta == null || titleElement == null || contentElement == null) {
                throw new WikiPageNotFound("Malformed wiki page html: " + resourceName);
            }

            String selfLink = meta.attr("selfLink").trim();
            if (!selfLink.equals(link)) {
                throw new WikiPageNotFound("Link does not match page selfLink: " + link);
            }

            List<String> links = new ArrayList<>();
            document.select(".links a[href]")
                .forEach(element -> links.add(element.attr("href").trim()));

            return new WikiPage(
                titleElement.text().trim(),
                contentElement.text().trim(),
                meta.attr("selfLink").trim(),
                links
            );
        } catch (IOException e) {
            throw new WikiPageNotFound("Unable to load wiki page for link: " + link, e);
        }
    }
}

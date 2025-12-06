package com.lastminute.recruitment.domain;

import com.lastminute.recruitment.domain.error.WikiPageNotFound;

public interface WikiClient {

    WikiPage fetch(String link) throws WikiPageNotFound;
}

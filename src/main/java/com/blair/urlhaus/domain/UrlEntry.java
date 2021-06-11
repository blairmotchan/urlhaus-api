package com.blair.urlhaus.domain;

import java.time.LocalDateTime;
import java.util.List;

public class UrlEntry {
    private long id;
    private LocalDateTime dateAdded;
    private String url;
    private String urlStatus;
    private String threat;
    private List<String> tags;
    private String urlHausLink;
    private String reporter;
}

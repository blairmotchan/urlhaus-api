package com.blair.urlhaus.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;

public class CsvRow {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String id;
    private String dateAdded;
    private String url;
    private String status;
    private String threat;
    private String tags;
    private String urlHausLink;
    private String reporter;

    public CsvRow() {

    }

    public CsvRow(String id, String dateAdded, String url, String status, String threat, String tags, String urlHausLink, String reporter) {
        this.id = id;
        this.dateAdded = dateAdded;
        this.url = url;
        this.status = status;
        this.threat = threat;
        this.tags = tags;
        this.urlHausLink = urlHausLink;
        this.reporter = reporter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThreat() {
        return threat;
    }

    public void setThreat(String threat) {
        this.threat = threat;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getUrlHausLink() {
        return urlHausLink;
    }

    public void setUrlHausLink(String urlHausLink) {
        this.urlHausLink = urlHausLink;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    @Override
    public String toString() {
        return "(" +
                id + "," +
                "'" + dateAdded + "'," +
                "'" + url + "'," +
                "'" + status + "'," +
                "'" + threat + "'," +
                "'" + getTags() + "'::JSONB," +
                "'" + urlHausLink + "'," +
                "'" + reporter + "'" +
                ")";
    }

    public static final CellProcessor[] CELL_PROCESSORS =
            new CellProcessor[]{
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional()
            };

    public static final String[] HEADERS = {
            "id",
            "dateAdded",
            "url",
            "status",
            "threat",
            "tags",
            "urlHausLink",
            "reporter"
    };
}

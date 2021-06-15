package com.blair.urlhaus.domain;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;

public class CsvGeographyRow extends CsvRow {
    private String country;
    private String city;
    private String longitude;
    private String latitude;

    public CsvGeographyRow() {

    }

    public CsvGeographyRow(String id, String dateAdded, String url, String status, String threat, String tags, String urlHausLink, String reporter, String country, String city, String longitude, String latitude) {
        super(id, dateAdded, url, status, threat, tags, urlHausLink, reporter);
        this.country = country;
        this.city = city;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public static final String[] HEADERS = {
            "id",
            "dateAdded",
            "url",
            "status",
            "threat",
            "tags",
            "urlHausLink",
            "reporter",
            "country",
            "city",
            "longitude",
            "latitude"
    };


    public static final CellProcessor[] CELL_PROCESSORS =
            new CellProcessor[]{
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional()
            };
}

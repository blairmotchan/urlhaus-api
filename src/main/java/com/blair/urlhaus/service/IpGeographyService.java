package com.blair.urlhaus.service;

import com.blair.urlhaus.domain.Geography;
import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Location;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

@Service
public class IpGeographyService {
    private DatabaseReader reader;
    private boolean populateGeoLocationData;

    public IpGeographyService(
            @Value("${geo-lite-city-database}") String geoLiteCityDatabase
    ) {
        try {
            File database = ResourceUtils.getFile("classpath:" + geoLiteCityDatabase);
            reader = new DatabaseReader.Builder(database).withCache(new CHMCache()).build();
            populateGeoLocationData = true;
        } catch (Exception e) {
            populateGeoLocationData = false;
        }
    }

    public Geography getGeography(String ip) throws IOException, GeoIp2Exception {
        if (!populateGeoLocationData) {
            return getEmptyGeography();
        }

        InetAddress ipAddress = InetAddress.getByName(ip);
        CityResponse response = reader.city(ipAddress);
        Location location = response.getLocation();
        return new Geography(
                response.getCountry().getName(),
                response.getCity().getName(),
                location.getLatitude(),
                location.getLongitude()
        );
    }

    public static Geography getEmptyGeography() {
        return new Geography(
                null,
                null,
                null,
                null
        );
    }
}

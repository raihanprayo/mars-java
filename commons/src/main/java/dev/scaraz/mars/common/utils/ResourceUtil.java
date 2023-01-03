package dev.scaraz.mars.common.utils;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

public class ResourceUtil {

    public static <T> ResponseEntity<List<T>> pagination(Page<T> page, String baseUrl) {
        return pagination(page, new HttpHeaders(), baseUrl);
    }

    public static <T> ResponseEntity<List<T>> pagination(Page<T> page, HttpHeaders headers, String baseUrl) {
        return new ResponseEntity<>(
                page.getContent(),
                generatePaginationHeader(page, headers, baseUrl),
                HttpStatus.OK
        );
    }

    public static HttpHeaders generatePaginationHeader(Page<?> page, String baseUrl) {
        return generatePaginationHeader(page, new HttpHeaders(), baseUrl);
    }

    public static HttpHeaders generatePaginationHeader(Page<?> page, HttpHeaders headers, String baseUrl) {
        headers.add("X-Total-Count", Long.toString(page.getTotalElements()));
        headers.add("X-Sort", page.getSort().toString());

        String link = "";
        //noinspection SingleStatementInBlock
        if ((page.getNumber() + 1) < page.getTotalPages()) {
            link = "<" + generateUri(baseUrl, page.getNumber() + 1, page.getSize()) + ">; rel=\"next\",";
        }
        // prev link
        //noinspection SingleStatementInBlock
        if ((page.getNumber()) > 0) {
            link += "<" + generateUri(baseUrl, page.getNumber() - 1, page.getSize()) + ">; rel=\"prev\",";
        }
        // last and first link
        int lastPage = 0;
        //noinspection SingleStatementInBlock
        if (page.getTotalPages() > 0) {
            lastPage = page.getTotalPages() - 1;
        }
        link += "<" + generateUri(baseUrl, lastPage, page.getSize()) + ">; rel=\"last\",";
        link += "<" + generateUri(baseUrl, 0, page.getSize()) + ">; rel=\"first\"";
        headers.add(HttpHeaders.LINK, link);
        return headers;
    }

    private static String generateUri(String baseUrl, int page, int size) {
        return UriComponentsBuilder.fromUriString(baseUrl).queryParam("page", page).queryParam("size", size).toUriString();
    }

}

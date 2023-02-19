package dev.scaraz.mars.common.utils;

import dev.scaraz.mars.common.domain.response.UserDTO;
import dev.scaraz.mars.common.tools.TempFileResource;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ResourceUtil {

    public static <T, K> ResponseEntity<?> plainMappedResponse(boolean plain,
                                                                  boolean mapped,
                                                                  String baseUrl,
                                                                  Supplier<Page<T>> paged,
                                                                  @Nullable Supplier<List<T>> list,
                                                                  Function<T, K> keymapper) {
        if (!plain) {
            Page<T> page = paged.get();
            HttpHeaders headers = ResourceUtil.generatePaginationHeader(page, baseUrl);
            if (!mapped) {
                return new ResponseEntity<>(
                        page.getContent(),
                        headers,
                        HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(
                        page.stream().collect(Collectors.toMap(keymapper, t -> t)),
                        headers,
                        HttpStatus.OK);
            }
        }
        else {
            List<T> all = Optional.ofNullable(list)
                    .map(Supplier::get)
                    .orElseGet(() -> paged.get().getContent());
            if (!mapped) return new ResponseEntity<>(all, HttpStatus.OK);
            else {
                return new ResponseEntity<>(
                        all.stream().collect(Collectors.toMap(keymapper, t -> t)),
                        HttpStatus.OK
                );
            }
        }
    }

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


    public static ResponseEntity<FileSystemResource> download(File file) {
        return download(file, file.getName());
    }


    public static ResponseEntity<FileSystemResource> download(File file, String filename) {
        return download(file, filename, HttpStatus.OK);
    }


    public static ResponseEntity<FileSystemResource> download(File file, HttpHeaders headers) {
        return download(file, file.getName(), headers);
    }


    public static ResponseEntity<FileSystemResource> download(File file, HttpStatus status) {
        return download(file, file.getName(), status);
    }


    public static ResponseEntity<FileSystemResource> download(File file, String filename, HttpHeaders headers) {
        return download(file, filename, headers, HttpStatus.OK);
    }


    public static ResponseEntity<FileSystemResource> download(File file, String filename, HttpStatus status) {
        return download(file, filename, new HttpHeaders(), status);
    }


    public static ResponseEntity<FileSystemResource> download(File file, String filename, HttpHeaders headers, HttpStatus status) {
        return downloadpicker(file, filename, headers, status, false);
    }


    public static ResponseEntity<FileSystemResource> downloadAndDelete(File file) {
        return downloadAndDelete(file, file.getName());
    }

    public static ResponseEntity<FileSystemResource> downloadAndDelete(File file, String filename) {
        return downloadAndDelete(file, filename, new HttpHeaders(), HttpStatus.OK);
    }

    public static ResponseEntity<FileSystemResource> downloadAndDelete(File file, HttpHeaders headers) {
        return downloadAndDelete(file, file.getName(), headers, HttpStatus.OK);
    }

    public static ResponseEntity<FileSystemResource> downloadAndDelete(File file, HttpStatus status) {
        return downloadAndDelete(file, file.getName(), status);
    }

    public static ResponseEntity<FileSystemResource> downloadAndDelete(File file, String filename, HttpHeaders headers) {
        return downloadAndDelete(file, filename, headers, HttpStatus.OK);
    }

    public static ResponseEntity<FileSystemResource> downloadAndDelete(File file, String filename, HttpStatus status) {
        return downloadAndDelete(file, filename, new HttpHeaders(), status);
    }

    public static ResponseEntity<FileSystemResource> downloadAndDelete(File file, String filename, HttpHeaders headers, HttpStatus status) {
        return downloadpicker(file, filename, headers, status, true);
    }

    private static ResponseEntity<FileSystemResource> downloadpicker(File file, String filename, HttpHeaders headers, HttpStatus status, boolean delete) {
        FileSystemResource resource = new FileSystemResource(file) {
            @Override
            public InputStream getInputStream() throws IOException {
                if (!delete) return super.getInputStream();

                File attached = getFile();
                return new FileInputStream(attached) {
                    @Override
                    public void close() throws IOException {
                        super.close();
                        FileUtils.deleteQuietly(attached);
                    }
                };
            }
        };
        return new ResponseEntity<>(
                resource,
                attachHeader(file, filename, headers),
                status
        );
    }

    private static HttpHeaders attachHeader(File file, String filename, HttpHeaders headers) {
        if (headers.getContentType() == null) {
            String absolutePath = file.getAbsolutePath();
            String ext = absolutePath.substring(absolutePath.lastIndexOf(".") + 1)
                    .toLowerCase();

            MediaType mediaType = AppConstants.MimeType.MAPPED_MIME_TYPE.get(ext);
            headers.setContentType(mediaType);
        }

        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(filename)
                .build());
        headers.set("filename", filename);
        return headers;
    }
}

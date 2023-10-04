package dev.scaraz.mars.app.administration.config.client;

import feign.Response;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface WitelApi {


    /**
     * @param json {@link dev.scaraz.mars.common.domain.request.CreateTicketDTO}
     * @return http response
     */
    @PostMapping(path = "/internal/ticket/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    Response createTicket(@RequestBody String json);


}

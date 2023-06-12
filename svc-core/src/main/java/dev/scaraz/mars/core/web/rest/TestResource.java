package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.core.domain.db.ticket.Ticket;
import dev.scaraz.mars.core.repository.db.TicketRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class TestResource {

    private final TicketRepo ticketRepo;

    @GetMapping
    public ResponseEntity<?> testCreateTicket() {
        return new ResponseEntity<>(
                ticketRepo.save(new Ticket()),
                HttpStatus.OK
        );
    }

}

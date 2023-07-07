package dev.scaraz.mars.v1.core.util.generator;


import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.query.Query;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Slf4j
public class TicketNoGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object o) throws HibernateException {
        LocalDate now = LocalDate.now();
        String dateString = DateTimeFormatter.ofPattern("yyyyMMdd")
                .format(now);

        Query query = session
                .createQuery("select t from Ticket t where t.createdAt >= :instant")
                .setParameter("instant", now.atStartOfDay().toInstant(ZoneOffset.of("+07")));

        long total = query.getFetchSize();

        String totalStr = String.format("%06d", total);
        log.debug("Generating no ticket {}", totalStr);
        return dateString + totalStr;
    }

}

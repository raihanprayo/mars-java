package dev.scaraz.mars.core.v2.domain.order;

import dev.scaraz.mars.common.tools.enums.Witel;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Ticket {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String no;

    private String incidentNo;

    private String serviceNo;

    private Witel witel;

    private String sto;

}

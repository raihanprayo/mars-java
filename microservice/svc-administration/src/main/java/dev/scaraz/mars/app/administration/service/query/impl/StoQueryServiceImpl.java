package dev.scaraz.mars.app.administration.service.query.impl;

import dev.scaraz.mars.app.administration.service.query.StoQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StoQueryServiceImpl implements StoQueryService {
}

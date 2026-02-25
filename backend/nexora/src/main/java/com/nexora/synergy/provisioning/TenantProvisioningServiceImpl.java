package com.nexora.synergy.provisioning;

import org.flywaydb.core.Flyway;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.UUID;

@Service
public class TenantProvisioningServiceImpl implements TenantProvisioningService {

    private final JdbcTemplate masterJdbcTemplate;
    private final TenantRepository tenantRepository;
    private final OutboxService outboxService;

    private static final String DB_PREFIX = "tenant_";

    public TenantProvisioningServiceImpl(
            JdbcTemplate masterJdbcTemplate,
            TenantRepository tenantRepository,
            OutboxService outboxService
    ) {
        this.masterJdbcTemplate = masterJdbcTemplate;
        this.tenantRepository = tenantRepository;
        this.outboxService = outboxService;
    }

    @Override
    @Transactional
    public void provisionTenantDatabase(UUID tenantId) {

        String dbName = DB_PREFIX + tenantId.toString().replace("-", "");

        // Idempotency check
        if (databaseExists(dbName)) {
            return;
        }

        masterJdbcTemplate.execute("CREATE DATABASE " + dbName);

        DataSource tenantDataSource = createTenantDataSource(dbName);

        Flyway flyway = Flyway.configure()
                .dataSource(tenantDataSource)
                .locations("classpath:db/tenant")
                .baselineOnMigrate(true)
                .load();

        flyway.migrate();

        outboxService.saveEvent(
                "TenantProvisionedEvent",
                "{\"tenantId\":\"" + tenantId + "\"}"
        );
    }

    @Override
    public void createPrimaryCampusSchema(UUID tenantId, String schemaName) {

        String dbName = DB_PREFIX + tenantId.toString().replace("-", "");

        DataSource tenantDataSource = createTenantDataSource(dbName);

        JdbcTemplate tenantJdbc = new JdbcTemplate(tenantDataSource);

        if (schemaExists(tenantJdbc, schemaName)) {
            return;
        }

        tenantJdbc.execute("CREATE SCHEMA " + schemaName);

        Flyway flyway = Flyway.configure()
                .dataSource(tenantDataSource)
                .schemas(schemaName)
                .locations("classpath:db/campus")
                .baselineOnMigrate(true)
                .load();

        flyway.migrate();

        outboxService.saveEvent(
                "PrimaryCampusCreatedEvent",
                "{\"tenantId\":\"" + tenantId + "\",\"schema\":\"" + schemaName + "\"}"
        );
    }

    private boolean databaseExists(String dbName) {
        Integer count = masterJdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pg_database WHERE datname = ?",
                Integer.class,
                dbName
        );
        return count != null && count > 0;
    }

    private boolean schemaExists(JdbcTemplate jdbcTemplate, String schemaName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name = ?",
                Integer.class,
                schemaName
        );
        return count != null && count > 0;
    }

    private DataSource createTenantDataSource(String dbName) {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/" + dbName);
        dataSource.setUsername("postgres");
        dataSource.setPassword("root");

        return dataSource;
    }
}

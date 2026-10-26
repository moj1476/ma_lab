package com.example.blog.migration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FlywayMigrationTest {

    private DriverManagerDataSource database() {
        return new DriverManagerDataSource("jdbc:h2:mem:" + UUID.randomUUID()
                + ";MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
    }

    private Flyway flyway(DriverManagerDataSource dataSource) {
        return Flyway.configure().dataSource(dataSource)
                .locations("classpath:db/migration/structure", "classpath:db/migration/data")
                .baselineOnMigrate(true).baselineVersion("0").load();
    }

    @Test
    void createsSchemaAndSeedsProductsOnlyOnce() {
        var dataSource = database();
        var flyway = flyway(dataSource);
        var jdbc = new JdbcTemplate(dataSource);

        assertThat(flyway.migrate().migrationsExecuted).isEqualTo(3);
        assertThat(jdbc.queryForList("SELECT name FROM PRODUCT ORDER BY id", String.class))
                .containsExactly("Product 1", "Product 2", "Product 3");
        assertThat(jdbc.queryForList("SELECT price FROM PRODUCT ORDER BY id", BigDecimal.class))
                .containsExactly(new BigDecimal("10.99"), new BigDecimal("19.99"), new BigDecimal("29.99"));
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM PRODUCT WHERE created_at IS NOT NULL "
                + "AND modified_at IS NOT NULL", Integer.class)).isEqualTo(3);
        jdbc.update("INSERT INTO users (name, email, created_at, updated_at) "
                + "VALUES ('Author', 'author@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");

        assertThat(flyway.migrate().migrationsExecuted).isZero();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM PRODUCT", Integer.class)).isEqualTo(3);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM users", Integer.class)).isEqualTo(1);
        flyway.validate();
    }

    @Test
    void adoptsExistingBlogWithoutLosingDataOrSkippingProductMigrations() {
        var dataSource = database();
        var jdbc = new JdbcTemplate(dataSource);
        var scripts = new ClassPathResource(
                "db/migration/structure/V2__CREATE_BLOG_TABLES.sql");
        new ResourceDatabasePopulator(scripts).execute(dataSource);
        jdbc.update("INSERT INTO users (name, email, created_at, updated_at) "
                + "VALUES ('Existing user', 'existing@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");

        var flyway = flyway(dataSource);
        assertThat(flyway.migrate().migrationsExecuted).isEqualTo(3);
        assertThat(jdbc.queryForObject("SELECT name FROM users WHERE email='existing@example.com'", String.class))
                .isEqualTo("Existing user");
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM PRODUCT", Integer.class)).isEqualTo(3);
        assertThat(flyway.info().current().getVersion().toString()).isEqualTo("2");
        flyway.validate();
    }
}

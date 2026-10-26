SELECT installed_rank, version, description, type, script, checksum, success
FROM flyway_schema_history
ORDER BY installed_rank;

SELECT id, name, description, price, created_at, modified_at
FROM PRODUCT
ORDER BY id;

SELECT COUNT(*) AS product_count FROM PRODUCT;

drop table if exists parking_violations;

create table parking_violations
(
    id                BIGINT IDENTITY PRIMARY KEY,
    summonsNumber     VARCHAR(20),
    plateId           VARCHAR(20),
    registrationState VARCHAR(20)
)
-- 主キーのカウンター
CREATE TABLE IF NOT EXISTS ID_SEQUENCE (
	SEQUENCE_NAME varchar(255) primary key,
	SEQUENCE_VALUE bigint NOT NULL
);

-- 顧客
CREATE TABLE IF NOT EXISTS customer (
	customer_id varchar primary key,
	first_name varchar NOT NULL,
	last_name varchar NOT NULL,
	birthday date,
	created_at timestamp NOT NULL,
	modified_at timestamp NOT NULL,
	version bigint NOT NULL
);

-- 社員
CREATE TABLE IF NOT EXISTS employee (
	id bigint auto_increment,
	name varchar NOT NULL,
	age int,
	role varchar,
	hire_date date,
	section_code varchar,
	business_establishment_code int,
	version bigint NOT NULL

);

-- 部門
CREATE TABLE IF NOT EXISTS section (
	code varchar NOT NULL,
	business_establishment_code int NOT NULL,
	name varchar,
	primary key(code, business_establishment_code)
);

-- 事業所
CREATE TABLE IF NOT EXISTS business_establishment (
	code int primary key,
	name varchar,
	address varchar,
	tel varchar
);

-- 顧客
CREATE TABLE IF NOT EXISTS customer (
	customer_id varchar primary key,
	first_name varchar NOT NULL,
	last_name varchar NOT NULL,
	birthday date,
	gender_type varchar NOT NULL,
	version bigint NOT NULL
);

-- 顧客住所
CREATE TABLE IF NOT EXISTS customer_address (
	customer_id varchar primary key,
	tel_number varchar,
	address varchar,
	version bigint NOT NULL
);


CREATE TABLE IF NOT EXISTS SAMPLE_IDENTITY2 (
	id1 bigint auto_increment,
	id2 bigint auto_increment,
	value varchar NOT NULL
);

CREATE SEQUENCE SAMPLE_SEQUENCE1_ID start with 1;

CREATE TABLE IF NOT EXISTS SAMPLE_SEQUENCE1 (
	id bigint primary key,
	value varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS SAMPLE_TABLE_ID1 (
	id bigint primary key,
	value varchar NOT NULL
);

/*
CREATE TABLE IF NOT EXISTS conv_lob (
	lob_id bigint primary key,
	text_data clob,
	binary_data blob

);

CREATE TABLE IF NOT EXISTS conv_enum (
	enum_id bigint primary key,
	enum_name varchar,
	enum_ordinary int

);

CREATE TABLE IF NOT EXISTS conv_xml (
	xml_id bigint primary key,
	text_data text,
	jaxb_data text,
	x_stream_data text
);
*/

CREATE TABLE IF NOT EXISTS ID_SEQUENCE (
	SEQUENCE_NAME varchar(255) primary key,
	SEQUENCE_VALUE bigint NOT NULL
);

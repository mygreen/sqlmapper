-- 社員
CREATE TABLE IF NOT EXISTS employee (
	id bigint auto_increment, -- ID : identity
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
	customer_id varchar primary key, -- ID : 手動
	first_name varchar NOT NULL,
	last_name varchar NOT NULL,
	birthday date,
	gender_type varchar,
	version bigint NOT NULL
);

-- 顧客住所
CREATE TABLE IF NOT EXISTS customer_address (
	customer_id varchar primary key,
	tel_number varchar,
	address varchar,
	version bigint NOT NULL
);

-- ID自動生成用のテスト用
CREATE TABLE IF NOT EXISTS test_generated_value_identity (
	id bigint auto_increment,
	comment varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS test_generated_value_identity2 (
	id1 bigint auto_increment,
	id2 bigint auto_increment,
	comment varchar NOT NULL
);

CREATE SEQUENCE test_sequence start with 1;

CREATE TABLE IF NOT EXISTS test_generated_value_sequence (
	id bigint primary key,
	comment varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS test_generated_value_sequence_format (
	id varchar primary key,
	comment varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS id_sequence (
	sequence_name varchar(255) primary key,
	sequence_value bigint NOT NULL
);

CREATE TABLE IF NOT EXISTS test_generated_value_table (
	id bigint primary key,
	comment varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS test_generated_value_table_format (
	id varchar primary key,
	comment varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS test_generated_value_uuid (
--	id uuid primary key,
	id varchar primary key,
	comment varchar NOT NULL
);

-- 各クラスタイプのテスト
CREATE TABLE IF NOT EXISTS test_type_value_lob (
	id bigint primary key,
	clob_data1 clob,
	clob_data2 clob,
	blob_data1 blob,
	blob_data2 blob,
	comment varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS test_type_value_enum (
	id bigint primary key,
	enum_ordinal_data int,
	enum_name_data varchar,
	comment varchar NOT NULL
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



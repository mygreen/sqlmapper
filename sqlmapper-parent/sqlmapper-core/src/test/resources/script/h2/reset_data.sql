-- 全てのテーブルのデータをリセットする

-- テーブルデータの削除
TRUNCATE table employee;
TRUNCATE table section;
TRUNCATE table business_establishment;
TRUNCATE table customer;
TRUNCATE table customer_address;
TRUNCATE table test_generated_value_identity;
TRUNCATE table test_generated_value_identity2;
TRUNCATE table test_generated_value_sequence;
TRUNCATE table test_generated_value_table;
TRUNCATE table test_generated_value_uuid;
TRUNCATE table id_sequence;

-- IDのリセット : ALTER TABLE <table_name> ALTER COLUMN <column_name> RESTART WITH 1
ALTER TABLE employee ALTER COLUMN id RESTART WITH 1;
ALTER TABLE test_generated_value_identity ALTER COLUMN id RESTART WITH 1;
ALTER TABLE test_generated_value_identity2 ALTER COLUMN id1 RESTART WITH 1;
--ALTER TABLE test_generated_value_identity2 ALTER COLUMN id2 RESTART WITH 1;

-- シーケンスのリセット : ALTER SEQUENCE <sequence_name> RESTART WITH 1;
ALTER SEQUENCE test_sequence RESTART WITH 1;


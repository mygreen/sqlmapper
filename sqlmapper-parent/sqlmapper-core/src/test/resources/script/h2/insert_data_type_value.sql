-- 列挙型
insert into test_type_value_enum values (1, 0, 'RED', '001@enum-test');
insert into test_type_value_enum values (2, 1, 'GREEN', '002@enum-test');

-- 時制型
insert into test_type_value_sql_date_time values (1, '2021-01-11', '01:02:03', '2021-01-11 01:02:03', '001@sql-datetime-test');
insert into test_type_value_sql_date_time values (2, '2021-02-12', '02:04:05', '2021-02-12 02:03:04', '002@sql-datetime-test');
insert into test_type_value_sql_date_time values (3, '2021-03-13', '03:04:05', '2021-03-13 03:04:05', '003@sql-datetime-test');

insert into test_type_value_util_date_time values (1, '2021-01-11', '01:02:03', '2021-01-11 01:02:03', '001@util-datetime-test');
insert into test_type_value_util_date_time values (2, '2021-02-12', '02:04:05', '2021-02-12 02:03:04', '002@util-datetime-test');
insert into test_type_value_util_date_time values (3, '2021-03-13', '03:04:05', '2021-03-13 03:04:05', '003@util-datetime-test');

insert into test_type_value_jsr310_date_time values (1, '2021-01-11', '01:02:03', '2021-01-11 01:02:03', '001@jsr310-datetime-test');
insert into test_type_value_jsr310_date_time values (2, '2021-02-12', '02:04:05', '2021-02-12 02:03:04', '002@jsr310-datetime-test');
insert into test_type_value_jsr310_date_time values (3, '2021-03-13', '03:04:05', '2021-03-13 03:04:05', '003@jsr310-datetime-test');

-- 数値型
insert into test_type_value_integer_number values (0, null, null, null, 'null@integer-number-test');
insert into test_type_value_integer_number values (1, 1, 10, 100, '001@integer-number-test');
insert into test_type_value_integer_number values (2, 2, 20, 200, '002@integer-number-test');
insert into test_type_value_integer_number values (3, 3, 30, 300, '003@integer-number-test');

insert into test_type_value_decimal_number values (0, null, null, null, 'null@decimal-number-test');
insert into test_type_value_decimal_number values (1, 1.1, 10.11, 100.111, '001@decimal-number-test');
insert into test_type_value_decimal_number values (2, 2.2, 20.22, 200.222, '002@decimal-number-test');
insert into test_type_value_decimal_number values (3, 3.3, 30.33, 300.333, '003@decimal-number-test');

insert into test_type_value_primitive_number values (0, null, null, null, null, null, 'null@primivie-number-test');
insert into test_type_value_primitive_number values (1, 1, 10, 100, 1.1, 10.11, 'null@primivie-number-test');
insert into test_type_value_primitive_number values (2, 2, 20, 200, 2.2, 20.22, '001@primivie-number-test');
insert into test_type_value_primitive_number values (3, 3, 30, 300, 3.3, 30.33, '002@primivie-number-test');
insert into test_type_value_primitive_number values (4, 4, 40, 400, 4.4, 40.44, '003@primivie-number-test');
insert into test_type_value_primitive_number values (5, 5, 50, 500, 5.5, 50.55, '003@primivie-number-test');


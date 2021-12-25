-- 列挙型
insert into test_type_value_enum values (1, 0, 'RED', 'enumtest-001');
insert into test_type_value_enum values (2, 1, 'GREEN', 'enumtest-002');

-- 時制型
insert into test_type_value_sql_date_time values (1, '2021-01-11', '01:02:03', '2021-01-11 01:02:03', 'sql-datetimetest-001');
insert into test_type_value_sql_date_time values (2, '2021-02-12', '02:04:05', '2021-02-12 02:03:04', 'sql-datetimetest-002');
insert into test_type_value_sql_date_time values (3, '2021-03-13', '03:04:05', '2021-03-13 03:04:05', 'sql-datetimetest-003');

insert into test_type_value_util_date_time values (1, '2021-01-11', '01:02:03', '2021-01-11 01:02:03', 'util-datetimetest-001');
insert into test_type_value_util_date_time values (2, '2021-02-12', '02:04:05', '2021-02-12 02:03:04', 'util-datetimetest-002');
insert into test_type_value_util_date_time values (3, '2021-03-13', '03:04:05', '2021-03-13 03:04:05', 'util-datetimetest-003');

insert into test_type_value_jsr310_date_time values (1, '2021-01-11', '01:02:03', '2021-01-11 01:02:03', 'jsr310-datetimetest-001');
insert into test_type_value_jsr310_date_time values (2, '2021-02-12', '02:04:05', '2021-02-12 02:03:04', 'jsr310-datetimetest-002');
insert into test_type_value_jsr310_date_time values (3, '2021-03-13', '03:04:05', '2021-03-13 03:04:05', 'jsr310-datetimetest-003');

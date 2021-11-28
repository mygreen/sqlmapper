-- 部門
insert into section values ('000', 1, '人事本部');
insert into section values ('010', 1, '営業本部');
insert into section values ('020', 1, '開発本部');

insert into section values ('001', 2, '人事課1');
insert into section values ('002', 2, '人事課2');
insert into section values ('011', 2, '営業課1');
insert into section values ('012', 2, '営業課2');
insert into section values ('021', 2, '開発課1');
insert into section values ('022', 2, '開発課2');

-- 事業所
insert into business_establishment values (1, '東京本社', '東京都港区虎ノ門〇×▽', '0120-11-2233');
insert into business_establishment values (2, '神奈川支社', '神奈川県相模原市〇×▽', '0120-11-4455');

-- 社員
insert into employee (name, age, role, hire_date, section_code, business_establishment_code, version) values ('山田太郎', 50, 'CEO', '1970-01-23', '000', 1, 0);
insert into employee (name, age, role, hire_date, section_code, business_establishment_code, version) values ('開発次郎', 40, 'ENGINEER', '2020-10-01', '021', 2, 0);


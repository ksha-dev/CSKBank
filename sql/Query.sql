select * from customers;
select * from accounts;
select * from transactions;
select * from branch;
select * from users;
select * from employees;
select * from credentials;

describe transactions;

use CSKBank;

insert into users(first_name, last_name, date_of_birth, gender, address, mobile, email, type) value ('ADMIN', 'USER', 1, 'MALE', 'Address', 9876543210, 'admin@email.com', 'EMPLOYEE');
insert into credentials value(1, 'd3fc50c8f714cebd16d6c827826df01205bf519529f9d34775293cf9b70a420e');
insert into branch(address, phone, email, ifsc_code) value('Karaikudi', 9775635324, 'karakudi@cskbank.in', 'CSKB0001111');
insert into employees(user_id, role, branch_id) value(1, '0', 1);

alter table transactions drop primary key;
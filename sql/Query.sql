select * from customers;
select * from accounts;
select * from transactions;
select * from branch;
select * from users;accounts
select * from employees;
select * from credentials;

select * from users where user_id = '1' or first_name = 'Sharan';
use CSKBank;
select balance, status from accounts where account_number = 5;

insert into users(first_name, last_name, date_of_birth, gender, address, mobile, email, type) value ('ADMIN', 'USER', 1, 'MALE', 'Address', 9876543210, 'admin@email.com', 'EMPLOYEE');
insert into credentials value(1, 'd3fc50c8f714cebd16d6c827826df01205bf519529f9d34775293cf9b70a420e');
insert into branch(address, phone, email, ifsc_code) value('Karaikudi', 9775635324, 'karakudi@cskbank.in', 'CSKB0001111');
insert into employees(user_id, role, branch_id) value(1, '0', 1);

alter table transactions drop primary key;
alter table transactions add primary key (transaction_id, user_id, viewer_account_number);
drop table transactions;

update users set user_id = 1 where user_id = 1;
update credentials set password = 'd3fc50c8f714cebd16d6c827826df01205bf519529f9d34775293cf9b70a420e' where user_id = 1;
update credentials set pin = '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4';
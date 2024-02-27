select accounts.user_id from employees left join accounts on employees.branch_id = accounts.branch_id;
select accounts.account_number, accounts.branch_id from customers  join accounts on customers.user_id = accounts.user_id where customers.user_id in 
(select accounts.user_id from employees left join accounts on employees.branch_id = accounts.branch_id); 

select accounts.account_number, accounts.branch_id from employees, accounts, customers where customers.user_id in 
(select accounts.user_id from accounts where accounts.branch_id = employees.branch_id) ;

select distinct users.*, customers.* from employees, accounts, users, customers where employees.branch_id = accounts.branch_id and accounts.user_id = users.user_id;

select account_number from customers right join accounts on customers.user_id = accounts.user_id ;

select * from customers;
select * from accounts;
select * from transactions;
select * from branch;
select * from users;
select * from employees;

select * from credentials;


use CSKBank;
describe accounts;
describe transactions;
insert into accounts(user_id, type, branch_id, opening_date, balance, status) values(8, 'SAVINGS', 1, 100000000, 10000.0, 'ACTIVE');
create table credentials (
	user_id int unique,
    password varchar(45),
    foreign key(user_id) references users(user_id)
);

alter table users drop column password;

alter table users modify gender enum('MALE', 'FEMALE', 'OTHER');
alter table users modify status enum('ACTIVE', 'INACTIVE', 'BLOCKED') default 'ACTIVE';
alter table users modify type enum('CUSTOMER', 'EMPLOYEE') default 'CUSTOMER';

update customers set pan_number = 'ABCPK9034F';

insert into branch (address, phone, email, ifsc_code) value('Manikoondu, Erode', 8805588066, 'erode@cskbank.com', 'CSKB0001235');

insert into accounts(user_id, type, branch_id, opening_date, balance, status) value(1, 'SALARY', 2, 123456789, 0.0, 'ACTIVE');

INSERT INTO users(user_id, password, first_name, last_name, date_of_birth, gender, address, mobile, email, status, type) VALUE(3,'iMnT6E', 'Hello','There',9876543210,'MALE',
'Address',1234567890, 'email@emaiol.com', 'ACTIVE', 'CUSTOMER');

SELECT LAST_INSERT_ID();
update users set mobile = 9876885723;
alter table users modify user_id int not null auto_increment primary key;
delete from users where user_id in (2, 6, 7);

update users set user_id = 2 where user_id = 3;

select * from accounts, users where branch_id = 1 and users.user_id = accounts.user_id; -- query to get the account details with the user details

update users set first_name = "Ponvel" where user_id = 8;
update customers set user_id = 8 where user_id = 6;




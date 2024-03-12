use CSKBank;
select * from accounts;
update accounts set status = '0' where status = 'ACTIVE';

select * from transactions;
update transactions set transaction_type = '1' where transaction_type = 'CREDIT';

select * from users;
update users set gender = '0' where gender = '1';
update users set type = '0' where type = 'CUSTOMER';
update users set type = '1' where type = 'EMPLOYEE';
update users set type = '2' where user_id = 1;
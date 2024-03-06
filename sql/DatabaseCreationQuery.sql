drop database CSKBank;
create database CSKBank;
use CSKBank;

create table users (
	user_id int not null auto_increment,
    first_name varchar(20) not null,
    last_name varchar(20) not null,
    date_of_birth bigint not null,
    gender enum('MALE', 'FEMALE', 'OTHER') not null,
    address varchar(255) not null,
    mobile bigint not null,
    email varchar(100) not null,
    type enum('CUSTOMER', 'EMPLOYEE') not null default 'CUSTOMER',
    primary key(user_id)
);

create table credentials (
	user_id int not null unique,
    password varchar(255) not null,
    pin int not null,
    foreign key (user_id) references users(user_id) on delete no action
);

create table customers (
	user_id int not null unique,
    aadhaar_number bigint not null unique,
    pan_number varchar(45) not null unique,
    foreign key (user_id) references users(user_id) on delete no action
);

create table branch (
	branch_id int not null auto_increment,
    address varchar(255) not null,
    phone bigint not null,
    email varchar(100) not null,
    ifsc_code varchar(20) not null unique,
    primary key (branch_id)
);

create table employees (
	user_id int not null unique,
    role enum('0','1') not null default '0',
	branch_id int not null,
    foreign key (user_id) references users(user_id) on delete no action,
    foreign key (branch_id) references branch(branch_id) on delete no action
);

create table accounts (
	account_number bigint not null auto_increment,
    user_id int not null,
    type enum('SAVINGS', 'SALARY', 'CURRENT') not null default 'SAVINGS',
    branch_id int not null,
    opening_date bigint not null,
    balance double not null,
    status enum('ACTIVE', 'INACTIVE', 'CLOSED', 'FROZEN') not null default 'ACTIVE',
    primary key (account_number),
    foreign key (user_id) references users(user_id) on delete no action,
	foreign key (branch_id) references branch(branch_id) on delete no action
);

create table transactions (
	transaction_id bigint not null auto_increment,
    user_id int not null,
    viewer_account_number bigint not null,
    transacted_account_number bigint null,
    transacted_amount double not null,
    transaction_type enum('CREDIT', 'DEBIT') not null,
    closing_balance double not null,
    time_stamp bigint not null,
    remarks varchar(255) not null,
    primary key (transaction_id, user_id, viewer_account_number),
    foreign key (viewer_account_number) references accounts(account_number) on delete no action
);

create table authorization_code (
	user_id int not null unique,
    authorization_pin int not null,
    foreign key (user_id) references users(user_id) on delete no action
);
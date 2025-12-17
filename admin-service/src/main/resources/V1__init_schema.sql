create table if not exists t_user (
                                      id bigserial primary key,
                                      login varchar(255) not null,
                                      password varchar(255) not null,
                                      role varchar(255) not null
);

create table if not exists company (
                                       id bigserial primary key,
                                       address varchar(255),
                                       city varchar(255),
                                       name varchar(255),
                                       user_id bigint
);

create table if not exists employee (
                                        id bigserial primary key,
                                        add_info varchar(255),
                                        birth_date date,
                                        city varchar(255),
                                        email varchar(255),
                                        experience int,
                                        father_name varchar(255),
                                        first_name varchar(255),
                                        gender varchar(255),
                                        last_name varchar(255),
                                        main_photo varchar(255),
                                        phone varchar(255),
                                        resume varchar(255),
                                        telegram varchar(255),
                                        user_id bigint not null
);

create table if not exists user_work_categories (
                                                    user_id bigint not null,
                                                    work_category varchar(255) not null
);

create table if not exists vacancy (
                                       id bigserial primary key,
                                       add_info varchar(255),
                                       busy varchar(255),
                                       experience int not null,
                                       income_level varchar(255),
                                       list_url varchar(255),
                                       title varchar(255) not null,
                                       work_schedule varchar(255) not null,
                                       work_type varchar(255) not null,
                                       working_hours int not null,
                                       company_id bigint not null
);

create table if not exists response_application (
                                                    id bigserial primary key,
                                                    company_id bigint,
                                                    created_at timestamp,
                                                    employee_login varchar(255),
                                                    status varchar(255),
                                                    updated_at timestamp,
                                                    vacancy_id bigint
);

create table if not exists chat (
                                    id bigserial primary key,
                                    company_id bigint not null,
                                    created_at timestamp,
                                    employee_login varchar(255) not null,
                                    vacancy_id bigint not null
);

create table if not exists message (
                                       id bigserial primary key,
                                       created_at timestamp,
                                       sender_login varchar(255) not null,
                                       sender_role varchar(255) not null,
                                       text varchar(255) not null,
                                       chat_id bigint not null references chat(id)
);

create index if not exists idx_company_name on company(name);
create index if not exists idx_chat_company on chat(company_id);
create index if not exists idx_chat_created_at on chat(created_at);
create index if not exists idx_msg_chat_created_at on message(chat_id, created_at);
create index if not exists idx_resp_company on response_application(company_id);
create index if not exists idx_resp_created_at on response_application(created_at);

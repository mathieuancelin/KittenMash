# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table cat (
  id                        bigint not null,
  url                       varchar(255),
  picked                    bigint not null,
  constraint pk_shorten primary key (id))
;

create sequence cat_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists shorten;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists cat_seq;


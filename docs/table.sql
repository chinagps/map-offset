-- BAIDU_OFFSET °Ù¶ÈµØÍ¼Æ«ÒÆ
create table BAIDU_OFFSET
(
  LON        NUMBER(5,2) not null,
  LAT        NUMBER(5,2) not null,
  OFF_LON    NUMBER(9,6),
  OFF_LAT    NUMBER(9,6)
);
alter table BAIDU_OFFSET
  add constraint BAIDU_OFFSET_PK primary key (LON, LAT)
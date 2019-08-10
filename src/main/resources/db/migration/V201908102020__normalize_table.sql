alter table lessons change k id int auto_increment;

alter table lessons change subject subject_id int not null;

alter table lessons change theme topic_id int not null;

alter table lessons change user student_id int default 1 not null;

rename table lessons to lesson;


alter table subject change k id int auto_increment;

alter table subject change user student_id int default 1 not null;


alter table theme change k id int auto_increment;

alter table theme change subject subject_id int not null;

alter table theme change user student_id int default 1 not null;

rename table theme to topic;


alter table users_eit change k id int auto_increment;

rename table users_eit to student;

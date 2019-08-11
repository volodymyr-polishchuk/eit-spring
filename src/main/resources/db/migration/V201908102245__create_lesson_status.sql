create table lesson_status
(
	id int auto_increment,
	name varchar(256) not null,
	constraint lesson_status_pk
		primary key (id)
);

INSERT INTO lesson_status(id, name)
VALUES
(1, 'ACTIVE'),
(2, 'CANCELED'),
(3, 'FINISHED');

alter table lesson change active lesson_status_id int not null;

UPDATE lesson_status SET id = 0 WHERE name LIKE 'FINISHED';

alter table lesson
	add constraint lesson_lesson_status_id_fk
		foreign key (lesson_status_id) references lesson_status (id);

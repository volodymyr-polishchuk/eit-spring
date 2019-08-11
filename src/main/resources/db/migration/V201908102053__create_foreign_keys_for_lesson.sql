alter table lesson
	add constraint lesson_student_id_fk
		foreign key (student_id) references student (id)
			on update cascade on delete cascade;

alter table lesson
	add constraint lesson_subject_id_fk
		foreign key (subject_id) references subject (id)
			on update cascade on delete cascade;

DELETE FROM lesson WHERE id IN (370, 371);

alter table lesson
	add constraint lesson_topic_id_fk
		foreign key (topic_id) references topic (id)
			on update cascade on delete cascade;


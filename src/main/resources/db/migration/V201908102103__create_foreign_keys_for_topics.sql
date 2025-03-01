alter table topic
	add constraint topic_student_id_fk
		foreign key (student_id) references student (id)
			on update cascade on delete cascade;

alter table topic
	add constraint topic_subject_id_fk
		foreign key (subject_id) references subject (id)
			on update cascade on delete cascade;

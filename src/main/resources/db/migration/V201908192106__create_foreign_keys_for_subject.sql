alter table subject
	add constraint subject_student_id_fk
		foreign key (student_id) references student (id)
			on update cascade on delete cascade;

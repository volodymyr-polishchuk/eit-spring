alter table subject
	add active boolean default false not null;

UPDATE subject SET subject.active = TRUE;

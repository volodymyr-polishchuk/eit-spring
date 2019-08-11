alter table topic
	add active boolean default true not null;

UPDATE topic SET topic.active = TRUE;

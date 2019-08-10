DELETE FROM topic WHERE id IN (107, 108, 109, 112);


ALTER TABLE lesson ENGINE = InnoDB;

ALTER TABLE student ENGINE = InnoDB;

ALTER TABLE subject ENGINE = InnoDB;

ALTER TABLE topic ENGINE = InnoDB;


drop index subject on lesson;

drop index theme on lesson;

drop index user on lesson;


drop index fk_subject on topic;

drop index user on topic;

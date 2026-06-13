create table if not exists posts(
    id bigserial primary key,
    title varchar(256) not null,
    text varchar(2000) not null,
    tags varchar(2000) null,
    likesCount integer not null
);

create table if not exists postImages(
    postId integer not null,
    path varchar(1000) not null,

    FOREIGN KEY (postId) REFERENCES posts(id) ON DELETE CASCADE
);

create table if not exists comments(
    id bigserial primary key,
    postId integer not null,
    text varchar(2000) not null,

    FOREIGN KEY (postId) REFERENCES posts(id) ON DELETE CASCADE
);


insert into posts(title, text, tags, likesCount) values ('Пост 1', 'Первый пост про localhost', 'it,web', 2);
insert into posts(title, text, tags, likesCount) values ('Пост 2', 'Пост про IT', 'it', 0);
insert into posts(title, text, tags, likesCount) values ('Пост 3', 'Пост на отвлеченные темы', null, 0);


insert into comments(postId, text) values (1, 'Это твой первый длиннопост?');
insert into comments(postId, text) values (1, 'Поздравляю с открытием блога!');
insert into comments(postId, text) values (3, 'Наконец-то нормальная тема!');







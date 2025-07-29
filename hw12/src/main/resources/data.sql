insert into authors(full_name)
values ('Author_1'), ('Author_2'), ('Author_3');

insert into genres(name)
values ('Genre_1'), ('Genre_2'), ('Genre_3');

insert into books(title, author_id, genre_id)
values ('BookTitle_1', 1, 1), ('BookTitle_2', 2, 2), ('BookTitle_3', 3, 3);

insert into comments(text, book_id)
values ('Comment_1', 1), ('Comment_2', 1), ('Comment_3', 2), ('Comment_4', 3);

insert into roles(name)
values ('USER');

insert into users(username, password, role_id)
values ('test_username', '$2a$10$Zmmi5N0.O5FBl5CIZTPWWOf0aSIkKHzkeoLKRNOLC1SPUjv65udvW', 1);  --password=123

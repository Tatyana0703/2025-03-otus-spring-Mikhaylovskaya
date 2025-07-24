insert into authors(full_name)
values ('Author_1'), ('Author_2'), ('Author_3');

insert into genres(name)
values ('Genre_1'), ('Genre_2'), ('Genre_3');

insert into books(title, author_id, genre_id)
values ('BookTitle_1', 1, 1), ('BookTitle_2', 2, 2), ('BookTitle_3', 3, 3);

insert into comments(text, book_id)
values ('Comment_1', 1), ('Comment_2', 1), ('Comment_3', 2), ('Comment_4', 3);

insert into users(username, password, role)
values ('test_username', '{bcrypt}$2a$10$8tWV9gktsst6SuSv.ipbwOIHzY1CfCF6dTXT546WIQs.TFzroW.zm', 'USER');  --password=123

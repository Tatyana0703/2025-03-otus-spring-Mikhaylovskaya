insert into authors(full_name)
values ('Author_1'), ('Author_2'), ('Author_3');

insert into genres(name)
values ('Genre_1'), ('Genre_2'), ('Genre_3');

insert into books(title, author_id, genre_id)
values ('BookTitle_1', 1, 1), ('BookTitle_2', 2, 2), ('BookTitle_3', 3, 3);

insert into roles(name)
values ('USER'), ('VIEW');

insert into users(username, password)
values ('username_1', '{bcrypt}$2a$10$kOcVSHsXzB4OEP.8drhPUe62OAX4z06/VqFYi9YdJWiMFrL7pEYFm'),  --password=123
       ('username_2', '{bcrypt}$2a$10$kOcVSHsXzB4OEP.8drhPUe62OAX4z06/VqFYi9YdJWiMFrL7pEYFm');  --password=123

insert into users_roles(user_id, role_id)
values (1, 1), (1, 2), (2, 1);

insert into comments(text, book_id, user_id)
values ('Comment_1', 1, 1), ('Comment_2', 1, 1), ('Comment_3', 2, 2), ('Comment_4', 2, 2);

delete from comments;
delete from books;
delete from authors;
delete from genres;

set @author1 = select id from final table (insert into authors (full_name) values ('Test_Author_1'));
set @author2 = select id from final table (insert into authors (full_name) values ('Test_Author_2'));
set @author3 = select id from final table (insert into authors (full_name) values ('Test_Author_3'));

set @genre1 = select id from final table (insert into genres (name) values ('Test_Genre_1'));
set @genre2 = select id from final table (insert into genres (name) values ('Test_Genre_2'));
set @genre3 = select id from final table (insert into genres (name) values ('Test_Genre_3'));

set @book1 = select id from final table (insert into books (title, author_id, genre_id) values ('Test_BookTitle_1', @author1, @genre1));
set @book2 = select id from final table (insert into books (title, author_id, genre_id) values ('Test_BookTitle_2', @author2, @genre2));
set @book3 = select id from final table (insert into books (title, author_id, genre_id) values ('Test_BookTitle_3', @author3, @genre3));

insert into comments(text, book_id)
values ('Test_Comment_1', @book1),
       ('Test_Comment_2', @book1),
       ('Test_Comment_3', @book2),
       ('Test_Comment_4', @book3);
package ru.otus.example.springbatch.testchangelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.MongoDatabase;
import ru.otus.example.springbatch.mongo.AuthorMongo;
import ru.otus.example.springbatch.mongo.BookMongo;
import ru.otus.example.springbatch.mongo.CommentMongo;
import ru.otus.example.springbatch.mongo.GenreMongo;

@ChangeLog(order = "001")
public class InitMongoDBDataChangeLog {

    @ChangeSet(order = "000", id = "dropDB", author = "tmikhaylovskaya", runAlways = true)
    public void dropDB(MongoDatabase database){
        database.drop();
    }

    @ChangeSet(order = "001", id = "initBooks", author = "tmikhaylovskaya", runAlways = true)
    public void initBooks(MongockTemplate template){
        for (int i = 1; i < 15; i++) {
            AuthorMongo author = template.save(new AuthorMongo("Author %d".formatted(i)));
            GenreMongo genre = template.save(new GenreMongo("Genre %d".formatted(i)));
            BookMongo book = template.save(new BookMongo("Title %d".formatted(i), author, genre));
            template.save(new CommentMongo("Comment %d1".formatted(i), book));
            template.save(new CommentMongo("Comment %d2".formatted(i), book));
        }
    }
}
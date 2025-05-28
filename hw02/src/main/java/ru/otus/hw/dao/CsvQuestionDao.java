package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Repository
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        var filename = fileNameProvider.getTestFileName();

        InputStream inputStream = CsvQuestionDao.class.getClassLoader().getResourceAsStream(filename);
        if (isNull(inputStream)) {
            throw new QuestionReadException(String.format("Resource %s not found", filename));
        }

        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
            return new CsvToBeanBuilder<QuestionDto>(inputStreamReader)
                    .withType(QuestionDto.class)
                    .withSeparator(';')
                    .withSkipLines(1)
                    .build()
                    .parse()
                    .stream()
                    .map(QuestionDto::toDomainObject)
                    .toList();
        } catch (IOException e) {
            throw new QuestionReadException(String.format("Error reading file %s", filename), e);
        }
    }
}
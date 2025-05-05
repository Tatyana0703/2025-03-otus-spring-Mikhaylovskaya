package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        var filename = fileNameProvider.getTestFileName();
        if (isNull(CsvQuestionDao.class.getClassLoader().getResource(filename))) {
            throw new QuestionReadException(String.format("Resource %s not found", filename));
        }

        List<QuestionDto> parseQuestions;
        try (InputStream inputStream = CsvQuestionDao.class.getClassLoader().getResourceAsStream(filename);
             InputStreamReader inputStreamReader = new InputStreamReader(requireNonNull(inputStream))) {
            parseQuestions = new CsvToBeanBuilder<QuestionDto>(inputStreamReader)
                    .withType(QuestionDto.class)
                    .withSeparator(';')
                    .withSkipLines(1)
                    .build()
                    .parse();
        } catch (IOException e) {
            throw new QuestionReadException(String.format("Error reading file %s", filename), e);
        }

        return parseQuestions.stream()
                .map(QuestionDto::toDomainObject)
                .collect(Collectors.toUnmodifiableList());
    }
}
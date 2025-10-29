package ru.otus.hw.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class AppProperties implements TestConfig, TestFileNameProvider {

    private int askingQuestionsCount;

    private int rightAnswersCountToPass;

    private String testFileName;

    public AppProperties(
            @Value("${test.askingQuestionsCount}") int askingQuestionsCount,
            @Value("${test.rightAnswersCountToPass}") int rightAnswersCountToPass,
            @Value("${test.fileName}") String testFileName
    ) {
        this.askingQuestionsCount = askingQuestionsCount;
        this.rightAnswersCountToPass = rightAnswersCountToPass;
        this.testFileName = testFileName;
    }
}

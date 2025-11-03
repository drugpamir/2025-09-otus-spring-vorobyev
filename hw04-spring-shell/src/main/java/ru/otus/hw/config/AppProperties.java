package ru.otus.hw.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;

@Setter
@Component
@ConfigurationProperties(prefix = "test")
public class AppProperties implements TestConfig, TestFileNameProvider, LocaleConfig {

    @Getter
    private int rightAnswersCountToPass;

    @Getter
    private Locale locale;

    private Map<String, String> fileNameByLocaleTag;

    public void setLocale(String locale) {
        this.locale = Locale.forLanguageTag(locale);
    }

    @Override
    public String getTestFileName() {
        String fileName = fileNameByLocaleTag.getOrDefault(
                locale.toLanguageTag(),
                Locale.US.toLanguageTag()
        );
        if (Paths.get(fileName).isAbsolute()) {
            return fileName;
        }
        return "/" + fileName;
    }
}

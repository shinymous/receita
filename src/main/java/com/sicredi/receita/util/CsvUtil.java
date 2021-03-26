package com.sicredi.receita.util;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class CsvUtil {

    public static List<String[]> leEConverteCsv(MultipartFile file) throws IOException {
        InputStream targetStream = file.getInputStream();
        CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build(); // separador
        try (CSVReader reader = new CSVReaderBuilder(
                new InputStreamReader(targetStream))
                .withCSVParser(csvParser)   // adiciona o parser
                .withSkipLines(1)           // pula a primeira linha
                .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                .build()) {
            return reader.readAll();
        }
    }
}

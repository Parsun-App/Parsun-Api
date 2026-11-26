package com.parsun.parsun_api.infrastructure.seed.valery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ValeryCsv {

    private static final Logger log = LoggerFactory.getLogger(ValeryCsv.class);
    private static final String SEPARATOR = "~";

    private ValeryCsv() {
    }

    public static List<String[]> load(String name) {
        String path = "seed/valery/" + name + ".csv.gz";
        try (InputStream stream = ValeryCsv.class.getClassLoader()
                .getResourceAsStream(path)) {
            if (stream == null) {
                log.warn("Valery seed file {} not found; step will be skipped",
                        path);
                return List.of();
            }
            List<String[]> rows = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new GZIPInputStream(stream),
                            StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }
                    rows.add(line.split(SEPARATOR, -1));
                }
            }
            return rows;
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    public static String text(String[] row, int index) {
        if (index >= row.length) {
            return "";
        }
        return row[index].trim();
    }

    public static BigDecimal decimal(String[] row, int index) {
        String raw = text(row, index);
        if (raw.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(raw);
        } catch (NumberFormatException exception) {
            return BigDecimal.ZERO;
        }
    }

    public static BigDecimal money(String[] row, int index) {
        return decimal(row, index).setScale(2, RoundingMode.HALF_UP);
    }

    public static LocalDate date(String[] row, int index) {
        String raw = text(row, index);
        if (raw.length() < 10) {
            return null;
        }
        try {
            return LocalDate.parse(raw.substring(0, 10));
        } catch (RuntimeException exception) {
            return null;
        }
    }

    public static String trim(String value, int max) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        return trimmed.length() <= max ? trimmed : trimmed.substring(0, max);
    }

    public static String orDefault(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}

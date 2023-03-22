package com.epam.mjc.nio;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class FileReader {
    private static final Logger logger = Logger.getLogger(FileReader.class.getName());
    //I would rather create enum right here, but we haven't studied that yet.
    private static final String NAME_FIELD = "Name";
    private static final String AGE_FIELD = "Age";
    private static final String EMAIL_FIELD = "Email";
    private static final String PHONE_FIELD = "Phone";
    private static final String FIELD_NAMES_PATTERN = String.join("|", List.of(NAME_FIELD, AGE_FIELD, EMAIL_FIELD, PHONE_FIELD));
    //Nevertheless, neither regular expressions nor String.split method were studied, so I've chosen the first option.
    private static final Pattern LINE_PATTERN = Pattern.compile(String.format("(%s): (.+)", FIELD_NAMES_PATTERN));

    private void updateProfileFromLine(Profile profile, String line) {
        var matcher = LINE_PATTERN.matcher(line);
        if (!matcher.find()) {
            throw ProfileParseException.ofInvalidLine(line, null);
        }

        try {
            updateProfile(profile, matcher.group(1), matcher.group(2));
        } catch (NumberFormatException exception) {
            throw ProfileParseException.ofInvalidLine(line, exception);
        }
    }

    private void updateProfile(Profile profile, String fieldName, String value) {
        switch (fieldName) {
            case NAME_FIELD -> profile.setName(value);
            case AGE_FIELD -> profile.setAge(Integer.parseInt(value));
            case EMAIL_FIELD -> profile.setEmail(value);
            case PHONE_FIELD -> profile.setPhone(Long.parseLong(value));
        }
    }

    public Profile getDataFromFile(File file) {
        var profile = new Profile();
        try (var reader = Files.newBufferedReader(file.toPath())) {
            String line;
            while (Objects.nonNull(line = reader.readLine())) {
                updateProfileFromLine(profile, line);
            }
        } catch (IOException | ProfileParseException exception) {
            logger.severe(MessageFormat.format("There was an error during file reading: {0}", exception.getMessage()));
        }
        return profile;
    }
}
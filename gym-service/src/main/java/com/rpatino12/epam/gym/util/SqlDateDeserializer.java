package com.rpatino12.epam.gym.util;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SqlDateDeserializer extends JsonDeserializer<Date> {

    // This SimpleDateFormat instance is used to parse the date string in the format "yyyy-MM-dd"
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    // This approach gives us full control over the deserialization process.
    // This way we can handle the date string exactly as we want and tell Jackson to use this custom
    // deserializer when parsing the date string, just by using @JsonDeserialize annotation in DTO class

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String date = p.getText();
        try {
            return new Date(format.parse(date).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}

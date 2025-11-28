package com.github.rickmvi.jtoolbox.json;

import com.github.rickmvi.jtoolbox.json.configuration.JsonXConfig;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class JsonXBuilder {

    private final GsonBuilder gsonBuilder;
    private boolean serializeNulls = false;
    private Charset charset = StandardCharsets.UTF_8;
    private boolean java8TimeSupport = false;
    private String java8DatePattern = "yyyy-MM-dd";
    private String java8DateTimePattern = "yyyy-MM-dd'T'HH:mm:ss";

    public JsonXBuilder() {
        this.gsonBuilder = new GsonBuilder();
    }

    public JsonXBuilder prettyPrint() {
        gsonBuilder.setPrettyPrinting();
        return this;
    }

    public JsonXBuilder serializeNulls() {
        gsonBuilder.serializeNulls();
        this.serializeNulls = true;
        return this;
    }

    public JsonXBuilder disableHtmlEscaping() {
        gsonBuilder.disableHtmlEscaping();
        return this;
    }

    public JsonXBuilder dateFormat(String pattern) {
        gsonBuilder.setDateFormat(pattern);
        return this;
    }

    public JsonXBuilder lenient() {
        gsonBuilder.setStrictness(Strictness.LENIENT);
        return this;
    }

    public JsonXBuilder charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public JsonXBuilder charsetUtf8() {
        this.charset = StandardCharsets.UTF_8;
        return this;
    }

    public JsonXBuilder fieldNamingPolicy(FieldNamingPolicy policy) {
        gsonBuilder.setFieldNamingPolicy(policy);
        return this;
    }

    public JsonXBuilder registerTypeAdapter(Class<?> type, Object typeAdapter) {
        gsonBuilder.registerTypeAdapter(type, typeAdapter);
        return this;
    }

    public JsonXBuilder withJava8TimeSupport() {
        this.java8TimeSupport = true;
        return this;
    }

    public JsonXBuilder withJava8TimeSupport(String datePattern, String dateTimePattern) {
        this.java8TimeSupport = true;
        this.java8DatePattern = datePattern;
        this.java8DateTimePattern = dateTimePattern;
        return this;
    }

    public JsonX build() {
        if (java8TimeSupport) {
            registerJava8Adapters();
        }

        Gson gson = gsonBuilder.create();
        JsonXConfig config = new JsonXConfig(serializeNulls, charset);

        return JsonX.initialize(gson, config);
    }

    private void registerJava8Adapters() {
        gsonBuilder.registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
            @Override
            public void write(JsonWriter out, LocalDate value) throws IOException {
                if (value == null) out.nullValue();
                else out.value(value.format(DateTimeFormatter.ofPattern(java8DatePattern)));
            }

            @Override
            public LocalDate read(JsonReader in) throws IOException {
                if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }
                return LocalDate.parse(in.nextString(), DateTimeFormatter.ofPattern(java8DatePattern));
            }
        });

        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
            @Override
            public void write(JsonWriter out, LocalDateTime value) throws IOException {
                if (value == null) out.nullValue();
                else out.value(value.format(DateTimeFormatter.ofPattern(java8DateTimePattern)));
            }

            @Override
            public LocalDateTime read(JsonReader in) throws IOException {
                if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }
                return LocalDateTime.parse(in.nextString(), DateTimeFormatter.ofPattern(java8DateTimePattern));
            }
        });

        gsonBuilder.registerTypeAdapter(LocalTime.class, new TypeAdapter<LocalTime>() {
            @Override
            public void write(JsonWriter out, LocalTime value) throws IOException {
                if (value == null) out.nullValue();
                else out.value(value.toString());
            }

            @Override
            public LocalTime read(JsonReader in) throws IOException {
                if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }
                return LocalTime.parse(in.nextString());
            }
        });
    }

}

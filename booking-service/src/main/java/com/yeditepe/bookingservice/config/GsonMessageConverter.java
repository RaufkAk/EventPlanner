package com.yeditepe.bookingservice.config;

import com.google.gson.Gson;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import java.nio.charset.StandardCharsets;

public class GsonMessageConverter implements MessageConverter {

    private final Gson gson;

    public GsonMessageConverter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Message toMessage(Object object, MessageProperties messageProperties)
            throws MessageConversionException {

        String json = gson.toJson(object);
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        messageProperties.setContentEncoding(StandardCharsets.UTF_8.name());

        return new Message(json.getBytes(StandardCharsets.UTF_8), messageProperties);
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        String json = new String(message.getBody(), StandardCharsets.UTF_8);
        return json; // ⚠️ TYPE BİLGİSİ YOK
    }
}
package org.knowm.xchange.kucoin.dto.trading;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Date;

public class KucoinDateDeserializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        int currentTokenId = jsonParser.getCurrentTokenId();

        if(currentTokenId != JsonTokenId.ID_NUMBER_INT)
            return null;

        Long longDateValue = jsonParser.getLongValue();
        return new Date(longDateValue);
    }
}


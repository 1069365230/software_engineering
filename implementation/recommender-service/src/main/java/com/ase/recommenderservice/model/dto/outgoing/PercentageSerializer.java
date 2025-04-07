package com.ase.recommenderservice.model.dto.outgoing;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class PercentageSerializer extends JsonSerializer<Double> {
    private static final DecimalFormatSymbols SYMBOLS = DecimalFormatSymbols.getInstance(Locale.US);
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("0.00%", SYMBOLS);

    @Override
    public void serialize(Double value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        if (value != null) {
            String formattedValue = PERCENT_FORMAT.format(value / 100);
            generator.writeString(formattedValue);
        }
    }
}

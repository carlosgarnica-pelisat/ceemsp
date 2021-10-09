package com.pelisat.cesp.ceemsp.restceemsp.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pelisat.cesp.ceemsp.infrastructure.exception.InvalidDataException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {
    private final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    public String getUserFromToken(String token) throws Exception {
        if(StringUtils.isBlank(token)) {
            logger.warn("The token is coming as empty. ");
            throw new InvalidDataException();
        }

        String[] jwtComponents = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();

        String payload = new String(decoder.decode(jwtComponents[1]));
        Map<String, String> tokenData = decodifyFromJson(payload);

        return tokenData.get("sub");
    }

    private Map<String, String> decodifyFromJson(String token) throws Exception {
        Map<String, String> jsonMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            jsonMap = objectMapper.readValue(token, new TypeReference<Map<String, String>>() {});
            return jsonMap;
        } catch (Exception ex) {
            throw ex;
        }
    }


}

package org.maas.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonConverter {
	private static ObjectMapper mapper = new ObjectMapper();
	
	public static String getJsonString(Object obj ) {
        try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
        return null;
	}

}

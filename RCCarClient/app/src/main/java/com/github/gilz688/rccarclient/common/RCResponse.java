package com.github.gilz688.rccarclient.common;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class RCResponse {
	private String request;
	private Map<String,Object> data;

	public String getCommand() {
		return request;
	}

	public RCResponse(String response) {
		super();
		this.request = response;
		this.data = new HashMap<>();
	}
	
	public void setResponse(String response) {
		this.request = response;
	}
	
	public void putData(String key, Object value) {
		data.put(key,value);
	}

    public Object getData(String key) {
        return data.get(key);
    }

    public String getJson(){
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        return gson.toJson(this, RCResponse.class);
    }

    public static RCResponse newInstanceFromJson(String json){
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        return gson.fromJson(json, RCResponse.class);
    }
}

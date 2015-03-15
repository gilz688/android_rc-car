package ph.edu.msuiit.rccarclient.common;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class RCCommand {
	private String cmd;
	private Map<String,Object> data;
	
	public String getCommand() {
		return cmd;
	}
	
	public RCCommand(String command) {
		super();
		this.cmd = command;
		this.data = new HashMap<>();
	}
	
	public void setCommand(String command) {
		this.cmd = command;
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

        return gson.toJson(this,RCCommand.class);
    }

    public static RCCommand newInstanceFromJson(String json){
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        return gson.fromJson(json, RCCommand.class);
    }
}

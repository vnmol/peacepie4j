package ru.peacepie.plugin.api;

import java.util.LinkedHashMap;
import java.util.Map;


public class PeacepieMessage extends LinkedHashMap<String, Object> {
	
	private static final long serialVersionUID = 3423968892697389398L;

	public static PeacepieMessage getMsg(
            String mid,
            String command,
            Map<String, Object> body,
            Object recipient,
            Object sender,
            Integer timeout,
            String user
    ) {
		PeacepieMessage res = new PeacepieMessage();
		res.put("mid", mid);
		res.put("command", command);
		res.put("body", body);
		res.put("recipient", recipient);
		res.put("sender", sender);
		res.put("timeout", timeout);
		res.put("user", user);
		return res;
	}
}

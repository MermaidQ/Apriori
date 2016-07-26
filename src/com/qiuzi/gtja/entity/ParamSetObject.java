package com.qiuzi.gtja.entity;

import java.util.HashMap;
import java.util.Map;

public class ParamSetObject {
	
	private Map<String, String> paramSetObject;
	private float support;
	private float confidence;
	
    public ParamSetObject(Map<String, String> paramSetObject, float support, float confidence) {
        this.paramSetObject = paramSetObject;
        this.support = support;
        this.confidence = confidence;
    }
    public Map<String, String> getparamSetObject() {
		return paramSetObject;
	}
	public void setparamSetObject(Map<String, String> paramObject) {
		this.paramSetObject = paramObject;
	}
	public static ParamSetObject getParamSetObject(String paramName, String paramValue, float support, float confidence) {
	    
	    Map<String, String> paramObject = new HashMap<String, String>();
	    paramObject.put(paramName, paramValue);
	    
	    return new ParamSetObject(paramObject, support, confidence);
    }
	public float getSupport() {
        return support;
    }
    public float getConfidence() {
		return confidence;
	}
}

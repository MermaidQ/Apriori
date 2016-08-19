package com.qiuzi.gtja.entity;

import java.util.HashSet;
import java.util.Set;

public class ParamSetObject {
	
	private Set<String> paramSetObject;
	private double support;
	private double confidence;
	
    public ParamSetObject(Set<String> paramSetObject, double support, double confidence) {
        this.paramSetObject = paramSetObject;
        this.support = support;
        this.confidence = confidence;
    }
    public Set<String> getparamSetObject() {
		return paramSetObject;
	}
	public void setparamSetObject(Set<String> paramObject) {
		this.paramSetObject = paramObject;
	}
	public static ParamSetObject getParamSetObject(String paramName, String paramValue, double support, double confidence) {
	    
	    Set<String> paramObject = new HashSet<String>();
	    paramObject.add(paramName+"="+paramValue);
	    
	    return new ParamSetObject(paramObject, support, confidence);
    }
	public double getSupport() {
        return support;
    }
    public double getConfidence() {
		return confidence;
	}
}

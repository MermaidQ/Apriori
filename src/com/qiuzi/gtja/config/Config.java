package com.qiuzi.gtja.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qiuzi.gtja.util.FileUtil;

public class Config {
	
	public static Map<String, int[]> getCategorizeStandardConfig(String configfilePath){		
		
		Map<String, int[]> map = new HashMap<String, int[]>();
		FileUtil fo = new FileUtil();
		List<String> list = fo.readFile(configfilePath);
		for(String s:list){
			
			String[] a = s.split(" ");
			String attributeName = a[0];
			String[] attributeRange = a[1].split(",");
			int[] categorizeStandard = new int[attributeRange.length];
			//����񻯱�׼תΪint
			for(int i = 0; i < attributeRange.length;i++){
				categorizeStandard[i] = Integer.parseInt(attributeRange[i]);
			}
			map.put(attributeName, categorizeStandard);
		}
		return map;
	}
}

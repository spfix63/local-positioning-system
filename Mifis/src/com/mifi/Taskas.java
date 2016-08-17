package com.mifi;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Taskas
{
	int id;
	
	public static String toJson(Taskas map)
	{
		Gson gson = new Gson();
		return gson.toJson(map);
	}
	
	public static Taskas fromJson(String json) throws JsonSyntaxException
	{
		Gson gson = new Gson();
		return gson.fromJson(json, Taskas.class);
	}
}

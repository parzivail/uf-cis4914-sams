package com.nitcoders.model;

import java.util.HashMap;

public class Subject
{
	public enum Gender
	{
		Male("Male"),
		Female("Female");

		private final String name;

		Gender(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}
	}

	private String id;
	private int age;
	private Gender gender;
	private HashMap<String, boolean[]> scores;

	public Subject(String id, int age, Gender gender)
	{
		this.id = id;
		this.age = age;
		this.gender = gender;
		this.scores = new HashMap<>();
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public Gender getGender()
	{
		return gender;
	}

	public void setGender(Gender gender)
	{
		this.gender = gender;
	}

	public int getAge()
	{
		return age;
	}

	public void setAge(int age)
	{
		this.age = age;
	}

	public HashMap<String, boolean[]> getScores()
	{
		return scores;
	}
}

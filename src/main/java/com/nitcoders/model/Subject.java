package com.nitcoders.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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

	private transient List<PlaylistEntry> bakedPlaylist = null;

	private String id;
	private int age;
	private Gender gender;
	private HashMap<String, boolean[]> scores;
	private List<PlaylistEntry> playlist = new ArrayList<>();

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

	public List<PlaylistEntry> getPlaylist()
	{
		return playlist;
	}

	public void invalidatePlaylist(Project project)
	{
		var map = project.getStimuliMap();
		this.playlist.removeIf(playlistEntry -> !map.containsKey(playlistEntry.getStimulusId()));

		bakedPlaylist = null;
	}

	public List<PlaylistEntry> getBakedPlaylist()
	{
		if (bakedPlaylist == null)
			bakedPlaylist = Collections.unmodifiableList(playlist);

		return bakedPlaylist;
	}
}

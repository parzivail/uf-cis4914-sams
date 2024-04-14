package com.nitcoders.util;

public enum AudioChannel
{
	Left("Left channel"),
	Right("Right channel"),
	Both("Both channels");

	private final String name;

	AudioChannel(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
}

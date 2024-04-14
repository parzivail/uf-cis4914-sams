package com.nitcoders.model;

import com.nitcoders.util.AudioChannel;

public class PlaylistEntry
{
	private String stimulusId;
	private AudioChannel channel;

	public PlaylistEntry(Stimulus stimulus, AudioChannel channel)
	{
		this.stimulusId = stimulus.getId();
		this.channel = channel;
	}

	public void setStimulus(Stimulus stimulus)
	{
		this.stimulusId = stimulus.getId();
	}

	public String getStimulusId()
	{
		return stimulusId;
	}

	public Stimulus getStimulus(Project project)
	{
		return project.getStimuliMap().get(stimulusId);
	}

	public AudioChannel getChannel()
	{
		return channel;
	}

	public void setChannel(AudioChannel channel)
	{
		this.channel = channel;
	}
}

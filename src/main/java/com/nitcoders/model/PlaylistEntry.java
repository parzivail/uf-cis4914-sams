package com.nitcoders.model;

import com.nitcoders.util.AudioUtil;

public class PlaylistEntry
{
	private String stimulusId;
	private AudioUtil.Channel channel;

	public PlaylistEntry(Stimulus stimulus, AudioUtil.Channel channel)
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

	public AudioUtil.Channel getChannel()
	{
		return channel;
	}

	public void setChannel(AudioUtil.Channel channel)
	{
		this.channel = channel;
	}
}

package com.nitcoders.model;

import com.nitcoders.util.AudioUtil;

import java.util.*;
import java.util.stream.Collectors;

public class Project
{
	private transient Map<String, Stimulus> stimuliMap = null;
	private transient List<PlaylistEntry> bakedPlaylist = null;
	private transient List<PlaylistEntry> bakedPracticePlaylist = null;

	private List<String> stimulusTypes = new ArrayList<>();
	private List<Stimulus> stimuli = new ArrayList<>();
	private List<Subject> subjects = new ArrayList<>();
	private List<PlaylistEntry> playlist = new ArrayList<>();

	private HashMap<AudioUtil.Channel, String> channelNames = new HashMap<>();

	public Project()
	{
		for (var channel : AudioUtil.Channel.values())
			channelNames.put(channel, channel.getName());
	}

	public HashMap<AudioUtil.Channel, String> getChannelNameMap()
	{
		return channelNames;
	}

	public String getChannelName(AudioUtil.Channel channel)
	{
		return channelNames.get(channel);
	}

	public List<PlaylistEntry> getPlaylist()
	{
		return playlist;
	}

	public void invalidatePlaylist()
	{
		bakedPlaylist = null;
		bakedPracticePlaylist = null;
	}

	public List<Stimulus> getStimuli()
	{
		return stimuli;
	}

	public List<String> getStimulusTypes()
	{
		return stimulusTypes;
	}

	public List<Subject> getSubjects()
	{
		return subjects;
	}

	public void invalidateStimuliMap()
	{
		stimuliMap = null;
	}

	public Map<String, Stimulus> getStimuliMap()
	{
		if (stimuliMap == null)
			stimuliMap = stimuli
					.stream()
					.collect(Collectors.toMap(Stimulus::getId, o -> o));

		return stimuliMap;
	}

	public List<PlaylistEntry> getBakedPlaylist()
	{
		if (bakedPlaylist == null)
			bakedPlaylist = Collections.unmodifiableList(playlist);

		return bakedPlaylist;
	}

	public List<PlaylistEntry> getBakedPracticePlaylist()
	{
		if (bakedPracticePlaylist == null)
		{
			bakedPracticePlaylist = new ArrayList<>();

			for (var stimulus : stimuli)
			{
				if (!stimulus.isPractice())
					continue;

				for (var channel : AudioUtil.Channel.values())
					bakedPracticePlaylist.add(new PlaylistEntry(stimulus, channel));
			}
		}

		return bakedPracticePlaylist;
	}
}

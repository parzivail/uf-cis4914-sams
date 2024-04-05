package com.nitcoders.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Project
{
	private transient Map<String, Stimulus> stimuliMap = null;

	private List<String> stimulusTypes = new ArrayList<>();
	private List<Stimulus> stimuli = new ArrayList<>();
	private List<Subject> subjects = new ArrayList<>();
	private List<PlaylistEntry> playlist = new ArrayList<>();

	public Project()
	{
	}

	public List<PlaylistEntry> getPlaylist()
	{
		return playlist;
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
}

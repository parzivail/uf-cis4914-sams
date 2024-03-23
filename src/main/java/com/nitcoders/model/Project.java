package com.nitcoders.model;

import java.util.ArrayList;
import java.util.List;

public class Project
{
	private List<String> stimulusTypes = new ArrayList<>();
	private List<Stimulus> stimuli = new ArrayList<>();
	private List<Subject> subjects = new ArrayList<>();

	public Project()
	{
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
}

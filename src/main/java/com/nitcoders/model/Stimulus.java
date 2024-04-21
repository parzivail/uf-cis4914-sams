package com.nitcoders.model;

import java.util.UUID;

public class Stimulus
{
	private final String id;

	private String sentence;
	private String stimulusType;
	private String sampleFilename;
	private boolean isPractice;
	private boolean isUnprocessed;

	public Stimulus(String sentence, String stimulusType)
	{
		this.id = UUID.randomUUID().toString();
		this.sentence = sentence;
		this.stimulusType = stimulusType;
	}

	public String getId()
	{
		return id;
	}

	public String getSentence()
	{
		return sentence;
	}

	public String getStimulusType()
	{
		return stimulusType;
	}

	public String getSampleFilename()
	{
		return sampleFilename;
	}

	public void setSentence(String sentence)
	{
		this.sentence = sentence;
	}

	public void setStimulusType(String stimulusType)
	{
		this.stimulusType = stimulusType;
	}

	public void setSampleFilename(String sampleFilename)
	{
		this.sampleFilename = sampleFilename;
	}

	public boolean isUnprocessed()
	{
		return isUnprocessed;
	}

	public void setUnprocessed(boolean unprocessed)
	{
		isUnprocessed = unprocessed;
	}

	public boolean isPractice()
	{
		return isPractice;
	}

	public void setPractice(boolean practice)
	{
		isPractice = practice;
	}
}

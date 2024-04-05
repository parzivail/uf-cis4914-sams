package com.nitcoders.model;

import java.util.UUID;

public class Stimulus
{
	private final String id;

	private String sentence;
	private String stimulusType;
	private String sampleFilename;

	public Stimulus(String sentence, String stimulusType, String sampleFilename)
	{
		this.id = UUID.randomUUID().toString();
		this.sentence = sentence;
		this.stimulusType = stimulusType;
		this.sampleFilename = sampleFilename;
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
}

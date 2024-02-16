package com.nitcoders.model;

public class Stimuli
{
	private String sentence;
	private String stimuliType;
	private String sampleFilename;

	public Stimuli(String sentence, String stimuliType, String sampleFilename)
	{
		this.sentence = sentence;
		this.stimuliType = stimuliType;
		this.sampleFilename = sampleFilename;
	}

	public String getSentence()
	{
		return sentence;
	}

	public String getStimuliType()
	{
		return stimuliType;
	}

	public String getSampleFilename()
	{
		return sampleFilename;
	}

	public void setSentence(String sentence)
	{
		this.sentence = sentence;
	}

	public void setStimuliType(String stimuliType)
	{
		this.stimuliType = stimuliType;
	}

	public void setSampleFilename(String sampleFilename)
	{
		this.sampleFilename = sampleFilename;
	}
}

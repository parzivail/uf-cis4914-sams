package com.nitcoders.model;

public class Stimulus
{
	private String sentence;
	private String stimulusType;
	private String sampleFilename;

	public Stimulus(String sentence, String stimulusType, String sampleFilename)
	{
		this.sentence = sentence;
		this.stimulusType = stimulusType;
		this.sampleFilename = sampleFilename;
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

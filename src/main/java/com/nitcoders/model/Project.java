package com.nitcoders.model;

import com.nitcoders.util.AudioChannel;
import com.nitcoders.util.DialogUtil;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Project
{
	private transient Map<String, Stimulus> stimuliMap = null;
	private transient Map<String, Subject> subjectMap = null;
	private transient List<PlaylistEntry> bakedPracticePlaylist = null;

	private List<String> stimulusTypes = new ArrayList<>();
	private List<Stimulus> stimuli = new ArrayList<>();
	private List<Subject> subjects = new ArrayList<>();

	private HashMap<AudioChannel, String> channelNames = new HashMap<>();

	public Project()
	{
		for (var channel : AudioChannel.values())
			channelNames.put(channel, channel.getName());
	}

	public HashMap<AudioChannel, String> getChannelNameMap()
	{
		return channelNames;
	}

	public String getChannelName(AudioChannel channel)
	{
		return channelNames.get(channel);
	}

	public void invalidatePracticePlaylist()
	{
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

	public void invalidateSubjectMap()
	{
		subjectMap = null;
	}

	public Map<String, Subject> getSubjectMap()
	{
		if (subjectMap == null)
			subjectMap = subjects
					.stream()
					.collect(Collectors.toMap(Subject::getId, o -> o));

		return subjectMap;
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

				if (stimulus.isUnprocessed())
				{
					bakedPracticePlaylist.add(new PlaylistEntry(stimulus, AudioChannel.Both));
					continue;
				}

				for (var channel : AudioChannel.values())
					bakedPracticePlaylist.add(new PlaylistEntry(stimulus, channel));
			}
		}

		return bakedPracticePlaylist;
	}

	public void exportScores(String path)
	{
		var folder = Path.of(path);

		var scores = new ArrayList<SubjectSummaryStatistics>();

		for (var subject : subjects)
		{
			var filename = folder.resolve("%s.csv".formatted(subject.getId()));
			try (var sw = new PrintWriter(filename.toFile()))
			{
				sw.println("Sentence,1st word correct,2nd word correct,...,Nth word correct");

				subject.getScores().keySet().stream().sorted().forEachOrdered(sentence -> {
					sw.print('"');
					sw.print(sentence);
					sw.print('"');

					var parts = subject.getScores().get(sentence);
					for (var part : parts)
					{
						sw.print(",");
						sw.print(part);
					}

					sw.println();
				});

				sw.println();
				sw.println();

				sw.println("Statistic,Value");

				var totalWords = 0;
				var totalSentences = 0;

				var correctSentences = 0;
				var correctWords = 0;

				for (var sentenceScore : subject.getScores().values())
				{
					var isSentenceCorrect = true;
					for (var word : sentenceScore)
					{
						if (word)
							correctWords++;
						else
							isSentenceCorrect = false;

						totalWords++;
					}

					if (isSentenceCorrect)
						correctSentences++;

					totalSentences++;
				}

				var stats = new SubjectSummaryStatistics(
						subject.getId(),
						totalWords,
						totalSentences,
						correctWords / (float)totalWords,
						correctSentences / (float)totalSentences
				);
				scores.add(stats);

				sw.println("Words correct,%s".formatted(stats.wordsCorrectProportion()));
				sw.println("Sentences correct,%s".formatted(stats.sentencesCorrectProportion()));
			}
			catch (Exception e)
			{
				DialogUtil.notify("Unable to save scores", "Unable to save scores! Error while saving %s: %s".formatted(filename.toString(), e.getMessage()), DialogUtil.Icon.ERROR);
				break;
			}
		}

		var filename = folder.resolve("summary.csv");
		try (var sw = new PrintWriter(filename.toFile()))
		{
			sw.println("Subject,Total Words,Words Correct Proportion,Total Sentences,Sentences Correct Proportion");

			scores.forEach(stat -> sw.println("\"%s\",%s,%s,%s,%s".formatted(stat.subjectId(), stat.totalWords(), stat.wordsCorrectProportion(), stat.totalSentences(), stat.sentencesCorrectProportion())));
		}
		catch (Exception e)
		{
			DialogUtil.notify("Unable to save scores", "Unable to save scores! Error while saving %s: %s".formatted(filename.toString(), e.getMessage()), DialogUtil.Icon.ERROR);
		}
	}

	public void invalidateAllPlaylists()
	{
		invalidatePracticePlaylist();
		subjects.forEach(subject -> subject.invalidatePlaylist(this));
	}
}

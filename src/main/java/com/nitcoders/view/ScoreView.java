package com.nitcoders.view;

import com.nitcoders.IconFont;
import com.nitcoders.MainWindow;
import com.nitcoders.model.PlaylistEntry;
import com.nitcoders.model.Project;
import imgui.ImColor;
import imgui.ImGui;
import imgui.flag.ImGuiCol;

import java.util.Arrays;
import java.util.List;

public class ScoreView
{
	private static String currentUserId;
	private static List<PlaylistEntry> previousPlaylist = null;
	private static int currentEntry = 0;

	private static boolean[] currentScore = null;

	public static void draw(Project project)
	{
		var playlist = project.getBakedPlaylist();

		if (playlist.isEmpty())
		{
			ImGui.textDisabled("Playlist is empty. Nothing to score.");
			return;
		}

		var playlistSize = playlist.size();

		if (playlist != previousPlaylist)
			currentEntry = 0;

		previousPlaylist = playlist;

		var entry = playlist.get(currentEntry);
		var stimulus = entry.getStimulus(project);

		var subjectMap = project.getSubjectMap();

		if (!subjectMap.containsKey(currentUserId))
			currentUserId = null;

		var currentSubject = subjectMap.get(currentUserId);

		var subjectPreviewStr = "Select a subject";
		if (currentSubject != null)
		{
			var scoredCount = playlist.stream()
			                          .filter(e -> currentSubject.getScores().containsKey(e.getStimulus(project).getSentence()))
			                          .count();
			var isComplete = scoredCount == playlistSize;

			subjectPreviewStr = "%s%s (%s/%s)".formatted(isComplete ? IconFont.checkmark + " " : "", currentSubject.getId(), scoredCount, playlistSize);
		}

		ImGui.text("Subject:");

		if (ImGui.beginCombo("##scoringSubject", subjectPreviewStr))
		{
			for (var subject : project.getSubjects())
			{
				var scoredCount = playlist.stream()
				                          .filter(e -> subject.getScores().containsKey(e.getStimulus(project).getSentence()))
				                          .count();
				var isComplete = scoredCount == playlistSize;

				if (ImGui.selectable("%s%s (%s/%s)".formatted(isComplete ? IconFont.checkmark + " " : "", subject.getId(), scoredCount, playlistSize)))
					currentUserId = subject.getId();
			}
			ImGui.endCombo();
		}

		if (currentSubject == null)
		{
			ImGui.textDisabled("No subject selected.");
			return;
		}

		ImGui.text("Sentence %s of %s".formatted(currentEntry + 1, playlistSize));

		ImGui.indent();
		ImGui.pushFont(MainWindow.getLargeFont());
		ImGui.textWrapped(stimulus.getSentence());

		ImGui.popFont();
		ImGui.unindent();

		ImGui.text("Score:");

		ImGui.indent();

		var wordParts = stimulus.getSentence().split("\\s+");

		// First attempt to get current score from subject data
		currentScore = currentSubject.getScores().get(stimulus.getSentence());

		// If the subject doesn't have a score for this sentence (or it's invalid), generate a new score array
		if (currentScore == null || currentScore.length != wordParts.length)
			currentScore = new boolean[wordParts.length];

		for (var i = 0; i < wordParts.length; i++)
		{
			if (i > 0)
				ImGui.sameLine();

			var hue = currentScore[i] ? 0.33f : 0f;

			ImGui.pushStyleColor(ImGuiCol.Button, ImColor.hsl(hue, 0.6f, 0.5f));
			ImGui.pushStyleColor(ImGuiCol.ButtonHovered, ImColor.hsl(hue, 0.7f, 0.6f));
			ImGui.pushStyleColor(ImGuiCol.ButtonActive, ImColor.hsl(hue, 0.8f, 0.7f));

			if (ImGui.button(wordParts[i]))
			{
				currentScore[i] = !currentScore[i];
				currentSubject.getScores().put(stimulus.getSentence(), currentScore);
			}

			ImGui.popStyleColor(3);
		}

		ImGui.spacing();
		ImGui.spacing();

		if (ImGui.button("Mark all correct"))
		{
			Arrays.fill(currentScore, true);
			currentSubject.getScores().put(stimulus.getSentence(), currentScore);
		}

		ImGui.sameLine();

		if (ImGui.button("Mark all incorrect"))
		{
			Arrays.fill(currentScore, false);
			currentSubject.getScores().put(stimulus.getSentence(), currentScore);
		}

		ImGui.sameLine();

		ImGui.beginDisabled(!currentSubject.getScores().containsKey(stimulus.getSentence()));
		if (ImGui.button("Mark unscored"))
		{
			currentSubject.getScores().remove(stimulus.getSentence());
		}
		ImGui.endDisabled();

		ImGui.unindent();

		ImGui.text("Actions:");

		ImGui.indent();
		ImGui.pushFont(MainWindow.getLargeFont());

		ImGui.beginDisabled(currentEntry <= 0);
		if (ImGui.button(IconFont.rew))
		{
			currentEntry--;
			currentScore = null;
		}
		ImGui.endDisabled();

		ImGui.sameLine();

		ImGui.sameLine();

		ImGui.beginDisabled(currentEntry >= playlistSize - 1);
		if (ImGui.button(IconFont.ff))
		{
			currentEntry++;
			currentScore = null;
		}
		ImGui.endDisabled();
		ImGui.popFont();

		ImGui.spacing();

		ImGui.unindent();

		ImGui.text("Queue:");

		ImGui.indent();
		if (ImGui.beginListBox("##queue", 0, ImGui.getContentRegionAvailY()))
		{
			for (var i = 0; i < playlistSize; i++)
			{
				var queueSentence = playlist.get(i).getStimulus(project).getSentence();
				if (ImGui.selectable("%s(%s) %s".formatted(
						                     currentSubject.getScores().containsKey(queueSentence) ? IconFont.checkmark : "",
						                     i + 1,
						                     queueSentence
				                     ),
				                     i == currentEntry
				))
					currentEntry = i;
			}
			ImGui.endListBox();
		}
		ImGui.unindent();
	}
}

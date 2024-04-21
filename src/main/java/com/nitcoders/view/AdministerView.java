package com.nitcoders.view;

import com.nitcoders.IconFont;
import com.nitcoders.MainWindow;
import com.nitcoders.ProjectManager;
import com.nitcoders.model.PlaylistEntry;
import com.nitcoders.model.Project;
import com.nitcoders.util.AudioChannel;
import com.nitcoders.util.AudioUtil;
import imgui.ImGui;
import imgui.type.ImBoolean;

import java.nio.file.Path;
import java.util.List;

public class AdministerView
{
	private static String currentUserId;
	private static List<PlaylistEntry> previousPlaylist = null;
	private static int currentEntry = 0;

	private static final ImBoolean autoAdvance = new ImBoolean(true);

	public static void draw(ProjectManager projectManager, Project project, List<PlaylistEntry> playlist)
	{
		if (playlist == null)
		{
			// Subject-based playlist

			var subjectMap = project.getSubjectMap();

			if (!subjectMap.containsKey(currentUserId))
				currentUserId = null;

			var currentSubject = subjectMap.get(currentUserId);

			var subjectPreviewStr = "Select a subject";
			if (currentSubject != null)
				subjectPreviewStr = currentSubject.getId();

			ImGui.text("Subject:");

			if (ImGui.beginCombo("##scoringSubject", subjectPreviewStr))
			{
				for (var subject : project.getSubjects())
				{
					if (ImGui.selectable(subject.getId()))
						currentUserId = subject.getId();
				}
				ImGui.endCombo();
			}

			if (currentSubject == null)
			{
				ImGui.textDisabled("No subject selected.");
				return;
			}

			playlist = currentSubject.getBakedPlaylist();
		}

		if (playlist.isEmpty())
		{
			ImGui.textDisabled("Playlist is empty. Nothing to play.");
			return;
		}

		var playlistSize = playlist.size();

		if (playlist != previousPlaylist)
			currentEntry = 0;

		previousPlaylist = playlist;

		var entry = playlist.get(currentEntry);
		var stimulus = entry.getStimulus(project);

		ImGui.text("Sentence %s of %s".formatted(currentEntry + 1, playlistSize));

		ImGui.indent();
		ImGui.pushFont(MainWindow.getLargeFont());
		ImGui.textWrapped(stimulus.getSentence());

		var channels = entry.getChannel();

		if (stimulus.isUnprocessed())
		{
			ImGui.textDisabled("%s L+R (Unprocessed)".formatted(IconFont.mute_ipo_on));
		}
		else
		{
			ImGui.textDisabled("%s %s   %s %s".formatted(
					channels != AudioChannel.Right ? IconFont.mute_ipo_on : IconFont.mute_ipo_off,
					project.getChannelName(AudioChannel.Left),
					channels != AudioChannel.Left ? IconFont.mute_ipo_on : IconFont.mute_ipo_off,
					project.getChannelName(AudioChannel.Right)
			));
		}

		ImGui.popFont();
		ImGui.unindent();

		ImGui.text("Actions:");

		ImGui.indent();
		ImGui.pushFont(MainWindow.getLargeFont());

		ImGui.beginDisabled(currentEntry <= 0);
		if (ImGui.button(IconFont.rew))
		{
			currentEntry--;
		}
		ImGui.endDisabled();

		ImGui.sameLine();

		final var soundFilename = stimulus.getSampleFilename();

		ImGui.beginDisabled(soundFilename == null);
		if (ImGui.button("%s##play".formatted(IconFont.play)))
		{
			AudioUtil.tryPlay(projectManager.pathProjectRelativeToAbsolute(Path.of(stimulus.getSampleFilename())), entry.getChannel());

			// TODO: onDone callback?
			if (autoAdvance.get() && currentEntry < playlistSize - 1)
				currentEntry++;
		}
		ImGui.endDisabled();

		ImGui.sameLine();

		ImGui.beginDisabled(currentEntry >= playlistSize - 1);
		if (ImGui.button(IconFont.ff))
		{
			currentEntry++;
		}
		ImGui.endDisabled();
		ImGui.popFont();

		ImGui.spacing();

		ImGui.checkbox("Advance to next stimulus when audio ends", autoAdvance);

		ImGui.unindent();

		ImGui.text("Queue:");

		ImGui.indent();
		if (ImGui.beginListBox("##queue", 0, ImGui.getContentRegionAvailY()))
		{
			for (var i = 0; i < playlistSize; i++)
			{
				var queueEntry = playlist.get(i);
				var stim = queueEntry.getStimulus(project);
				var channelName = stim.isUnprocessed() ? "L+R Unprocessed" : project.getChannelName(queueEntry.getChannel());
				if (ImGui.selectable("(%s) %s (%s)".formatted(i + 1, stim.getSentence(), channelName), i == currentEntry))
					currentEntry = i;
			}
			ImGui.endListBox();
		}
		ImGui.unindent();
	}
}

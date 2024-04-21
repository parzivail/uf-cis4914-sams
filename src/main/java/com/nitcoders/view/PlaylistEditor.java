package com.nitcoders.view;

import com.nitcoders.IconFont;
import com.nitcoders.MainWindow;
import com.nitcoders.ProjectManager;
import com.nitcoders.model.PlaylistEntry;
import com.nitcoders.model.Project;
import com.nitcoders.model.Subject;
import com.nitcoders.util.AudioChannel;
import com.nitcoders.util.AudioUtil;
import com.nitcoders.util.DialogUtil;
import com.nitcoders.util.ListUtil;
import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PlaylistEditor
{
	private static final class RandomizerRule
	{
		private boolean isEverythingElse;
		private int count;
		private final String stimulusType;

		private RandomizerRule(String stimulusType)
		{
			this.stimulusType = stimulusType;
		}
	}

	private record InsertData(PlaylistEntry entry, int index)
	{
	}

	private static String currentUserId;

	private static AudioChannel randomizerFirstChannel = AudioChannel.Left;
	private static final ImBoolean randomizerRandomGrouping = new ImBoolean(true);
	private static final ImInt selectedRandomizerStimulusType = new ImInt();
	private static final List<RandomizerRule> randomizerRules = new ArrayList<>();

	static
	{
		var rule = new RandomizerRule("everything else");
		rule.isEverythingElse = true;
		randomizerRules.add(rule);
	}

	public static void draw(ProjectManager projectManager, Project project)
	{
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

		var playlist = currentSubject.getPlaylist();

		var innerSize = ImGui.getContentRegionAvail();
		if (ImGui.beginTable("entryTable", 2, ImGuiTableFlags.Resizable | ImGuiTableFlags.NoHostExtendY, innerSize.x, innerSize.y))
		{
			ImGui.tableSetupColumn("label", ImGuiTableColumnFlags.WidthFixed, 600);
			ImGui.tableSetupColumn("field", ImGuiTableColumnFlags.WidthStretch);

			ImGui.tableNextColumn();

			var frameSize = ImGui.getFrameHeight();

			var channels = AudioChannel.values();

			var stimuli = project.getStimuli();
			var stimulusTypes = project.getStimulusTypes().toArray(String[]::new);

			var includedIds = playlist.stream()
			                          .map(PlaylistEntry::getStimulusId)
			                          .collect(Collectors.toSet());

			ImGui.text("Available Stimuli");

			if (ImGui.beginListBox("##stimuliList", -1, -1))
			{
				if (includedIds.size() == stimuli.size())
					ImGui.textDisabled("All stimuli are included in this playlist.");
				else if (ImGui.beginTable("stimuliList", 2, ImGuiTableFlags.BordersH | ImGuiTableFlags.PadOuterX))
				{
					ImGui.tableSetupColumn("body[]", ImGuiTableColumnFlags.WidthStretch);
					ImGui.tableSetupColumn("actions[]", ImGuiTableColumnFlags.WidthFixed, -1);

					ListUtil.iterate(stimuli, (iterator, i, stimulus) ->
					{
						if (stimulus.isPractice() || includedIds.contains(stimulus.getId()))
							return;

						ImGui.tableNextColumn();
						ImGui.spacing();

						ImGui.textWrapped(stimulus.getSentence());

						ImGui.indent();
						ImGui.textDisabled(stimulus.getStimulusType());
						ImGui.unindent();

						ImGui.tableNextColumn();

						if (ImGui.button("%s##add%s".formatted(IconFont.add, i), frameSize, frameSize))
						{
							playlist.add(0, new PlaylistEntry(stimulus, AudioChannel.Both));

							currentSubject.invalidatePlaylist(project);
							project.invalidatePracticePlaylist();
						}
					});

					ImGui.endTable();
				}
				ImGui.endListBox();
			}

			ImGui.tableNextColumn();

			ImGui.pushFont(MainWindow.getSmallFont());
			if (ImGui.beginTable("##quantityBreakdown", 1 + channels.length, ImGuiTableFlags.BordersH | ImGuiTableFlags.PadOuterX))
			{
				ImGui.tableSetupColumn("stimuliType[]", ImGuiTableColumnFlags.WidthStretch);

				for (var channel : channels)
					ImGui.tableSetupColumn("channels[]" + channel.getName(), ImGuiTableColumnFlags.WidthFixed, -1);

				ImGui.tableNextColumn();

				for (var channel : channels)
				{
					ImGui.tableNextColumn();
					ImGui.text(project.getChannelName(channel));
				}

				for (var type : stimulusTypes)
				{
					ImGui.tableNextColumn();
					ImGui.text(type);

					for (var channel : AudioChannel.values())
					{
						ImGui.tableNextColumn();
						ImGui.text(String.valueOf(
								playlist.stream()
								        .filter(e -> e.getStimulus(project).getStimulusType().equals(type) && e.getChannel() == channel)
								        .count()
						));
					}
				}

				ImGui.tableNextColumn();
				ImGui.textDisabled("Total (%s)".formatted(playlist.size()));

				for (var channel : AudioChannel.values())
				{
					ImGui.tableNextColumn();
					ImGui.text(String.valueOf(
							playlist.stream()
							        .filter(e -> e.getChannel() == channel)
							        .count()
					));
				}

				ImGui.endTable();
			}
			ImGui.popFont();

			if (ImGui.beginTabBar("playlistTabs"))
			{
				if (ImGui.beginTabItem("Playlist"))
				{
					if (ImGui.button("Clear Playlist"))
					{
						var button = DialogUtil.notifyChoice("Clear playlist?", "Are you sure? This action is irreversible.", DialogUtil.Icon.WARNING, DialogUtil.ButtonGroup.YESNO, false);
						if (button == DialogUtil.Button.YES)
						{
							playlist.clear();
						}
					}

					if (ImGui.beginListBox("##playlistList", -1, -1))
					{
						if (ImGui.beginTable("playlistList", 2, ImGuiTableFlags.BordersH | ImGuiTableFlags.PadOuterX))
						{
							ImGui.tableSetupColumn("body[]", ImGuiTableColumnFlags.WidthStretch);
							ImGui.tableSetupColumn("actions[]", ImGuiTableColumnFlags.WidthFixed, -1);

							Mutable<InsertData> insertData = new MutableObject<>();

							if (playlist.isEmpty())
							{
								ImGui.tableNextColumn();
								ImGui.textDisabled("Playlist is empty.");
								ImGui.tableNextColumn();
							}

							ListUtil.iterate(playlist, (iterator, i, entry) ->
							{
								ImGui.tableNextColumn();
								ImGui.spacing();

								var stimulus = entry.getStimulus(project);
								ImGui.textWrapped(stimulus.getSentence());

								ImGui.indent();
								ImGui.textDisabled(stimulus.getStimulusType());
								ImGui.unindent();

								ImGui.alignTextToFramePadding();
								ImGui.textDisabled("Audio channel: ");

								ImGui.sameLine();

								for (var channel : channels)
								{
									if (ImGui.radioButton("%s##%s".formatted(project.getChannelName(channel), i), entry.getChannel() == channel))
										entry.setChannel(channel);

									ImGui.sameLine();
								}

								ImGui.tableNextColumn();

								final var soundFilename = stimulus.getSampleFilename();

								ImGui.beginDisabled(soundFilename == null);
								if (ImGui.button("%s##preview%s".formatted(IconFont.play_sound, i), frameSize, frameSize))
								{
									AudioUtil.tryPlay(projectManager.pathProjectRelativeToAbsolute(Path.of(stimulus.getSampleFilename())), entry.getChannel());
								}
								ImGui.endDisabled();

								ImGui.sameLine();

								ImGui.beginDisabled(i == 0);
								if (ImGui.button("%s##moveUp%s".formatted(IconFont.tria_up, i), frameSize, frameSize))
								{
									insertData.setValue(new InsertData(entry, i - 1));
									iterator.remove();

									currentSubject.invalidatePlaylist(project);
									project.invalidatePracticePlaylist();
								}
								ImGui.endDisabled();

								ImGui.sameLine();

								ImGui.beginDisabled(!iterator.hasNext());
								if (ImGui.button("%s##moveDown%s".formatted(IconFont.tria_down, i), frameSize, frameSize))
								{
									insertData.setValue(new InsertData(entry, i + 1));
									iterator.remove();

									currentSubject.invalidatePlaylist(project);
									project.invalidatePracticePlaylist();
								}
								ImGui.endDisabled();

								ImGui.sameLine();

								if (ImGui.button("%s##remove%s".formatted(IconFont.trash, i), frameSize, frameSize))
								{
									iterator.remove();

									currentSubject.invalidatePlaylist(project);
									project.invalidatePracticePlaylist();
								}
							});

							if (insertData.getValue() != null)
							{
								var value = insertData.getValue();
								playlist.add(value.index, value.entry);
							}

							ImGui.endTable();
						}
						ImGui.endListBox();
					}

					ImGui.endTabItem();
				}

				if (ImGui.beginTabItem("Randomizer"))
				{
					if (ImGui.collapsingHeader("Stimuli Selection", ImGuiTreeNodeFlags.DefaultOpen))
					{
						ImGui.combo("##ruleTypes", selectedRandomizerStimulusType, stimulusTypes);
						ImGui.sameLine();
						if (ImGui.button("Add Rule"))
							randomizerRules.add(0, new RandomizerRule(stimulusTypes[selectedRandomizerStimulusType.get()]));

						ListUtil.iterate(randomizerRules, (iterator, i, rule) -> {
							ImGui.bullet();

							if (!rule.isEverythingElse)
							{
								if (ImGui.button(IconFont.trash))
									iterator.remove();

								ImGui.sameLine();
							}

							ImGui.text("Select");
							ImGui.sameLine();

							ImGui.setNextItemWidth(200);

							var count = new ImInt(rule.count);
							if (ImGui.inputInt("##countOf" + rule.stimulusType, count))
								rule.count = count.get();

							ImGui.sameLine();

							ImGui.text("of %s".formatted(rule.stimulusType));
						});
					}

					if (ImGui.collapsingHeader("Blocking", ImGuiTreeNodeFlags.DefaultOpen))
					{
						ImGui.checkbox("Fully random", randomizerRandomGrouping);

						ImGui.beginDisabled(randomizerRandomGrouping.get());

						ImGui.text("First block:");

						if (ImGui.radioButton(project.getChannelName(AudioChannel.Left), randomizerFirstChannel == AudioChannel.Left))
							randomizerFirstChannel = AudioChannel.Left;

						if (ImGui.radioButton(project.getChannelName(AudioChannel.Both), randomizerFirstChannel == AudioChannel.Both))
							randomizerFirstChannel = AudioChannel.Both;

						ImGui.endDisabled();
					}

					ImGui.separator();

					if (ImGui.button("Apply"))
					{
						var button = DialogUtil.notifyChoice("Overwrite playlist?", "Are you sure? Your existing playlist for this subject will be overwritten. This action is irreversible.", DialogUtil.Icon.WARNING, DialogUtil.ButtonGroup.YESNO, false);
						if (button == DialogUtil.Button.YES)
						{
							createRandomPlaylist(project, currentSubject, playlist);
						}
					}

					ImGui.sameLine();

					if (ImGui.button("Apply to ALL subjects"))
					{
						var button = DialogUtil.notifyChoice("Overwrite playlists?", "Are you sure? Your existing playlist for ALL SUBJECTS will be overwritten. This action is irreversible.", DialogUtil.Icon.WARNING, DialogUtil.ButtonGroup.YESNO, false);
						if (button == DialogUtil.Button.YES)
						{
							for (var subject : project.getSubjects())
							{
								createRandomPlaylist(project, subject, subject.getPlaylist());
							}
						}
					}

					ImGui.endTabItem();
				}

				ImGui.endTabBar();
			}

			ImGui.endTable();
		}
	}

	private static void createRandomPlaylist(Project project, Subject subject, List<PlaylistEntry> playlist)
	{
		playlist.clear();

		var rand = new Random();

		var everythingElse = new HashSet<>(project.getStimulusTypes());
		int everythingElseCount = 0;

		for (var rule : randomizerRules)
		{
			if (rule.isEverythingElse)
			{
				everythingElseCount = rule.count;
				continue;
			}

			everythingElse.remove(rule.stimulusType);

			var result = project.getStimuli().stream()
			                    // From all the stimuli of this type...
			                    .filter(stimulus -> stimulus.getStimulusType().equals(rule.stimulusType) && !stimulus.isPractice() && !stimulus.isUnprocessed())
			                    // ...randomly shuffle them...
			                    .sorted((left, right) -> rand.nextInt())
			                    // ...and take the requested quantity
			                    .limit(rule.count)
			                    .toList();

			var pivot = result.size() / 2;
			for (var i = 0; i < result.size(); i++)
			{
				if (i >= pivot)
					playlist.add(new PlaylistEntry(result.get(i), AudioChannel.Left));
				else
					playlist.add(new PlaylistEntry(result.get(i), AudioChannel.Both));
			}
		}

		// everything else
		var result = project.getStimuli().stream()
		                    // From all the stimuli of this type...
		                    .filter(stimulus -> everythingElse.contains(stimulus.getStimulusType()))
		                    // ...randomly shuffle them...
		                    .sorted((left, right) -> rand.nextInt())
		                    // ...and take the requested quantity
		                    .limit(everythingElseCount)
		                    .toList();

		var pivot = result.size() / 2;
		for (var i = 0; i < result.size(); i++)
		{
			if (i >= pivot)
				playlist.add(new PlaylistEntry(result.get(i), AudioChannel.Left));
			else
				playlist.add(new PlaylistEntry(result.get(i), AudioChannel.Both));
		}

		// Shuffle the resulting playlist.
		playlist.sort((left, right) -> rand.nextInt());

		if (!randomizerRandomGrouping.get())
		{
			// Block the groups by the first channel
			var tempPlaylist = playlist.stream().toList();
			playlist.clear();

			for (var entry : tempPlaylist)
			{
				if (entry.getChannel() == randomizerFirstChannel)
					playlist.add(0, entry);
				else
					playlist.add(entry);
			}
		}

		project.invalidatePracticePlaylist();
		subject.invalidatePlaylist(project);
	}
}

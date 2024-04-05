package com.nitcoders.view;

import com.nitcoders.IconFont;
import com.nitcoders.MainWindow;
import com.nitcoders.model.PlaylistEntry;
import com.nitcoders.model.Project;
import com.nitcoders.util.AudioUtil;
import com.nitcoders.util.ListUtil;
import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.stream.Collectors;

public class PlaylistEditor
{
	private record InsertData(PlaylistEntry entry, int index)
	{
	}

	public static void draw(Project project)
	{
		var innerSize = ImGui.getContentRegionAvail();
		if (ImGui.beginTable("entryTable", 2, ImGuiTableFlags.Resizable | ImGuiTableFlags.NoHostExtendY, innerSize.x, innerSize.y))
		{
			ImGui.tableSetupColumn("label", ImGuiTableColumnFlags.WidthFixed, 600);
			ImGui.tableSetupColumn("field", ImGuiTableColumnFlags.WidthStretch);

			ImGui.tableNextColumn();

			var frameSize = ImGui.getFrameHeight();

			var playlist = project.getPlaylist();

			var channels = AudioUtil.Channel.values();

			var stimuli = project.getStimuli();
			var stimulusTypes = project.getStimulusTypes();
			var stimuliMap = project.getStimuliMap();

			var includedIds = playlist.stream()
			                          .map(PlaylistEntry::getStimulusId)
			                          .collect(Collectors.toSet());

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
						if (includedIds.contains(stimulus.getId()))
							return;

						ImGui.tableNextColumn();
						ImGui.spacing();

						ImGui.textWrapped(stimulus.getSentence());

						ImGui.tableNextColumn();

						if (ImGui.button("%s##add%s".formatted(IconFont.tria_right, i), frameSize, frameSize))
						{
							playlist.add(0, new PlaylistEntry(stimulus, AudioUtil.Channel.Both));
						}
					});

					ImGui.endTable();
				}
				ImGui.endListBox();
			}

			ImGui.tableNextColumn();

			if (!playlist.isEmpty())
			{
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
						ImGui.text(channel.getAbbreviation());
					}

					for (var type : stimulusTypes)
					{
						ImGui.tableNextColumn();
						ImGui.text(type);

						for (var channel : AudioUtil.Channel.values())
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

					for (var channel : AudioUtil.Channel.values())
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

				if (ImGui.beginListBox("##playlistList", -1, -1))
				{
					if (ImGui.beginTable("playlistList", 2, ImGuiTableFlags.BordersH | ImGuiTableFlags.PadOuterX))
					{
						ImGui.tableSetupColumn("body[]", ImGuiTableColumnFlags.WidthStretch);
						ImGui.tableSetupColumn("actions[]", ImGuiTableColumnFlags.WidthFixed, -1);

						Mutable<InsertData> insertData = new MutableObject<>();

						ListUtil.iterate(playlist, (iterator, i, entry) ->
						{
							ImGui.tableNextColumn();
							ImGui.spacing();

							var stimulus = entry.getStimulus(project);
							ImGui.textWrapped(stimulus.getSentence());

							ImGui.alignTextToFramePadding();
							ImGui.textDisabled("Audio channel: ");

							ImGui.sameLine();

							for (var channel : channels)
							{
								if (ImGui.radioButton("%s##%s".formatted(channel.getAbbreviation(), i), entry.getChannel() == channel))
									entry.setChannel(channel);

								ImGui.sameLine();
							}

							ImGui.tableNextColumn();

							ImGui.beginDisabled(i == 0);
							if (ImGui.button("%s##moveUp%s".formatted(IconFont.tria_up, i), frameSize, frameSize))
							{
								insertData.setValue(new InsertData(entry, i - 1));
								iterator.remove();
							}
							ImGui.endDisabled();

							ImGui.sameLine();

							ImGui.beginDisabled(!iterator.hasNext());
							if (ImGui.button("%s##moveDown%s".formatted(IconFont.tria_down, i), frameSize, frameSize))
							{
								insertData.setValue(new InsertData(entry, i + 1));
								iterator.remove();
							}
							ImGui.endDisabled();

							ImGui.sameLine();

							if (ImGui.button("%s##remove%s".formatted(IconFont.trash, i), frameSize, frameSize))
							{
								iterator.remove();
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
			}
			else
			{
				ImGui.textDisabled("Playlist is empty.");
			}

			ImGui.endTable();
		}
	}
}

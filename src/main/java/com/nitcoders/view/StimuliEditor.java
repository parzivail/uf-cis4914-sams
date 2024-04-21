package com.nitcoders.view;

import com.nitcoders.IconFont;
import com.nitcoders.MainWindow;
import com.nitcoders.ProjectManager;
import com.nitcoders.model.Project;
import com.nitcoders.model.Stimulus;
import com.nitcoders.util.*;
import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;

import java.nio.file.Path;

public class StimuliEditor
{
	private static final ImInt selectedStimulusType = new ImInt();

	private static Stimulus currentlyEditingStimulus = null;

	public static void draw(ProjectManager projectManager, Project project)
	{
		var innerSize = ImGui.getContentRegionAvail();
		if (ImGui.beginTable("stimuliTable", 2, ImGuiTableFlags.Resizable | ImGuiTableFlags.NoHostExtendY, innerSize.x, innerSize.y))
		{
			ImGui.tableSetupColumn("label", ImGuiTableColumnFlags.WidthFixed, 600);
			ImGui.tableSetupColumn("field", ImGuiTableColumnFlags.WidthStretch);

			ImGui.tableNextColumn();

			var frameSize = ImGui.getFrameHeight();

			var stimuli = project.getStimuli();
			var stimulusTypes = project.getStimulusTypes().toArray(String[]::new);

			if (stimulusTypes.length > 0)
			{
				ImGui.combo("##stimulusTypes", selectedStimulusType, stimulusTypes);
				ImGui.sameLine();
				if (ImGui.button("New Stimulus", -1, frameSize))
				{
					var stimuliType = stimulusTypes[selectedStimulusType.get()];
					stimuli.add(0, currentlyEditingStimulus = new Stimulus("Sentence", stimuliType));
					project.invalidateStimuliMap();
				}

				ImGui.pushFont(MainWindow.getSmallFont());
				if (ImGui.beginTable("##quantityBreakdown", 2, ImGuiTableFlags.BordersH | ImGuiTableFlags.PadOuterX))
				{
					ImGui.tableSetupColumn("stimuliType[]", ImGuiTableColumnFlags.WidthStretch);
					ImGui.tableSetupColumn("quantity[]", ImGuiTableColumnFlags.WidthFixed, -1);

					for (var type : stimulusTypes)
					{
						ImGui.tableNextColumn();
						ImGui.text(type);

						ImGui.tableNextColumn();
						ImGui.text(String.valueOf(stimuli.stream().filter(s -> s.getStimulusType().equals(type)).count()));
					}

					ImGui.tableNextColumn();
					ImGui.textDisabled("Total");

					ImGui.tableNextColumn();
					ImGui.textDisabled(String.valueOf(stimuli.size()));

					ImGui.endTable();
				}
				ImGui.popFont();
			}
			else
			{
				ImGui.textDisabled("No stimuli types defined.");
			}

			if (ImGui.beginListBox("##stimuliList", -1, -1))
			{
				if (stimuli.isEmpty())
					ImGui.textDisabled("No stimuli yet, add one above.");
				else if (ImGui.beginTable("stimuliList", 2, ImGuiTableFlags.BordersH | ImGuiTableFlags.PadOuterX))
				{
					ImGui.tableSetupColumn("body[]", ImGuiTableColumnFlags.WidthStretch);
					ImGui.tableSetupColumn("actions[]", ImGuiTableColumnFlags.WidthFixed, -1);

					ListUtil.iterate(stimuli, (iterator, i, stimulus) ->
					{
						ImGui.tableNextColumn();
						ImGui.spacing();

						ImGui.textWrapped(stimulus.getSentence());

						ImGui.pushFont(MainWindow.getSmallFont());
						ImGui.indent();
						ImGui.text(stimulus.getStimulusType());

						if (stimulus.isPractice())
						{
							ImGui.sameLine();
							ImGui.textDisabled("(Practice only)");
						}

						if (stimulus.isUnprocessed())
						{
							ImGui.sameLine();
							ImGui.textDisabled("(Unprocessed)");
						}

						if (stimulus.getSampleFilename() == null)
							ImGui.textDisabled("No audio file selected");
						else
							ImGui.text(stimulus.getSampleFilename());

						ImGui.unindent();
						ImGui.popFont();

						ImGui.spacing();

						ImGui.tableNextColumn();
						ImGui.newLine();

						final var soundFilename = stimulus.getSampleFilename();

						ImGui.beginDisabled(soundFilename == null);
						if (ImGui.button("%s##preview%s".formatted(IconFont.play_sound, i), frameSize, frameSize))
						{
							AudioUtil.tryPlay(projectManager.pathProjectRelativeToAbsolute(Path.of(stimulus.getSampleFilename())), AudioChannel.Both);
						}
						ImGui.endDisabled();

						ImGui.sameLine();

						if (ImGui.button("%s##edit%s".formatted(IconFont.greasepencil, i), frameSize, frameSize))
							currentlyEditingStimulus = stimulus;

						ImGui.sameLine();

						if (ImGui.button("%s##delete%s".formatted(IconFont.trash, i), frameSize, frameSize))
						{
							var choice = DialogUtil.notifyChoice(
									"Delete stimulus",
									"Are you sure you want to delete this stimulus?",
									DialogUtil.Icon.WARNING,
									DialogUtil.ButtonGroup.YESNO,
									false);

							if (choice == DialogUtil.Button.YES)
							{
								iterator.remove();
								project.invalidateStimuliMap();
							}
						}
					});

					ImGui.endTable();
				}
				ImGui.endListBox();
			}

			ImGui.tableNextColumn();

			renderEditor(projectManager, project, stimulusTypes);

			ImGui.endTable();
		}
	}

	private static void renderEditor(ProjectManager projectManager, Project project, String[] stimulusTypes)
	{
		if (currentlyEditingStimulus == null)
			return;

		if (ImGui.beginTable("stimulusEditor", 2))
		{
			ImGui.tableSetupColumn("label", ImGuiTableColumnFlags.WidthFixed, 150);
			ImGui.tableSetupColumn("field", ImGuiTableColumnFlags.WidthStretch);

			var typeInt = new ImInt(project.getStimulusTypes().indexOf(currentlyEditingStimulus.getStimulusType()));

			ImGui.tableNextColumn();
			ImGui.text("Stimulus Type");
			ImGui.tableNextColumn();
			if (ImGui.combo("##editorStimulusType", typeInt, stimulusTypes))
				currentlyEditingStimulus.setStimulusType(stimulusTypes[typeInt.get()]);

			ImGui.tableNextColumn();
			ImGui.text("Sentence");
			ImGui.tableNextColumn();

			var sentenceStr = new ImString(currentlyEditingStimulus.getSentence(), 512);
			if (ImGui.inputText("##stimulusSentence", sentenceStr))
				currentlyEditingStimulus.setSentence(sentenceStr.get());

			ImGui.tableNextColumn();

			ImGui.text("Practice Only");

			ImGui.tableNextColumn();
			var isPractice = new ImBoolean(currentlyEditingStimulus.isPractice());
			if (ImGui.checkbox("##practiceOnly", isPractice))
			{
				currentlyEditingStimulus.setPractice(isPractice.get());
				project.invalidatePlaylist();
			}

			ImGui.beginDisabled(!currentlyEditingStimulus.isPractice());
			ImGui.tableNextColumn();

			ImGui.text("Unprocessed Audio");

			ImGui.tableNextColumn();
			var isUnprocessed = new ImBoolean(currentlyEditingStimulus.isUnprocessed());
			if (ImGui.checkbox("##unprocessed", isUnprocessed))
			{
				currentlyEditingStimulus.setUnprocessed(isUnprocessed.get());
				project.invalidatePlaylist();
			}
			ImGui.endDisabled();

			ImGui.tableNextColumn();
			ImGui.text("Audio Sample");
			ImGui.tableNextColumn();

			ImGuiHelper.filePicker(
					"Choose File##stimulusSoundPicker",
					currentlyEditingStimulus::getSampleFilename,
					sampleFilename -> currentlyEditingStimulus.setSampleFilename(projectManager.pathAbsoluteToProjectRelative(Path.of(sampleFilename)).toString()),
					"Open Sound",
					"WAV files (*.wav)",
					"*.wav"
			);

			ImGui.endTable();
		}
	}
}

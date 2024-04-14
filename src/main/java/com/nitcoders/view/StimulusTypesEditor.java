package com.nitcoders.view;

import com.nitcoders.IconFont;
import com.nitcoders.model.Project;
import com.nitcoders.util.DialogUtil;
import com.nitcoders.util.ListUtil;
import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImString;

public class StimulusTypesEditor
{
	private static final ImString stimulusValue = new ImString();

	public static void draw(Project project)
	{
		var innerSize = ImGui.getContentRegionAvail();
		if (ImGui.beginTable("stimulusTypeTable", 2, ImGuiTableFlags.Resizable | ImGuiTableFlags.NoHostExtendY, innerSize.x, innerSize.y))
		{
			ImGui.tableSetupColumn("label", ImGuiTableColumnFlags.WidthFixed, 400);
			ImGui.tableSetupColumn("field", ImGuiTableColumnFlags.WidthStretch);

			ImGui.tableNextColumn();

			var frameSize = ImGui.getFrameHeight();

			var stimuliTypes = project.getStimulusTypes();

			ImGui.inputTextWithHint("##stimulusName", "Stimulus type name", stimulusValue);
			ImGui.sameLine();
			if (ImGui.button("Create", -1, frameSize))
				stimuliTypes.add(stimulusValue.get());

			if (ImGui.beginListBox("##stimulusTypesList", -1, -1))
			{
				if (stimuliTypes.isEmpty())
					ImGui.textDisabled("No stimulus types yet, add one above.");
				else if (ImGui.beginTable("stimulusTypesList", 2, ImGuiTableFlags.BordersH | ImGuiTableFlags.PadOuterX))
				{
					ImGui.tableSetupColumn("body[]", ImGuiTableColumnFlags.WidthStretch);
					ImGui.tableSetupColumn("actions[]", ImGuiTableColumnFlags.WidthFixed, -1);

					ListUtil.iterate(stimuliTypes, (iterator, i, stimuliType) ->
					{
						ImGui.tableNextColumn();
						ImGui.spacing();

						ImGui.text(stimuliType);

						ImGui.spacing();

						ImGui.tableNextColumn();

						if (ImGui.button("%s##delete%s".formatted(IconFont.trash, i), frameSize, frameSize))
						{
							var choice = DialogUtil.notifyChoice(
									"Delete stimuli type",
									"Are you sure you want to delete this stimulus type? This will also delete all stimuli of this type.",
									DialogUtil.Icon.WARNING,
									DialogUtil.ButtonGroup.YESNO,
									false);

							if (choice == DialogUtil.Button.YES)
							{
								iterator.remove();
								project.getStimuli().removeIf(stimuli -> stimuli.getStimulusType().equals(stimuliType));
							}
						}
					});

					ImGui.endTable();
				}
				ImGui.endListBox();
			}

			ImGui.tableNextColumn();

			ImGui.endTable();
		}
	}
}

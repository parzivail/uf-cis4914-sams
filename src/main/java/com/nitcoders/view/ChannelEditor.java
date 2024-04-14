package com.nitcoders.view;

import com.nitcoders.model.Project;
import imgui.ImGui;
import imgui.flag.ImGuiTableColumnFlags;
import imgui.type.ImString;

public class ChannelEditor
{
	public static void draw(Project project)
	{
		ImGui.text("Audio Channel Labels");

		if (ImGui.beginTable("channelEditor", 2))
		{
			ImGui.tableSetupColumn("label", ImGuiTableColumnFlags.WidthFixed, 100);
			ImGui.tableSetupColumn("field", ImGuiTableColumnFlags.WidthStretch);

			for (var channelEntry : project.getChannelNameMap().entrySet())
			{
				ImGui.tableNextColumn();
				ImGui.text(channelEntry.getKey().getName());
				ImGui.tableNextColumn();

				var sentenceStr = new ImString(channelEntry.getValue(), 512);
				if (ImGui.inputText("##channelName" + channelEntry.getKey().getName(), sentenceStr))
					project.getChannelNameMap().put(channelEntry.getKey(), sentenceStr.get());
			}

			ImGui.endTable();
		}
	}
}

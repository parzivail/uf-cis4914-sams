package com.nitcoders.view;

import com.nitcoders.model.Project;
import com.nitcoders.util.AudioUtil;
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

			drawChannelEditor(project, AudioUtil.Channel.Left);
			drawChannelEditor(project, AudioUtil.Channel.Right);
			drawChannelEditor(project, AudioUtil.Channel.Both);

			ImGui.endTable();
		}
	}

	private static void drawChannelEditor(Project project, AudioUtil.Channel channel)
	{
		ImGui.tableNextColumn();
		ImGui.text(channel.getName());
		ImGui.tableNextColumn();

		var sentenceStr = new ImString(project.getChannelName(channel), 512);
		if (ImGui.inputText("##channelName" + channel.getName(), sentenceStr))
			project.getChannelNameMap().put(channel, sentenceStr.get());
	}
}

package com.nitcoders.util;

import imgui.ImVec2;
import imgui.internal.ImGui;
import imgui.internal.flag.ImGuiAxis;
import imgui.type.ImFloat;
import org.lwjgl.glfw.GLFW;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ImGuiHelper
{
	@FunctionalInterface
	public interface IteratorConsumer<T>
	{
		void consume(Iterator<T> iterator, int i, T item);
	}

	public static boolean isShiftDown()
	{
		return ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT);
	}

	public static boolean isCtrlDown()
	{
		return ImGui.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || ImGui.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL);
	}

	public static boolean splitter(boolean split_vertically, float thickness, ImFloat size1, ImFloat size2, float min_size1, float min_size2, float splitter_long_axis_size)
	{
		var cursorPos = ImGui.getCursorPos();
		var id = ImGui.getID("##splitter");
		var min = new ImVec2(cursorPos.x + (split_vertically ? size1.get() : 0), cursorPos.y + (split_vertically ? 0 : size1.get()));
		var itemSizeX = ImGui.calcItemSizeX(split_vertically ? thickness : splitter_long_axis_size, split_vertically ? splitter_long_axis_size : thickness, 0, 0);
		var itemSizeY = ImGui.calcItemSizeY(split_vertically ? thickness : splitter_long_axis_size, split_vertically ? splitter_long_axis_size : thickness, 0, 0);
		return ImGui.splitterBehavior(min.x, min.y, min.x + itemSizeX, min.y + itemSizeY, id, split_vertically ? ImGuiAxis.X : ImGuiAxis.Y, size1, size2, min_size1, min_size2, 0);
	}

	public static void filePicker(String button, Supplier<String> filenameGetter, Consumer<String> filenameSetter, String title, String filterName, String... filters)
	{
		if (ImGui.button(button))
			DialogUtil.openFile(title, filterName, false, filters)
			          .ifPresent(paths -> filenameSetter.accept(paths[0]));

		ImGui.sameLine();

		var filename = filenameGetter.get();
		if (filename != null)
			ImGui.textWrapped(filename);
		else
			ImGui.textDisabled("No file selected");
	}
}

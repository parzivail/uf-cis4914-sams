package com.nitcoders;

import com.nitcoders.util.IoUtil;
import com.nitcoders.view.StimuliEditor;
import com.nitcoders.view.StimulusTypesEditor;
import com.nitcoders.view.SubjectsEditor;
import imgui.ImFont;
import imgui.ImFontConfig;
import imgui.ImGui;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MainWindow extends Application
{
	private static ImFont smallFont;

	private final ProjectManager projectManager;

	public MainWindow()
	{
		projectManager = new ProjectManager();
	}

	@Override
	protected void configure(Configuration config)
	{
		config.setTitle("Sample Administration and Management System (SAMS)");
		getColorBg().set(0, 0, 0, 1);
	}

	@Override
	protected void initImGui(Configuration config)
	{
		super.initImGui(config);

		final var io = ImGui.getIO();
		io.setIniFilename(null);
		io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
		io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
		io.setConfigDockingWithShift(true);
		io.setConfigViewportsNoTaskBarIcon(true);

		final ImFontConfig fontConfig = new ImFontConfig();

		fontConfig.setOversampleH(1);
		fontConfig.setOversampleV(1);
		fontConfig.setPixelSnapH(true);
		fontConfig.setRasterizerMultiply(1.25f);

		try
		{
			io.getFonts().addFontFromMemoryTTF(Files.readAllBytes(Path.of("C:\\Windows\\Fonts\\segoeui.ttf")), 20, fontConfig);

			fontConfig.setMergeMode(true);
			io.getFonts().addFontFromMemoryTTF(IoUtil.getBytes("fonts\\icons.ttf"), 20, fontConfig, IconFont.ICON_RANGE);

			fontConfig.setMergeMode(false);
			smallFont = io.getFonts().addFontFromMemoryTTF(Files.readAllBytes(Path.of("C:\\Windows\\Fonts\\segoeui.ttf")), 18, fontConfig);

			fontConfig.setMergeMode(true);
			io.getFonts().addFontFromMemoryTTF(IoUtil.getBytes("fonts\\icons.ttf"), 18, fontConfig, IconFont.ICON_RANGE);

			io.getFonts().build();
		}
		catch (IOException | URISyntaxException e)
		{
			throw new RuntimeException(e);
		}

		fontConfig.destroy();
	}

	@Override
	public void process()
	{
		var flags = ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.MenuBar;
		flags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBackground;
		flags |= ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus | ImGuiWindowFlags.NoDecoration;

		var v = ImGui.getMainViewport();

		ImGui.setNextWindowPos(v.getWorkPosX(), v.getWorkPosY());
		ImGui.setNextWindowSize(v.getWorkSizeX(), v.getWorkSizeY());

		ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0);

		if (ImGui.begin("SAMS", new ImBoolean(true), flags))
		{
			ImGui.popStyleVar();

			drawMainWindow();
		}
		ImGui.end();
	}

	private void drawMainWindow()
	{
		drawMenuBar();
		drawTabs();
	}

	private void drawTabs()
	{
		if (ImGui.beginTabBar("featureTabs"))
		{
			if (ImGui.beginTabItem("Stimulus Types"))
			{
				StimulusTypesEditor.draw(projectManager.getProject());
				ImGui.endTabItem();
			}

			if (ImGui.beginTabItem("Stimuli"))
			{
				StimuliEditor.draw(projectManager.getProject());
				ImGui.endTabItem();
			}

			if (ImGui.beginTabItem("Subjects"))
			{
				SubjectsEditor.draw(projectManager.getProject());
				ImGui.endTabItem();
			}

			if (ImGui.beginTabItem("Practice"))
			{
				ImGui.endTabItem();
			}

			if (ImGui.beginTabItem("Administer"))
			{
				ImGui.endTabItem();
			}

			ImGui.endTabBar();
		}
	}

	private void drawMenuBar()
	{
		if (ImGui.beginMenuBar())
		{
			if (ImGui.beginMenu("File"))
			{
				if (ImGui.menuItem(IconFont.filebrowser + " Open Project"))
					projectManager.openProject();

				if (ImGui.beginMenu(IconFont.time + " Recent Projects"))
				{
					for (var recent : projectManager.getRecentProjects())
					{
						if (ImGui.menuItem(recent.name()))
							projectManager.openProject(recent.filename());
					}

					ImGui.endMenu();
				}

				ImGui.separator();

				if (ImGui.menuItem(IconFont.import_file + " Save Project"))
					projectManager.saveProject();

				if (ImGui.menuItem(IconFont.current_file + " Save Project As..."))
					projectManager.saveProjectAs();

				ImGui.endMenu();
			}
			ImGui.endMenuBar();
		}
	}

	public static ImFont getSmallFont()
	{
		return smallFont;
	}

	public static void main(String[] args)
	{
		launch(new MainWindow());
	}
}

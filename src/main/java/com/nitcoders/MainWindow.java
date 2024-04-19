package com.nitcoders;

import com.nitcoders.util.DialogUtil;
import com.nitcoders.util.IoUtil;
import com.nitcoders.view.*;
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
	private static final String SAMS_TITLE = "Sample Administration and Management System (SAMS)";
	private static ImFont smallFont;
	private static ImFont largeFont;

	private final ProjectManager projectManager;

	public MainWindow()
	{
		projectManager = new ProjectManager();

		/*
			3 types
				SO,CC,Other
				~24 of each
		 */
	}

	@Override
	protected void configure(Configuration config)
	{
		config.setTitle(SAMS_TITLE);
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

			fontConfig.setMergeMode(false);
			largeFont = io.getFonts().addFontFromMemoryTTF(Files.readAllBytes(Path.of("C:\\Windows\\Fonts\\segoeui.ttf")), 48, fontConfig);
			fontConfig.setMergeMode(true);
			io.getFonts().addFontFromMemoryTTF(IoUtil.getBytes("fonts\\icons.ttf"), 48, fontConfig, IconFont.ICON_RANGE);

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

		if (!projectManager.hasOpenProject())
		{
			ImGui.text(SAMS_TITLE);
			ImGui.text("Open or create a project to get started!");
		}
		else
			drawTabs();
	}

	private void drawTabs()
	{
		if (ImGui.beginTabBar("featureTabs"))
		{
			if (ImGui.beginTabItem("Audio Channels"))
			{
				ChannelEditor.draw(projectManager.getProject());
				ImGui.endTabItem();
			}

			if (ImGui.beginTabItem("Stimulus Types"))
			{
				StimulusTypesEditor.draw(projectManager.getProject());
				ImGui.endTabItem();
			}

			if (ImGui.beginTabItem("Stimuli"))
			{
				StimuliEditor.draw(projectManager, projectManager.getProject());
				ImGui.endTabItem();
			}

			if (ImGui.beginTabItem("Subjects"))
			{
				SubjectsEditor.draw(projectManager.getProject());
				ImGui.endTabItem();
			}

			if (ImGui.beginTabItem("Experiment Playlist"))
			{
				PlaylistEditor.draw(projectManager, projectManager.getProject());
				ImGui.endTabItem();
			}

			if (ImGui.beginTabItem("Practice"))
			{
				AdministerView.draw(projectManager, projectManager.getProject(), projectManager.getProject().getBakedPracticePlaylist());
				ImGui.endTabItem();
			}

			if (ImGui.beginTabItem("Administer"))
			{
				AdministerView.draw(projectManager, projectManager.getProject(), projectManager.getProject().getBakedPlaylist());
				ImGui.endTabItem();
			}

			if (ImGui.beginTabItem("Score"))
			{
				// TODO: report for all stimuli per subject
				ScoreView.draw(projectManager.getProject());
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
				if (ImGui.menuItem(IconFont.file_new + " New Project"))
				{
					var result = projectManager.hasOpenProject() ? DialogUtil.notifyChoice(
							"Are you sure?",
							"Are you sure you want to create a new project? Any unsaved changes in your current project will be lost.",
							DialogUtil.Icon.WARNING,
							DialogUtil.ButtonGroup.YESNO,
							false
					) : DialogUtil.Button.YES;
					if (result == DialogUtil.Button.YES)
						projectManager.createProject();
				}

				if (ImGui.menuItem(IconFont.filebrowser + " Open Project"))
				{
					var result = projectManager.hasOpenProject() ? DialogUtil.notifyChoice(
							"Are you sure?",
							"Are you sure you want to open another project? Any unsaved changes in your current project will be lost.",
							DialogUtil.Icon.WARNING,
							DialogUtil.ButtonGroup.YESNO,
							false
					) : DialogUtil.Button.YES;
					if (result == DialogUtil.Button.YES)
						projectManager.openProject();
				}

				var recents = projectManager.getRecentProjects();

				ImGui.beginDisabled(recents.isEmpty());
				if (ImGui.beginMenu(IconFont.time + " Recent Projects"))
				{
					for (var recent : recents)
					{
						if (ImGui.menuItem(recent.name()))
						{
							var result = projectManager.hasOpenProject() ? DialogUtil.notifyChoice(
									"Are you sure?",
									"Are you sure you want to create a new project? Any unsaved changes in your current project will be lost.",
									DialogUtil.Icon.WARNING,
									DialogUtil.ButtonGroup.YESNO,
									false
							) : DialogUtil.Button.YES;
							if (result == DialogUtil.Button.YES)
								projectManager.openProject(recent.filename());
						}
					}

					ImGui.endMenu();
				}
				ImGui.endDisabled();

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

	public static ImFont getLargeFont()
	{
		return largeFont;
	}

	public static void main(String[] args)
	{
		launch(new MainWindow());
	}
}

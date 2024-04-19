package com.nitcoders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nitcoders.model.Config;
import com.nitcoders.model.Project;
import com.nitcoders.model.ProjectReference;
import com.nitcoders.util.DialogUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class ProjectManager
{
	private static final String CONFIG_FILE = "config.json";
	private static final String PROJECT_FILE_FILTER = "*.sams";
	private static final Gson GSON = new GsonBuilder()
			.create();

	private String filename;
	private Project currentProject;

	private Config config;

	public ProjectManager()
	{
		loadConfig();
	}

	Path getProjectPath()
	{
		return Path.of(filename).getParent();
	}

	public Path pathAbsoluteToProjectRelative(Path path)
	{
		return getProjectPath().relativize(path);
	}

	public Path pathProjectRelativeToAbsolute(Path path)
	{
		return getProjectPath().resolve(path);
	}

	public Project getProject()
	{
		return currentProject;
	}

	public void createProject()
	{
		this.currentProject = new Project();
		this.filename = null;
	}

	public void openProject()
	{
		DialogUtil.openFile("Open Project", "SAMS Project (*.sams)", false, PROJECT_FILE_FILTER)
		          .ifPresent(paths -> {
			          openProject(paths[0]);
		          });
	}

	public void openProject(String filename)
	{
		try
		{
			currentProject = GSON.fromJson(Files.readString(Path.of(filename)), Project.class);
			this.filename = filename;

			config.recentProjects().add(new ProjectReference(new File(filename).getName(), filename));
			saveConfig();
		}
		catch (Exception e)
		{
			DialogUtil.notify("Error", "Could not open project: " + e.getMessage(), DialogUtil.Icon.ERROR);
		}
	}

	public void saveProject()
	{
		if (filename == null)
		{
			saveProjectAs();
			return;
		}

		try
		{
			Files.writeString(Path.of(filename), GSON.toJson(currentProject));
		}
		catch (Exception e)
		{
			DialogUtil.notify("Error", "Could not save project: " + e.getMessage(), DialogUtil.Icon.ERROR);
		}
	}

	public void saveProjectAs()
	{
		DialogUtil.saveFile("Open Project", PROJECT_FILE_FILTER).ifPresent(newFilename -> {
			try
			{
				Files.writeString(Path.of(newFilename), GSON.toJson(this));
				this.filename = newFilename;

				config.recentProjects().add(new ProjectReference(new File(filename).getName(), filename));
				saveConfig();
			}
			catch (Exception e)
			{
				DialogUtil.notify("Error", "Could not save project: " + e.getMessage(), DialogUtil.Icon.ERROR);
			}
		});
	}

	private void saveConfig()
	{
		try
		{
			Files.writeString(Path.of(CONFIG_FILE), GSON.toJson(config));
		}
		catch (Exception e)
		{
			// Ignored
		}
	}

	private void loadConfig()
	{
		try
		{
			config = GSON.fromJson(Files.readString(Path.of(CONFIG_FILE)), Config.class);
		}
		catch (Exception e)
		{
			// Ignored
		}

		if (config == null)
			config = new Config(new HashSet<>());
	}

	public Set<ProjectReference> getRecentProjects()
	{
		if (config.recentProjects() == null)
			return Set.of();

		return config.recentProjects();
	}

	public boolean hasOpenProject()
	{
		return currentProject != null;
	}
}

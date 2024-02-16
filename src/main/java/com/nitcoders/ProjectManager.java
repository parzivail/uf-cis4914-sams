package com.nitcoders;

import com.nitcoders.model.Project;
import com.nitcoders.model.ProjectReference;

import java.util.List;

public class ProjectManager
{
	private Project currentProject;

	public ProjectManager()
	{
		// TODO: this is temporary
		currentProject = new Project();
	}

	public Project getProject()
	{
		return currentProject;
	}

	public void openProject()
	{
		// TODO: prompt user to select a project file and call openProject(String)
	}

	public void openProject(String filename)
	{
		// TODO: open the given project and set the current project
	}

	public void saveProject()
	{
		// TODO: write the current project to disk
	}

	public void saveProjectAs()
	{
		// TODO: prompt user and write the current project to disk in a different location
	}

	public List<ProjectReference> getRecentProjects()
	{
		return List.of(
				new ProjectReference("Study 2024A", null),
				new ProjectReference("Study 2024B", null),
				new ProjectReference("Study 2024C", null),
				new ProjectReference("Study 2024D", null)
		);
	}
}

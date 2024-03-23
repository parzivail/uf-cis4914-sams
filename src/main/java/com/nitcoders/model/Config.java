package com.nitcoders.model;

import java.util.HashSet;

public record Config(HashSet<ProjectReference> recentProjects)
{
}

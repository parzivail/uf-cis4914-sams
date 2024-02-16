package com.nitcoders.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class IoUtil
{
	public static byte[] getBytes(String path) throws URISyntaxException, IOException
	{
		var resource = IoUtil.class.getClassLoader().getResource(path);
		return Files.readAllBytes(Paths.get(resource.toURI()));
	}
}

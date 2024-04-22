package com.nitcoders.util;

import java.io.IOException;
import java.net.URISyntaxException;

public class IoUtil
{
	public static byte[] getBytes(String path) throws URISyntaxException, IOException
	{
		try (var resource = IoUtil.class.getResourceAsStream(path))
		{
			return resource.readAllBytes();
		}
	}
}

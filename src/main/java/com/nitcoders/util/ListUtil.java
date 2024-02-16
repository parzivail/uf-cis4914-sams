package com.nitcoders.util;

import java.util.List;

public class ListUtil
{
	public static <T> void iterate(List<T> items, ImGuiHelper.IteratorConsumer<T> function)
	{
		var i = 0;
		var it = items.iterator();

		while (it.hasNext())
		{
			function.consume(it, i, it.next());
			i++;
		}
	}
}

package com.nitcoders.util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioUtil
{
	static class AudioListener implements LineListener
	{
		private boolean done = false;

		@Override
		public synchronized void update(LineEvent event)
		{
			LineEvent.Type eventType = event.getType();
			if (eventType == LineEvent.Type.STOP || eventType == LineEvent.Type.CLOSE)
			{
				done = true;
				notifyAll();
			}
		}

		public synchronized void waitUntilDone() throws InterruptedException
		{
			while (!done)
				wait();
		}
	}

	private static void playClip(File clipFile) throws IOException, UnsupportedAudioFileException, LineUnavailableException, InterruptedException
	{
		AudioListener listener = new AudioListener();
		try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(clipFile))
		{
			Clip clip = AudioSystem.getClip();

			try (clip)
			{
				clip.addLineListener(listener);

				clip.open(audioInputStream);
				clip.start();

				listener.waitUntilDone();
			}
		}
	}

	public static void tryPlay(String filename)
	{
		try
		{
			playClip(new File(filename));
		}
		catch (Exception e)
		{
			DialogUtil.notify("Error", "Could not play audio: " + e.getMessage(), DialogUtil.Icon.ERROR);
		}
	}
}

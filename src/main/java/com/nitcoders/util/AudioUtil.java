package com.nitcoders.util;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

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

	private static void playClip(File clipFile, AudioChannel channel) throws IOException, UnsupportedAudioFileException, LineUnavailableException, InterruptedException
	{
		AudioListener listener = new AudioListener();
		try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(clipFile))
		{
			Clip clip = AudioSystem.getClip();

			try (clip)
			{
				clip.addLineListener(listener);

				clip.open(audioInputStream);

				var ctrl = clip.getControls();

				// [0] gain (FloatControl)
				// [1] mute (BooleanControl)
				// [2] balance (FloatControl)
				// [3] pan (FloatControl)

				if (ctrl.length > 2)
				{
					// Mono audio only has [0] gain and [1] mute

					var balanceControl = ((FloatControl)ctrl[2]);

					switch (channel)
					{
						case Left -> balanceControl.setValue(-1);
						case Right -> balanceControl.setValue(1);
						case Both -> balanceControl.setValue(0);
					}
				}

				clip.start();

				listener.waitUntilDone();
			}
		}
	}

	public static void tryPlay(Path filename, AudioChannel channel)
	{
		try
		{
			playClip(filename.toFile(), channel);
		}
		catch (Exception e)
		{
			DialogUtil.notify("Error", "Could not play audio: " + e.getMessage(), DialogUtil.Icon.ERROR);
		}
	}
}

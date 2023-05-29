package org.lolicode.nekomusiccli.libs.ogg.oggdecoder;

import java.nio.ByteBuffer;

public class OggData {
	/** The data that has been read from the OGG file */
	public ByteBuffer data;
	
	/** The sampling rate */
	public int rate;
	
	/** The number of channels in the sound file */
	public int channels;
}

package de.ecconia.mc.jclient.network.connector;

public interface PacketHandler
{
	/**
	 * Called when a packet got received.
	 * The packet is unencrypted and uncompressed.
	 * @param packet - the ID and the content of the packet
	 */
	public void onPacketReceive(byte[] packet);
}

package io.github.cellularghost;

import org.savarese.vserv.tcpip.IPPacket;
import org.savarese.vserv.tcpip.TCPPacket;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;

public class ReceiveThread extends Thread {
	private ForwardingService forwardingService;

	private OutputStream outputStream;

	public ReceiveThread(ForwardingService forwardService, OutputStream outputStream) {
		this.forwardingService = forwardService;
		this.outputStream = outputStream;
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			byte[] buffer = new byte[1500];
			int read = forwardingService.rawSocket.read(buffer, 0);
			if (read >= 0) {
				try {
					IPPacket ipPacket = new IPPacket(buffer);
					InetAddress source = ipPacket.getSourceAsInetAddress();
					if (
							source.equals(InetAddress.getByName(forwardingService.ip))
							&& ipPacket.getProtocol() == 6
					) {
						TCPPacket tcpPacket = new TCPPacket(buffer);
						if (
									tcpPacket.getSourcePort() == forwardingService.dest_port
									&& tcpPacket.getDestinationPort() == forwardingService.source_port
						) {
							outputStream.write(buffer, tcpPacket.getCombinedHeaderByteLength(), read - tcpPacket.getCombinedHeaderByteLength());
						} else {
							outputStream.write(buffer, 0, read);
						}

					} else {
						outputStream.write(buffer, 0, read);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
}

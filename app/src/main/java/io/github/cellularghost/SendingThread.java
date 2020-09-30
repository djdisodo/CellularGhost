package io.github.cellularghost;

import org.savarese.vserv.tcpip.TCPPacket;

import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Random;

public class SendingThread extends Thread {

	private ForwardingService forwardingService;

	private InputStream inputStream;

	public SendingThread(ForwardingService forwardService, InputStream inputStream) {
		this.forwardingService = forwardService;
		this.inputStream = inputStream;
	}



	@Override
	public void run() {
		byte[] buffer = new byte[1000];
		while (!isInterrupted()) {
			try {
				while(this.inputStream.available() >= 1) {
					int read = this.inputStream.read(buffer);

					int host = InetAddress.getLocalHost().hashCode();


					int dest = InetAddress.getByName(forwardingService.ip).hashCode();


					//buildup new dummy tcp packet
					final TCPPacket tcpPacket = new TCPPacket(new byte[40 + read]);
					tcpPacket.setIPVersion(4);
					tcpPacket.setProtocol(6);
					tcpPacket.setIdentification(new Random().nextInt(65565));
					tcpPacket.setIPHeaderLength(20 >> 2);
					tcpPacket.setTCPHeaderLength(20 >> 2);
					tcpPacket.setIPPacketLength(40 + read);
					tcpPacket.setWindowSize(0);
					tcpPacket.setTTL(255);
					tcpPacket.setUrgentPointer(0);
					tcpPacket.setControlFlags(TCPPacket.MASK_RST);
					tcpPacket.setSequenceNumber(0);
					tcpPacket.setAckNumber(0);
					tcpPacket.setSourceAsWord(host);
					tcpPacket.setSourcePort(forwardingService.source_port);
					tcpPacket.setDestinationAsWord(dest);
					tcpPacket.setDestinationPort(forwardingService.dest_port);
					tcpPacket.setTCPDataByteLength(read);
					System.arraycopy(buffer, 0, tcpPacket.getData(), 40, read);
					tcpPacket.computeTCPChecksum(true);
					tcpPacket.computeIPChecksum(true);
					forwardingService.rawSocket.write(tcpPacket.getData(), 0, 40 + read);
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

package io.github.cellularghost;

import android.content.Intent;
import android.net.VpnService;

public class ForwardingService extends VpnService {

	public String ip;
	public int dest_port;
	public int source_port;

	public RawSocket rawSocket;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		ip = intent.getStringExtra("ip");
		dest_port = intent.getIntExtra("dest_port", 805);
		dest_port = intent.getIntExtra("source_port", 805);
		rawSocket = new RawSocket();
		Builder builder = new Builder();
		builder.setBlocking(true);
		builder.setMtu(1000);
		builder.addAddress(ip, 16);
		builder.addRoute("0.0.0.0", 0);
		return START_STICKY;
	}

}

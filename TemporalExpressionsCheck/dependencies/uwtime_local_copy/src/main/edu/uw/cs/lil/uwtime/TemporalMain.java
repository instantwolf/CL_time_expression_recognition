package edu.uw.cs.lil.uwtime;

import java.util.Arrays;

import org.eclipse.jetty.server.Server;

import edu.uw.cs.lil.uwtime.api.DocumentAnnotation;
import edu.uw.cs.lil.uwtime.api.TemporalAnnotator;
import edu.uw.cs.lil.uwtime.api.TemporalHandler;
import edu.uw.cs.utils.collections.ListUtils;

public class TemporalMain {
	public static void main(String[] args) throws Exception {
		if (args.length > 0) {
			DocumentAnnotation annotation = new TemporalAnnotator().extract(ListUtils.join(Arrays.asList(args), " "));
			System.out.println(annotation.getTimeML());
		}
		else {
			System.out.println("Creating server...");
			Server server = new Server(10001);
			server.setHandler(new TemporalHandler());

			server.start();
			System.out.println("Server ready...");
			server.join();
		}
	}
}

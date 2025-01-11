package edu.uw.cs.lil.uwtime.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TemporalLog {
	private static SimpleDateFormat					formatter			= new SimpleDateFormat(
																				"yyyy-MM-dd:HH.mm.ss");
	private static final String						LOG_ROOT			= "logs/";
	private static String							logDir				= LOG_ROOT;					// Put
																										// logs
																										// in
																										// root
																										// by
																										// default

	private static Map<String, List<PrintStream>>	printStreamMap		= new HashMap<>();
	private static Set<String>						suppressedLabels	= new HashSet<>();

	private static String							timestamp			= getTimestamp();

	public static void addAlias(String label, PrintStream ps) {
		if (!printStreamMap.containsKey(label)) {
			printStreamMap.put(label, new LinkedList<PrintStream>());
		}
		if (ps != null) {
			printStreamMap.get(label).add(ps);
		}
		try {
			printStreamMap.get(label).add(
					new PrintStream(new File(logDir + label + ".txt")));
		} catch (final FileNotFoundException e) {
			System.out.println("Unable to create log file for label: " + label);
		}
	}

	public static void print(String label, Object s) {
		for (final PrintStream ps : getPrintStreams(label)) {
			synchronized (ps) {
				ps.print(s);
			}
		}
	}

	public static void printf(String label, String s, Object... args) {
		for (final PrintStream ps : getPrintStreams(label)) {
			synchronized (ps) {
				ps.printf(s, args);
			}
		}
	}

	public static void println(String label) {
		for (final PrintStream ps : getPrintStreams(label)) {
			synchronized (ps) {
				ps.println();
			}
		}
	}

	public static void println(String label, Object s) {
		for (final PrintStream ps : getPrintStreams(label)) {
			synchronized (ps) {
				ps.println(s);
			}
		}
	}

	public static void setLogs(String dir) {
		logDir = LOG_ROOT + dir + "/" + timestamp + "/";
		new File(logDir).mkdirs();
	}

	public static void suppressLabel(String label) {
		suppressedLabels.add(label);
	}

	private static List<PrintStream> getPrintStreams(String s) {
		if (suppressedLabels.contains(s)) {
			return Collections.<PrintStream> emptyList();
		}
		if (!printStreamMap.containsKey(s)) {
			addAlias(s, null);
		}
		return printStreamMap.get(s);
	}

	private static String getTimestamp() {
		return formatter.format(new Date());
	}
}

package com.google.logsummarizer;

import com.google.logsummarizer.core.OperationInterval;
import com.google.logsummarizer.core.Render;
import com.google.logsummarizer.core.Summarize;
import com.google.logsummarizer.gcp.DataflowParser;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

/**
 * Summarizes and renders the Dataflow log given as argument.
 *
 * (logsummarizer can be used as a library, but this gives a simple command-line access)
 */
public class Main {

    public static void main(String[] args) throws Exception {
        String jobId = null;
        String logFile;
        if (args.length<1 || args.length>2) {
            System.out.println("Usage: Main <logfile> [jobId]");
        }
        logFile = args[0];
        if (args.length>1) jobId = args[1];

        List<OperationInterval> ops = DataflowParser.parse(logFile, jobId);
        if (null!=jobId) {
            System.out.println("Summary for Job ID "+jobId);
        } else {
            System.out.println("Summary for "+logFile);
        }
        new Summarize(ops).printTable();
        new Render(ops).toSvg(new PrintStream(new File("out.svg")));
        System.out.println("Graph saved to 'out.svg'");
    }

}

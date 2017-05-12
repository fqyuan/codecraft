package com.filetool.main;

import java.util.ArrayList;

import com.cacheserverdeploy.deploy.Deploy;
import com.filetool.util.FileUtil;
import com.filetool.util.LogUtil;
import com.fqy.cdn.Graph;
import com.fqy.cdn.MCFF;

//
public class Main {
	/*
	 * Arg:/Users/fqyuan/Documents/Java_Work/CDN/case_example/m2.txt
	 * /Users/fqyuan/Documents/Java_Work/CDN/case_example/res.txt
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("please input args: graphFilePath, resultFilePath");
			return;
		}

		String graphFilePath = args[0];
		String resultFilePath = args[1];

		LogUtil.printLog("Begin");

		// 璇诲彇杈撳叆鏂囦欢
		String[] graphContent = FileUtil.read(graphFilePath, null);

		// Test part:
		// for (int i = 0; i < graphContent.length; i++)
		// System.out.println(graphContent[i]);

		/********
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 */
		Graph graph = new Graph();
		int arr[] = { 0, 3, 22 };
		ArrayList<Integer> serverId = new ArrayList<>();
		for (int i = 0; i < arr.length; i++)
			serverId.add(arr[i]);
		graph.parseInput(graphContent);
		MCFF mc = new MCFF(graph);
		mc.getResult(serverId);
		/*********************
		 * 
		 * 
		 * 
		 * 
		 * 
		 */
		String[] resultContents = Deploy.deployServer(graphContent);

		if (hasResults(resultContents)) {
			FileUtil.write(resultFilePath, resultContents, false);
		} else {
			FileUtil.write(resultFilePath, new String[] { "NA" }, false);
		}
		LogUtil.printLog("End");
	}

	private static boolean hasResults(String[] resultContents) {
		if (resultContents == null) {
			return false;
		}
		for (String contents : resultContents) {
			if (contents != null && !contents.trim().isEmpty()) {
				return true;
			}
		}
		return false;
	}

}

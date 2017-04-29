import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileWriter;
import java.io.IOException;

public class Controller {
	private static final Logger logger = LoggerFactory.getLogger(Controller.class);

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			logger.info("Needed parameters: ");
			logger.info("\t rootFolder (it will contain intermediate crawl data)");
			logger.info("\t numberOfCralwers (number of concurrent threads)");
			return;
		}

		String rootFolder = args[0];
		int numberOfCrawlers = Integer.parseInt(args[1]);

		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(rootFolder);
		config.setMaxPagesToFetch(20000);
		config.setMaxDepthOfCrawling(16);
		// config.setPolitenessDelay(100);
		// config.setIncludeBinaryContentInCrawling(true);
		config.setIncludeHttpsPages(true);
		// config.setFollowRedirects(true);

		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

		controller.addSeed("http://www.nytimes.com/");
		controller.start(MyCrawler.class, numberOfCrawlers);

		System.out.println("Fetches attempted:" + MyCrawler.attemptedFetch);
		System.out.println("Succussful attempted:" + MyCrawler.succeededFetch);
		System.out.println("Total URLs extracted:" + MyCrawler.allUrlsList.size());
		writeFetchCsv(MyCrawler.fetchedUrl, MyCrawler.statusCodeList);
		writeVisitCsv(MyCrawler.downloadedUrlsList, MyCrawler.sizeList, MyCrawler.outgoinglinksList,
				MyCrawler.contentTypeList);
		writeUrlsCsv(MyCrawler.allUrlsList);
	}

	private static void writeFetchCsv(ArrayList<String> fetchedUrl, ArrayList<String> statusCodeList) {
		FileWriter fileWriter = null;
//		HashMap<String, String> hmap = new HashMap<String, String>();
//		for (String i : statusCodeList) {
//			hmap.put(i, "s");
//		}
//		for(Entry<String, String> entry : hmap.entrySet())
//	    {   
//	         System.out.println(entry.getKey() + " : " +entry.getValue().length());
//	    }
		try {
			fileWriter = new FileWriter("fetch_NewsSite.csv");
			for (int i = 0; i < fetchedUrl.size(); i++) {
				fileWriter.append(fetchedUrl.get(i));
				fileWriter.append(",");
				fileWriter.append(statusCodeList.get(i));
				fileWriter.append("\n");
			}
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter");
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			}

		}
		return;
	}

	private static void writeVisitCsv(ArrayList<String> a, ArrayList<String> b, ArrayList<String> c,
			ArrayList<String> d) {
		FileWriter fileWriter = null;
		long pageSize = 0;
		int lessThan1KB = 0;
		int oneKB210KB = 0;
		int tenKB2100KB = 0;
		int to1MB = 0;
		int greaterThan1MB = 0;
		
		try {
			fileWriter = new FileWriter("visit_NewsSite.csv");
			for (int i = 0; i < a.size(); i++) {
				fileWriter.append(a.get(i));
				fileWriter.append(",");
				fileWriter.append(b.get(i));
				fileWriter.append(",");
				fileWriter.append(c.get(i));
				fileWriter.append(",");
				fileWriter.append(d.get(i));
				fileWriter.append("\n");
			}
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter");
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			}
		}
		for (String i : b) {
			pageSize = Long.valueOf(i);
			if (pageSize < 1024) {
				lessThan1KB++;
			} else if (pageSize < 10240) {
				oneKB210KB++;
			} else if (pageSize < 1024000) {
				tenKB2100KB++;
			} else if (pageSize < 1048576) {
				to1MB++;
			} else {
				greaterThan1MB++;
			}			
		}
		System.out.println("lessThan1KB: " + lessThan1KB);
		System.out.println("oneKB210KB: " + oneKB210KB);
		System.out.println("tenKB2100KB: " + tenKB2100KB);
		System.out.println("to1MB: " + to1MB);
		System.out.println("greaterThan1MB: " + greaterThan1MB);
		return;
	}

	private static void writeUrlsCsv(List<String> a) {
		FileWriter fileWriter = null;
		long uniqueUrlwithin = 0;
		Set<String> b = new HashSet<String>();
		for (String i : a) {
			if (i.contains("www.nytimes.com")) {
				uniqueUrlwithin++;
			}
			b.add(i);
		}
		System.out.println("unique URLs:" + b.size());
		System.out.println("unique URLs within:" + uniqueUrlwithin);
		try {
			fileWriter = new FileWriter("urls_NewsSite.csv");
			for (int i = 0; i < a.size(); i++) {
				fileWriter.append(a.get(i));
				fileWriter.append(",");
				if (a.get(i).contains("www.nytimes.com")) {
					fileWriter.append("OK");
				} else {
					fileWriter.append("N_OK");
				}
				fileWriter.append("\n");
			}
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter");
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			}
		}
		return;
	}
}

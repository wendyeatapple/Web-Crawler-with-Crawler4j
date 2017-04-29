import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import java.io.UnsupportedEncodingException;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {


	private static final Pattern FILTERS = Pattern.compile(".*(\\.(css|js|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
	static int attemptedFetch = 0;
	static int succeededFetch = 0;
	
	static ArrayList<String> fetchedUrl = new ArrayList<String>();
	static ArrayList<String> statusCodeList = new ArrayList<String>();
	
	static ArrayList<String> downloadedUrlsList = new ArrayList<String>();
	static ArrayList<String> contentTypeList = new ArrayList<String>();
	static ArrayList<String> outgoinglinksList = new ArrayList<String>();
	static ArrayList<String> sizeList = new ArrayList<String>();
	
	static List<String> allUrlsList = new ArrayList<String>();
	static ArrayList<String> indicatorList = new ArrayList<String>();

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		
		String href = url.getURL().toLowerCase();
		
		if (FILTERS.matcher(href).matches() == true) {
			return false;
		}
		return href.contains("www.nytimes.com");
	}
	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription){
		attemptedFetch++;
		if (statusCode == 200) {
			succeededFetch++;
		}
		fetchedUrl.add(webUrl.toString());
		statusCodeList.add(Integer.toString(statusCode));
		System.out.println("FetchedUrl: {" + webUrl + "}");
//		System.out.println("statuscode: {" + statusCode + "}");
	}
	@Override
	public void visit(Page page) {
		downloadedUrlsList.add(page.getWebURL().getURL());
		contentTypeList.add(page.getContentType());
//		System.out.println("DownloadedUrls: {" + page.getWebURL().getURL() + "}");
//		System.out.println("Content_type: {" + page.getContentType()
//		 + "}");
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData parseData = (HtmlParseData) page.getParseData();
			Set<WebURL> links = parseData.getOutgoingUrls();
			for ( WebURL i : links) {			
				allUrlsList.add(i.toString());
			}
			outgoinglinksList.add(Integer.toString(links.size()));
//			System.out.println("# of outgoinglinks" + links.size());	
			try {
				sizeList.add(Integer.toString(parseData.getText().getBytes("UTF-8").length));
//				System.out.println("Size: " + parseData.getText().getBytes("UTF-8").length);
			} catch (UnsupportedEncodingException ignored) {
				// Do nothing
			}
		}

		
	}
	


	
}
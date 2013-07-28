package de.fzj.unicore.cis.aggregator;

public class MockIP {
	
	String url;
	
	
	public MockIP(String url) {
		this.url = url;
	}
	
	public String getInfo(){
		return "UNICORE 6 Information from "+url;
	}

	public String getUrl() {
		return url;
	}

}

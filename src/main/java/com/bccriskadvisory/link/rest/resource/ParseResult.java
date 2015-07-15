package com.bccriskadvisory.link.rest.resource;

import com.bccriskadvisory.link.rest.gson.GsonObject;

public class ParseResult extends GsonObject {
	
	private String markdown;
	private String markup;
	private String html;
	
	public void setMarkdown(String markdown) {
		this.markdown = markdown;
	}
	public void setMarkup(String markup) {
		this.markup = markup;
	}
	public void setHtml(String html) {
		this.html = html;
	}

}

package org.grameenfoundation.cch.supervisor.model;

import java.util.ArrayList;

public class Payload {
	
	private ArrayList<? extends Object> data;
	private boolean result = false;
	private String resultResponse;
	private ArrayList<Object> responseData = new ArrayList<Object>();
	private String url;

	public Payload(){
		
	}
	
	public Payload(String url){
		this.setUrl(url);		
	}
	
	public Payload(ArrayList<? extends Object> data) {
		this.data = data;
	}

	public ArrayList<? extends Object> getData() {
		return data;
	}

	public void setData(ArrayList<? extends Object> data) {
		this.data = data;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getResultResponse() {
		return resultResponse;
	}

	public void setResultResponse(String resultResponse) {
		this.resultResponse = resultResponse;
	}

	public ArrayList<Object> getResponseData() {
		return responseData;
	}

	public void setResponseData(ArrayList<Object> responseData) {
		this.responseData = responseData;
	}
	
	public void addResponseData(Object obj){
		this.responseData.add(obj);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}

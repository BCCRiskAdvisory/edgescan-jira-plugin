package com.bccriskadvisory.link.rest.edgescan;

import java.util.List;
import java.util.Optional;

import com.bccriskadvisory.link.rest.gson.GsonObject;


public class EdgescanResponse extends GsonObject {
	
	private int total;
	private int count;
	
	private List<Asset> assets;
	private Asset asset;
	
	private List<Vulnerability> vulnerabilities;
	private Vulnerability vulnerability;
	
	public int getTotal() {
		return total;
	}
	
	public int getCount() {
		return count;
	}
	
	public Optional<List<Asset>> getAssets() {
		return Optional.ofNullable(assets);
	}
	
	public Optional<Asset> getAsset() {
		return Optional.ofNullable(asset);
	}
	
	public Optional<List<Vulnerability>> getVulnerabilities() {
		return Optional.ofNullable(vulnerabilities);
	}
	
	public Optional<Vulnerability> getDetailedVulnerability() {
		return Optional.ofNullable(vulnerability);
	}
}
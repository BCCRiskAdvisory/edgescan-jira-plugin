/**
 * Copyright (C) 2015 BCC Risk Advisory (info@bccriskadvisory.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bccriskadvisory.link.rest.edgescan;

import java.util.ArrayList;
import java.util.List;

import com.bccriskadvisory.link.rest.gson.GsonObject;

public class EdgescanResponse extends GsonObject {

	private int total;
	private int count;

	private List<Asset> assets;
	private Asset asset;

	private List<Vulnerability> vulnerabilities;
	private Vulnerability vulnerability;

	public EdgescanResponse() {
		this.assets = new ArrayList<>();
		this.vulnerabilities = new ArrayList<>();
	}

	public int getTotal() {
		return total;
	}

	public int getCount() {
		return count;
	}

	public List<Asset> getAssets() {
		return assets;
	}

	public Asset getAsset() {
		return asset;
	}

	public List<Vulnerability> getVulnerabilities() {
		return vulnerabilities;
	}

	public Vulnerability getDetailedVulnerability() {
		return vulnerability;
	}
}
package com.example.redis.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FargateMetaData {

	private String metadata;

	public FargateMetaData() {
		try {
			this.metadata = getMetaData();
		} catch (IOException e) {
			this.metadata = "{'error': 'failed to get the meta data: " + e + ".'}";
		}
	}

	public String getTaskArn() {
		try {
			return new JSONObject(metadata).getString("TaskARN");
		} catch (JSONException e) {
			return "No 'TaskARN' present in the Fargate metadata.";
		}
	}

	public String getImage() {
		try {
			JSONObject firstContainer = getContainers().getJSONObject(0);
			if(firstContainer != null) {
				return firstContainer.getString("Image");
			} else {
				return "No containers present in the Fargate metadata.";
			}
		} catch (JSONException e) {
			return "No 'Image' present in the Fargate 'Containers' metadata.";
		}
    }

	public JSONArray getContainers() {
		try {
			return new JSONObject(metadata).getJSONArray("Containers");
		} catch (JSONException e) {
			return new JSONArray();
		}
    }

	private String getMetaData() throws IOException {
		URL url = getMetaDataUrl();
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		return connectionResponseToString(connection);
	}

	private URL getMetaDataUrl() throws MalformedURLException {
		return new URL(System.getenv("ECS_CONTAINER_METADATA_URI_V4") + "/task");
	}

	private String connectionResponseToString(HttpURLConnection connection) {
		BufferedReader br = null;
		try {
			if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 400) {
				br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			}

			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			br.close();

			return response.toString();
		} catch (IOException e) {
			return e.toString();
		}
	}

	@Override
	public String toString() {
		return metadata;
	}

}
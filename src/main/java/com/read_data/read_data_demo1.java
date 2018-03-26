package com.read_data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

import com.alibaba.fastjson.*;

public class read_data_demo1 {
	static String trace =  "/mnt/data/20171020/";
	
	public static Client getClient() throws IOException {
		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "estest1").build();
		TransportClient client = new TransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress("10.10.41.153", 9300));

		return client;
	}

		public static void write2File(Client client) throws IOException {
			long start = System.currentTimeMillis();
			int scrollSize = 1000;
			SearchResponse response = null;
			FileWriter fw_trace1 = new FileWriter(trace + "test"+".txt");
	//		BufferedWriter bfw1 = new BufferedWriter(fw_trace1);
	 
	
			int i = 0;
			int trace_count = 0;
			while (response == null || response.getHits().hits().length != 0) {
			
				try {
					
					
					 BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();      
					
					response = client.prepareSearch("emcas-2017.10.20")
							.setTypes("trace")					
	//						.setQuery(QueryBuilders.filteredQuery(boolQuery,FilterBuilders.termFilter("collectorId", "55")))
							.setQuery(QueryBuilders.matchAllQuery())
	//						.addSort("@timestamp",SortOrder.ASC)
							.setSize(scrollSize).setFrom(i * scrollSize)						 
							.execute().actionGet();
				} catch (IndexMissingException e) {
					System.out.println("not found");
				}
	
				SearchHits hits = response.getHits();
				System.out.println(hits.hits().length);
				
		 
	
				for (int j = 0; j < hits.getHits().length; j++) {
					String jsonstr = hits.getHits()[j].getSourceAsString();
					JSONObject json_1 = JSON.parseObject(jsonstr);
	
					
					if (trace_count % 50000 == 0) {
	//					File file = new File("E:/es_data_test/test/" + (trace_count+1)/500 + ".txt");
						fw_trace1.close();
						fw_trace1 = new FileWriter(trace + (trace_count+1)/50000 + ".txt");
																		
	//					fw_trace1.write(json_1.toString() + '\r');
	//					fw_trace1.flush();
					}
						
						fw_trace1.write(json_1.toString() + '\r');
						fw_trace1.flush();
	
													
					trace_count++;
				}
				i++;
			}
			
			
			fw_trace1.close();
			long end = System.currentTimeMillis();
			long totalTime = end - start;
			System.out.println("共耗时:" + totalTime);
		}

 
	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
		read_data_demo1 instance = new read_data_demo1();
		Client client = instance.getClient();		
		write2File(client);		
	}
}

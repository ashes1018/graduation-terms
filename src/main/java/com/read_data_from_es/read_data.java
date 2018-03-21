package org.elasticsearch.esTest;

import java.awt.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.admin.indices.stats.IndicesStatsAction;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;

import org.elasticsearch.index.query.QueryBuilders.*;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.search.SearchHits;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Hello world!
 *
 */
public class EsClient {
	static File trace = new File("E:/es data/emcas-2018.01.04_trace.txt");
	static File warning = new File("E:/es data/emcas-2018.01.04_warning.txt");
	static File other = new File("E:/es data/emcas-2018.01.04_other.txt");
	
	public static Client  getClient() throws IOException {
		 Settings settings = ImmutableSettings.settingsBuilder()
		            .put("cluster.name", "estest1")
		            .build();
		    TransportClient client = new TransportClient(settings).addTransportAddress(  
		            new InetSocketTransportAddress("10.10.41.153", 9300));
//		    FileWriter fw = new FileWriter(article);
//			BufferedWriter bfw = new BufferedWriter(fw);
			return client; 
	}
	
   
   public static HashSet<String> write2File(Client client) throws IOException{
	   long start = System.currentTimeMillis();
	   int scrollSize = 1000;
	   SearchResponse response = null;	   
	   FileWriter fw_trace1 = new FileWriter(trace);
	   BufferedWriter bfw1 = new BufferedWriter(fw_trace1);
	   
	   FileWriter fw_warning1 = new FileWriter(warning);
	   BufferedWriter bfw2 = new BufferedWriter(fw_warning1);
	   
	   FileWriter fw_other1 = new FileWriter(trace);
	   BufferedWriter bfw3 = new BufferedWriter(fw_other1);
	   
//	   ArrayList<Integer>collectid = new ArrayList<Integer>();
	   HashSet collectid = new HashSet();
	   
	   int i =0;
	   while (response == null || response.getHits().hits().length != 0 && i <=1) {			   
//		   if(i % 100 == 0){
//			   fw = new FileWriter(autoCreateFile(i/10+1));
//			   BufferedWriter bfw1 = new BufferedWriter(fw);	
//			   bfw = bfw1;
//			   System.out.println("这是第"+i/10+"万条数据");
//		   }		   
		   try{
		   response = client.prepareSearch("emcas-2017.10.16")
				    .setQuery(QueryBuilders.matchAllQuery())
		    		.setSize(scrollSize)	    		    		
		    		.setFrom(i*scrollSize)
//		    		.setFrom(0)
		    		.execute()
		    		.actionGet();
		   }
		   catch (IndexMissingException e) {
			 System.out.println("not found");
		}	
		   
		   
		   
		   
		   SearchHits hits = response.getHits();
		   
		   int trace_count = 0;
		   int warning_count =0;
		   int other_count = 0;
		   
		   for(int j = 0 ; j < hits.getHits().length; j++){
			   String jsonstr = hits.getHits()[j]
					   .getSourceAsString();			 
			   JSONObject json_1 = JSON.parseObject(jsonstr);
			   System.out.println(json_1);
			   
			  
			   
			   if(json_1.get("eventType").equals("trace")){
				   trace_count++;
				   collectid.add(json_1.get("collectorId"));
				   if(trace_count % 100000 == 0){
					   FileWriter fw_trace2 = new FileWriter(autoCreateFile(trace_count/100000));
					   BufferedWriter bfw_trace = new BufferedWriter(fw_trace2);
					   bfw1 = bfw_trace;
				   }
				   bfw1.write(json_1.toString()+'\r');
				   bfw1.flush();
			   }else if(json_1.get("eventType").equals("warning")){
				   warning_count++;
				   if(warning_count % 100 == 0){
					   FileWriter fw_warning2 = new FileWriter(autoCreateFile(warning_count/100));
					   BufferedWriter bfw_warning2 = new BufferedWriter(fw_warning2);
					   bfw2 = bfw_warning2;
				   }
				   bfw2.write(json_1.toString()+'\r');
				   bfw2.flush();
			   }else{
				   other_count++;
				   if(other_count % 100 == 0){
					   FileWriter fw_other2 = new FileWriter(autoCreateFile(other_count/100));
					   BufferedWriter bfw_other2 = new BufferedWriter(fw_other2);
					   bfw3 = bfw_other2;
				   }
				   bfw3.write(json_1.toString()+'\r');
				   bfw3.flush();
			   }
			  }		   			   	   		   			  
		   i++;
	   }	   	    
	   		bfw1.close();
	   		bfw2.close();
	   		bfw3.close();
	   		fw_other1.close();
	   		fw_trace1.close();
	   		fw_warning1.close();
	   		long end = System.currentTimeMillis();
	   		long totalTime = end - start;
	   		System.out.println("总耗时:"+totalTime);
	   		return collectid;
	  }
  
	
  
   public static File autoCreateFile(int i ) throws IOException {
	   File file = new File("E:/es data/"+i+".txt");	
	   file.createNewFile();
	   return file;
   }
   
   
   
   
    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
    	EsClient instance = new EsClient();
    	Client client = instance.getClient();
    	HashSet hashSet = new HashSet();
    	hashSet = write2File(client);
    	for (Object object : hashSet) {
			System.out.println(object);
		}
    	System.out.println(hashSet.size()+"size!!!!!!!!");
//    	GetResponse response = client.prepareGet("emcas-2017.10.18","trace","AV8tK5NeSBmsIUk260HQ")
//    	GetResponse response = client.prepareGet("emcas-2017.10.18","status","4")
//    	        .execute()
//    	        .actionGet();    	
//    	System.out.println(response.getSource());    	
    	
//    	SearchResponse response2 = client.prepareSearch("emcas-2018.01.04")
//				    .setQuery(QueryBuilders.matchAllQuery())
//		    		.setSize(0)	    		    			
//		    		.execute()
//		    		.actionGet();
//    	SearchHits hits = response2.getHits();
//    	long hitscount = hits.getTotalHits();
//    	System.out.println(hitscount);
	}
}


//fastjason
//setFrom setTo 写个循环读取


















//        //设置集群名称和自动感知节点 集群名称为"estest"
//    	Settings settings = Settings.builder()
//                .put("cluster.name", "estest").put("client.transport.sniff", true).build();
//        try {
//            //创建节点客户端，9300位节点的端口，不是集群端口
//            client = TransportClient.builder()
//            		.settings(settings)
//            		.build()
//                    .addTransportAddress(
//                     new InetSocketTransportAddress(InetAddress.getByName(host), 9300));           
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//            logger.info("unknownHost={}",host);
//        }
    
//    /**
//     * 通过ID查询数据，
//     * test为索引库，blog为类型，id为标识
//     */
//    
//    public static GetResponse get(String id){
//        return  client.prepareGet("test", "blog",id ).get();
//    }
//    /**
//     * 创建索引，indexName 索引名称
//     * */
//    public static CreateIndexResponse createIndex(String indexName) throws IOException {
//        return  client.admin().indices().prepareCreate(indexName).execute().actionGet();       
//    }
//    
//    //集群状态
//    public static void clusterStatus(){
//        //注意集群的client获取方式略有不同，
//        ClusterAdminClient clusterAdminClient = client.admin().cluster();
//        ClusterHealthResponse healths = clusterAdminClient.prepareHealth().get();
//        String clusterName = healths.getClusterName();
//        int numberOfDataNodes = healths.getNumberOfDataNodes();
//        int numberOfNodes = healths.getNumberOfNodes();
//        ClusterHealthStatus status = healths.getStatus();
//        System.out.println(clusterName+"###"+numberOfDataNodes+"###"+status.name());
//    }
//
//    public static void addType() throws IOException {
//        // 定义索引字段属性,其实这里就是组合json,可以参考curl 方式创建索引的json格式  此处blog 和执行时blog必须一致
//        XContentBuilder builder=XContentFactory.jsonBuilder()
//                .startObject()
//                .startObject("blog")
//                .startObject("properties")
//                .startObject("id").field("type", "integer").field("store", "yes").endObject()
//                .startObject("title").field("type", "string").field("store", "yes").endObject()
//                .startObject("content").field("type", "string").field("store", "yes").endObject()
//                .endObject()
//                .endObject()
//                .endObject();
//
//        PutMappingRequest mappingRequest = Requests.putMappingRequest("test").type("blog").source(builder);
//        client.admin().indices().putMapping(mappingRequest).actionGet();
//    }
//    
//    /**
//     * 创建数据
//     * */
//    public static void createData() throws IOException {
//            // 创建数据json 注意此ID是一个字段不是上面查询的id
//    		
//    		XContentBuilder builder=XContentFactory.jsonBuilder()
//                       .startObject()
//                       .field("id", "2")
//                       .field("title", "我是标题")
//                       .field("content", "我是内容")
//                       .endObject();
//        IndexResponse indexResponse = client.prepareIndex("test","blog").setSource(builder).execute().actionGet();
//        System.out.println(indexResponse.isCreated());
//    }
//
//}

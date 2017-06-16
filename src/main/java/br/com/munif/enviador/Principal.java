package br.com.munif.enviador;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;
import javax.activation.MimetypesFileTypeMap;
import org.apache.http.Header;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.apache.commons.io.IOUtils;

public class Principal {

    public static void main(String args[]) throws IOException, InterruptedException {
        
        ObjectMapper mapper=new ObjectMapper();
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        
        System.out.println("----> Enviando");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            File file = new File("/home/munif/gumga/security/pom.xml");
            //File file = new File("/home/munif/Pictures/DuqueDeCaxias2016.JPG");
            //DuqueDeCaxias2016.JPG
            String mimeType = mimeTypesMap.getContentType(file);
            System.out.println("---------------->"+mimeType);
            
            
            HttpPost httppost = new HttpPost("http://192.168.25.200:8084/storage-api/api/database");
            FileBody bin = new FileBody(file, ContentType.create(mimeType));

            StringBody comment = new StringBody("A binary file of some kind", ContentType.APPLICATION_XML);
            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("file", bin)
                    .addPart("comment", comment)
                    .build();

            httppost.setEntity(reqEntity);
            httppost.addHeader("gumgaToken", "eterno");

            System.out.println("executing request " + httppost.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httppost);
            Header[] allHeaders = response.getAllHeaders();
            for (Header h : allHeaders) {
                System.out.println(h.getName() + " ->" + h.getValue());
            }

            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    System.out.println("-->" + resEntity.toString());
                    InputStream is = resEntity.getContent();
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(is, writer, "UTF-8");
                    String results = writer.toString();
                    System.out.println("----->"+results); 
                    Map map = mapper.readValue(results, Map.class);
                    System.out.println("----->"+map);
                    System.out.println("----->"+map.get("publicUrl"));
                    

                    System.out.println("Response content length: " + resEntity.getContentLength());
                }
                EntityUtils.consume(resEntity);
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }

    }

}

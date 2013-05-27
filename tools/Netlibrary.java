package com.lynnrichter.tools;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;

/*
 * @auth lynnrichter
 * 
 * 在这个类里面主要处理三个函数：
 * 一个是使用httprequest 请求url地址页面的数据；
 * 一个是使用httprequest向指定url地址发送纯文本数据；
 * 一个是使用httprequest想指定url地址发送多媒体文件；
 * 
 */
public class Netlibrary {

	/**
	 * 获取url对应的服务器数据
	 * @param url 
	 * @return
	 */
	public final static  String GetUrlData(String url)
	{
		/*建立HttpGet 连接 */
		HttpGet  httpRequest =new HttpGet(url);
		String result="";
		
		try
		{
			/*发出Http请求*/
			HttpResponse httpResponse=new DefaultHttpClient().execute(httpRequest);
			
			/*若状态为200？ ok*/
			if(httpResponse.getStatusLine().getStatusCode()==200)
			{
				/*获取应答字符串即content？*/
				result=EntityUtils.toString(httpResponse.getEntity());
				
				/*删除冗余字符*/
//				result=result.replace("(\r\n|\r|\n|\n\r)","");
				
			}
			else
			{
				result="Error Response:"+httpResponse.getStatusLine().toString();
			}
		}
		catch(ClientProtocolException e)
		{
			result=e.getMessage();
			e.printStackTrace();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			result=e.getMessage();
			e.printStackTrace();
		}
		catch(Exception e)
		{
			result=e.getMessage();
			e.printStackTrace();
		}
		finally
		{

			
		}
		
		return result;
		
	}
	
	
	/**
	 * 
	 * @param url
	 * @param params 
	 * @return
	 * 
	 */
	public final static String PostData2Url(String url,List<NameValuePair> params) 
	{
		HttpPost  httpRequest=new HttpPost(url);
		String result="";
		try
		{
			httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			HttpResponse httpResponse=new DefaultHttpClient().execute(httpRequest);
			if(httpResponse.getStatusLine().getStatusCode()==200)
			{
				result=EntityUtils.toString(httpResponse.getEntity());
			}
			else
			{
				result="Error Response:"+httpResponse.getStatusLine().toString();
			}
		}
		catch(ClientProtocolException e)
		{
			result=e.getMessage();
			e.printStackTrace();
		}
		catch(IOException e)
		{
			result=e.getMessage();
			e.printStackTrace();
		}
		catch(Exception e)
		{
			result=e.getMessage();
			e.printStackTrace();
		}
		finally
		{
			
		}
		
		return result;
	}

	/**
	 * 
	 * @param <MultiPartEntity>
	 * @param url
	 * @param params
	 * @param files
	 * @return
	 * @throws Exception 
	 */
	public final static String PostMediaData(String url,List<NameValuePair> params ,byte[] buffer,String fileName) throws Exception
	{
		StringBuilder sbdata=null;
		String BOUNDARY=java.util.UUID.randomUUID().toString();
		String LINEND="\r\n";
		String PREFIX="--";
		String MULTIPART_FROM_DATA="";
		String CHARSET="UTF-8";
		
			
		URL uri=new URL(url);
		HttpURLConnection conn=(HttpURLConnection)uri.openConnection();
		conn.setReadTimeout(6*1000);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA+";boundary="+BOUNDARY);
		
		//首先拼组文本类型的参数
		StringBuilder sbtext=new StringBuilder();
		
		for(NameValuePair nvp :params)
		{
			sbtext.append(PREFIX);
			sbtext.append(BOUNDARY);
			sbtext.append(LINEND);
			sbtext.append("Content-Disposition:form-data;name=\""+nvp.getName()+"\""+LINEND);
			sbtext.append("Content-Type:text/plain;charset="+CHARSET+LINEND);
			sbtext.append("Content-Trasnfer-Encoding:8bit"+LINEND);
			sbtext.append(LINEND);
			sbtext.append(nvp.getValue());
			sbtext.append(LINEND);
			
		}
		DataOutputStream outStream=new DataOutputStream(conn.getOutputStream()); 
		outStream.write(sbtext.toString().getBytes());
		
		//发送文件数据
		StringBuilder sbfile=new StringBuilder();
		sbfile.append(PREFIX);
		sbfile.append(BOUNDARY);
		sbfile.append(LINEND);
		sbfile.append("Content-Disposition:form-data;name=\""+fileName+"\""+LINEND);
		sbfile.append("Content-Type:application/octet-stream;charset="+CHARSET+LINEND);
		sbfile.append("Content-Trasnfer-Encoding:8bit"+LINEND);
		sbfile.append(LINEND);
		outStream.write(sbfile.toString().getBytes());
		outStream.write(buffer);
		outStream.write(LINEND.getBytes());
		
		//请求结束标志
		byte[] end_data=(PREFIX+BOUNDARY+PREFIX+LINEND).getBytes();
		outStream.write(end_data);
		outStream.flush();
		
		//得到相应码
		int res=conn.getResponseCode();
		if(res==200)
		{
			InputStream in=conn.getInputStream();
			int ch;
			sbdata=new StringBuilder();
			while((ch=in.read())!=-1)
			{
				sbdata.append((char)ch);
			}
			System.out.println(sbdata.toString());
		}
		outStream.close();
		conn.disconnect();
		//解析服务器返回的数据；
		return sbdata.toString();
		
		
	}
	
	public final static byte[] GetFileFromAsset(Context ctx,String fileName)
	{
		byte[] buffer = null;
		try {
			InputStream in=ctx.getResources().getAssets().open(fileName);
			int length=in.available();
			buffer=new byte[length];
			in.read(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buffer;
	}
}

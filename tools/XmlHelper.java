package com.lynnrichter.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;


public final class XmlHelper {

	public static String ReadCityCode(Context context,String cityname)
	{
		String returnString="";
		DocumentBuilderFactory docBuilderFactory=null;
		DocumentBuilder docBuilder=null;
		Document doc=null;
		try
		{
			docBuilderFactory=DocumentBuilderFactory.newInstance();
			docBuilder=docBuilderFactory.newDocumentBuilder();
			doc=docBuilder.parse(context.getAssets().open("citys.xml"));
			
			Element root=doc.getDocumentElement();
			NodeList nodeList =root.getElementsByTagName("city");
			for(int i=0,len=nodeList.getLength();i<len;i++)
			{
				Element nd=(Element) nodeList.item(i);
				String temp=nd.getAttribute("name");
				if(temp.equals(cityname))
				{
					returnString=nd.getAttribute("code");
					break;
				}
				
				
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			returnString="";
		}
		return returnString;
		
		
	}
}

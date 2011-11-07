package org.destecs.core.simulationengine.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;


public class CustomLoggingUtils {

//	  public static void logRequest(Logger logger, 
//			  String pWriter) throws XmlRpcException {
//	    ByteArrayOutputStream bos = null;
//	    try {
//	      logger.debug("---- Request ----");
//	      bos = new ByteArrayOutputStream();
//	      requestEntity.writeRequest(bos);
//	      logger.debug(toPrettyXml(logger, bos.toString()));
//	    } catch (IOException e) {
//	      throw new XmlRpcException(e.getMessage(), e);
//	    } finally {
//	      IOUtils.closeQuietly(bos);
//	    }
//	  }

	  public static void logRequest(Logger logger,String clientUrl, String content) {
	    logger.debug("---- Request ----");
	    logger.debug("---- Client "+clientUrl+" ----");
	    logger.debug(toPrettyXml(logger, content));
	  }

	  public static String logResponse(Logger logger, String clientUrl, InputStream istream) 
	      throws XmlRpcException {
	    BufferedReader reader = null;
	    try {
	      reader = new BufferedReader(new InputStreamReader(istream));
	      String line = null;
	      StringBuilder respBuf = new StringBuilder();
	      while ((line = reader.readLine()) != null) {
	        respBuf.append(line);
	      }
	      String response = respBuf.toString();
	      logger.debug("---- Response ----");
	      logger.debug("---- Client "+clientUrl+" ----");
	      logger.debug(toPrettyXml(logger, respBuf.toString()));
	      return response;
	    } catch (IOException e) {
	      throw new XmlRpcException(e.getMessage(), e);
	    } finally {
	    	IOUtils.closeQuietly(reader);
	    }
	  }

//	  public static void logResponse(Logger logger, String clientUrl,String content) {
//	    logger.debug("---- Response ----");
//	    logger.debug(toPrettyXml(logger, content));
//	  }

	  public static String toPrettyXml(Logger logger, String xml) {
	    try {
//	      Transformer transformer = 
//	        TransformerFactory.newInstance().newTransformer();
//	      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//	      transformer.setOutputProperty(
//	        "{http://xml.apache.org/xslt}indent-amount", "2");
//	      StreamResult result = new StreamResult(new StringWriter());
//	      StreamSource source = new StreamSource(new StringReader(xml));
//	      transformer.transform(source, result);
//	      return result.getWriter().toString();
	    	return xml;
	    } catch (Exception e) {
//	      logger.warn("Can't parse XML");
	      return xml;
	    }
	  }
	}
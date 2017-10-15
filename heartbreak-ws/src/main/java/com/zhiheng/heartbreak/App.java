package com.zhiheng.heartbreak;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.WebResourceSet;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.EmptyResourceSet;
import org.apache.catalina.webresources.StandardRoot;

/**
 * App Start Endpoint!
 *
 */
public class App {
	
	private static File getRootFolder() {
		File root;
    	try {
			String runningJarPath = App.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replaceAll("\\\\", "/");
			System.out.println(runningJarPath);
			int lastIndexOf = runningJarPath.lastIndexOf("/target/");
			System.out.println(lastIndexOf);
			if(lastIndexOf < 0) {
				root = new File("");
			} else {
				root = new File(runningJarPath.substring(0, lastIndexOf));
			}
			System.out.println("application resolved root folder: " + root.getAbsolutePath());
			return root;
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
    public static void main(String[] args ) throws Exception {
    	File root = getRootFolder();
    	System.setProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE", "true");
    	Tomcat tomcat = new Tomcat();
    	Path tempPath = Files.createTempDirectory("tomcat-base-dir");
    	tomcat.setBaseDir(tempPath.toString());
    	
    	System.out.println("tomcat tempDir:  " + tempPath.toString());
    	
    	String webPort = System.getenv("PORT");
    	if(webPort == null || webPort.isEmpty()) {
    		webPort = "8080";
    	}
    	
    	tomcat.setPort(Integer.valueOf(webPort));
    	File webContentFolder = new File(root.getAbsolutePath(), "src/main/webapp/");
    	if(!webContentFolder.exists()) {
    		webContentFolder = Files.createTempDirectory("default-doc-base").toFile();
    	}
    	StandardContext ctx = (StandardContext) tomcat.addWebapp("", webContentFolder.getAbsolutePath());
    	ctx.setParentClassLoader(App.class.getClassLoader());
    	
    	System.out.println("configuring app with basedir: " + webContentFolder.getAbsolutePath());
    	
    	File additionWebInfClassesFolder = new File(root.getAbsolutePath(), "target/classes");
    	WebResourceRoot resources = new StandardRoot(ctx);
    	
    	WebResourceSet resourceSet;
    	if(additionWebInfClassesFolder.exists()) {
    		resourceSet = new DirResourceSet(resources, "/WEB-INF/classes", additionWebInfClassesFolder.getAbsolutePath(), "/");
    		System.out.println("loading WEB-INF resources from as '" + additionWebInfClassesFolder.getAbsolutePath() + "'");
    	} else {
    		resourceSet = new EmptyResourceSet(resources);
    	}
    	resources.addPreResources(resourceSet);
    	ctx.setResources(resources);
    	
    	tomcat.start();
    	tomcat.getServer().await();
    	
    	
    }
}

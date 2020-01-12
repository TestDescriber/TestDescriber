package nl.tudelft.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.nio.MappedByteBuffer;
import java.nio.charset.Charset;

/**
 * Set of utility routines
 * @author annibale.panichella
 *
 */
public class Utilities {
	
    public static class CPBuilder{
    	private final StringBuilder sb = new StringBuilder();
    	private final String seperator;
    	
    	public CPBuilder(){ this(":"); }
    	public CPBuilder(String seperator){
    		this.seperator = seperator;
    	}
    	

    	private CPBuilder append(String s){
    		if(sb.length() > 0)
    			sb.append(seperator);
    		sb.append(s);
    		return this;
    	}
    	
    	public CPBuilder and(String f){ return append(f); }
    	public CPBuilder and(File f){ return append(f.getAbsolutePath()); }
    	public CPBuilder and(Collection<File> lf){
    		for(File f : lf)
    			append(f.getAbsolutePath());
    		return this;
    	}
    	public CPBuilder andStrings(Collection<String> lf){
    		for(String f : lf)
    			append(f);
    		return this;
    	}
    	public String build(){ return sb.toString(); }
    }

    public static String readFile(File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            return Charset.defaultCharset().decode(bb).toString();
        } finally {
            stream.close();
        }
    }

	
	public static void cleanDirectory(File dir) throws IOException{
		if(dir.exists())
			delete(dir);

		System.out.println("Creating directory " + dir);
		if (!dir.mkdir())
			throw new IOException("Could not create directory: "+dir);
	}

	private static void delete(File f) throws IOException {
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				delete(c);
		}
		System.out.println("Deleting file or directory " + f);
		if (!f.delete())
			throw new IOException("Failed to delete file: " + f);
	}
	
    @SuppressWarnings("resource")
	public static void CopyToDirectory(File fileOrDirectory, File destDir, String targetName) throws IOException{
    	System.out.println("Copying '" + fileOrDirectory + "' to '" + destDir + "'");

        if(targetName == null)
            targetName = fileOrDirectory.getName();

        if(!destDir.exists()){
            if(!destDir.mkdirs())
                throw new IOException("Unable to create directory " + destDir.getAbsolutePath());
        }

        if(fileOrDirectory.isFile()){
            File destFile = new File(destDir.getAbsolutePath() + File.separator + targetName);
            if(!destFile.exists()){
                if(!destFile.createNewFile())
                    throw new IOException("Unable to create file " + destFile.getAbsolutePath());
            }

            FileChannel source = null;
            FileChannel destination = null;

            try {
                source = new FileInputStream(fileOrDirectory).getChannel();
                destination = new FileOutputStream(destFile).getChannel();
                destination.transferFrom(source, 0, source.size());
            }finally {
                if(source != null)
                    source.close();
                if(destination != null)
                    destination.close();
            }
        }else if(fileOrDirectory.isDirectory()){
            File copyDir = new File(destDir.getAbsolutePath() + File.separator + targetName);

            if(!copyDir.exists()){
                if(!copyDir.mkdir())
                    throw new IOException("Unable to create directory " + copyDir.getAbsolutePath());
            }

            File[] files = fileOrDirectory.listFiles();
            if(files != null) {
                for(File f: files)
                    CopyToDirectory(f, copyDir, null);
            }
        }
    }
        
    public static void pause(double time){
        try{
        	Thread.sleep((int) (time * 1000));
        }catch(Throwable t){}
    }   
    
    public static URL[] createURLs(String cp) throws MalformedURLException{

		LinkedList<String> required_libraries = new LinkedList<String>();
		String join_symbol = ":"; 

		if(System.getProperty("os.name").startsWith("Windows")) {
			join_symbol = "&"; 
		}
		
		String[] libraries = cp.split(join_symbol);
		for (String s : libraries){
			s = s.replace(join_symbol, "");
			if (s.length() > 0)
				required_libraries.addLast(s);
		}

		URL[] url = new URL[required_libraries.size()];

		for (int index = 0; index < required_libraries.size(); index++){
			if (required_libraries.get(index).endsWith(".jar")) {
				System.out.println("createURLs with jars");//GGG del
				url[index] = new URL("jar:file:" + required_libraries.get(index)+"!/");
				//TODO GGG what about this ":"???
			} else {
				System.out.println("createURLs no jars");//GGG del
				url[index] = new File(required_libraries.get(index)).toURI().toURL();
			}
		}

		//GGG was commented out
		for (URL u : url){
			System.out.println("createURLs "+u.getFile());
			//Main.debug("url "+u.getFile());
		}
		return url;

	}

	public static List<File> getCompiledFileList(File directory){
		List<File> list = new ArrayList<File>();
		if (directory.isDirectory()){
			for (File f : directory.listFiles()){
					list.addAll(getCompiledFileList(f));
			}
		}  else {
			if (directory.getName().endsWith(".class"))
				list.add(directory);
		}
		
		return list;
	}
 }

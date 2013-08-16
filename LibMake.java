import java.io.File;
import java.io.StringReader;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Properties;

/**
* This class attempts to compile your library from SIMPLICITY-J template.
* 
*/
class LibMake
{
		private static final int C_DEFAULT_TEXT_LINE_WIDTH = 80; // we assume screen is 80chars wide
		private static final int C_DEF_OFFSET = 8;
		
		/**
		* Attempts to make library.
		*/
		public void make() throws WinMakeEx
		{
		     int lineOffset = C_DEF_OFFSET;
			 linePrint("Compiling library" ,  lineOffset, '-');
		     compileClasses(lineOffset + C_DEF_OFFSET);
			 dprintln("");
			 
			 linePrint("Putting library into JAR" ,  lineOffset , '-');
			 packClassesIntoJAR(lineOffset + C_DEF_OFFSET);
			 dprintln("");
			 
			 linePrint("Exporting library into sketchbook folder" ,  lineOffset , '-');
			 putLibraryIntoSketchbookFolder(lineOffset + C_DEF_OFFSET);
			 dprintln("");

			
		}

		/**
		*
		*/
		public static void main(String[] args){
				linePrint("Starting building library from Simplicity template", 0, '=');
				try
				{
				   new  WinMake().make();
				}
				catch(WinMakeEx ex){
				   dprintln("-------------------------------------------------------");
				   dprintln("******Error occured during making: " + ex.getMessage());
				   ex.printStackTrace();
				}
					  
		}			 
		
		
		/**
		* Prints lines
		*/
	    private static void linePrint(String msg, int offset, char fillChar){
			simpleLine(fillChar, offset, C_DEFAULT_TEXT_LINE_WIDTH);
			dprintln(msg, offset);
			simpleLine(fillChar, offset, C_DEFAULT_TEXT_LINE_WIDTH);
		}
		
		/**
		* Just draws line full of fillChar
		*/
		private static void simpleLine(char fillChar, int offset, int maxLineLength){
		    int fillCharsCount = maxLineLength - offset;
			
		    if ( fillCharsCount < 1 ){
			   dprintln("*** warning in simpleLine() **** fillCharCount is less than 1 that means depth is too large ***");
			   return;
			   // we kinda should offset to far?
			}
			
			// normal execution
			// calculate how many chars to print
			StringBuilder sb = new StringBuilder();
			// add whitespace in the beginnign
			for(int i = 0 ; i < offset ; i++){
			  sb.append(' ');
			}
			
			
			for(int j = 0 ; j < fillCharsCount ;j++){
			   sb.append(fillChar);
			}
			
			dprintln(sb.toString());
			
		}
		
		/**
		* Attempts to compile classes in the subdirectory structure.
		* If compiler results in error, then prints out stderr to console.
		* And throws WinMakeEx exception.
		*/
		void compileClasses(int lineOffset) throws WinMakeEx
		{
				    Process p;
					String cmd = "../java/bin/javac -d bin -target 1.6 -source 1.6 -sourcepath src -cp ../core/library/core.jar src/simplicity/*.java  -bootclasspath ../java/lib/rt.jar";
					try
					{
					 p = Runtime.getRuntime().exec(cmd);
					  int rez = p.waitFor();

					  dprintln("Exit result returned: " + rez, lineOffset);
					  
					  if ( rez != 0 ){  // error
					     // output error buffer
						 printStream(p.getErrorStream(), lineOffset);
						 throw new WinMakeEx("Compile failed with code: " + rez);
					  }
					  
					  printStream(p.getInputStream(), lineOffset);
					  // else success
					 

					}
					catch(IOException e){
					  e.printStackTrace();
					}		
					catch(InterruptedException e){
					  e.printStackTrace();
					}
		 
		}
		
		private static void printStream(InputStream is, int lineOffset){
		    try{
				  BufferedReader reader = 
					 new BufferedReader(new InputStreamReader( is));
					 
				  
				  String line = reader.readLine();
				  while (line != null) {
					dprintln(line, lineOffset);
					line = reader.readLine();
				  }		
					 
		    }
			catch(IOException ioex){
				ioex.printStackTrace();
			}
		}

		
		static void packClassesIntoJAR(int outputPaddingOffset) throws WinMakeEx
		{
					Process p;
					File newWorkingDirectory = dirRelativeToCurrent("bin");
					File jarExePath = new File("../java/bin/jar");
					String jarExe = null;
					try{
					 jarExe = jarExePath.getCanonicalPath();
					}
					catch(IOException ioex){
					   // this is kinda fatal if we can't get canonical path.
					   throw new RuntimeException(ioex);
					}
					dprintln("Jar exe is: [" + jarExe + "]", outputPaddingOffset);
					
					
					//String jarPath = "c:\\Users\\Ernesto Guevara\\Desktop\\ProcessingBin\\processing-2.0.1\\java\\bin\\jar.exe";
					//String cmd = jarPath + "  " + " cfv ../dist/simplicity/library/simplicity.jar  * ";
					//String cmd = "jar cfv ../dist/simplicity/library/simplicity.jar  * ";
					String cmd = jarExe +  " cfv ../dist/simplicity/library/simplicity.jar  * ";
					String[] cmdArray = { jarExe, "cfv", "../dist/simplicity/library/simplicity.jar", "*" };
					try
					{
					 //p = Runtime.getRuntime().exec(cmd, null, newWorkingDirectory);
					 p = Runtime.getRuntime().exec(cmdArray, null, newWorkingDirectory);
					  int rez = p.waitFor();

					  dprintln("Exit result returned: " + rez, outputPaddingOffset);
					  
					  if ( rez != 0 ){  // error
					     // output error buffer
						 printStream(p.getErrorStream(), outputPaddingOffset);
						 throw new WinMakeEx("JARring failed with code: " + rez);
					  }
					  
					  printStream(p.getInputStream(), outputPaddingOffset);
					  // else success
					 

					}
					catch(IOException e){
					  e.printStackTrace();
					}		
					catch(InterruptedException e){
					  e.printStackTrace();
					}	
					
		}
		
		
		private static File  dirRelativeToCurrent(String dirName){
		  File cwd = new File( System.getProperty("user.dir") );
		  File newDir =  new File(cwd, dirName); // exception? no ?
		  return newDir;
		}
		
		
		/**
		* Copies library (library directory structure) into the sketchbook folder.
		*
		*/
		static void putLibraryIntoSketchbookFolder(int outputOffset){
		     File sketchBookFolder = getSketchBookFolderPath();
			 File libraryFolder = new File(sketchBookFolder, "libraries");
			 File distFolder = new File("dist");
			 copyDirectoryContentsFrom(distFolder, libraryFolder, outputOffset);
		}
		
		
		
		/**
		* Returns sketchbook folder path.
		* Tries reading preferences.txt and fetch sketchbook path from there.
		* ?? or throws something? just throws RuntimeException on failure. Let's fail fast.
		*/
		private static File getSketchBookFolderPath(){
			String preferencesTxtPath = getPreferencesTxtPath();
			String pathFromPreferences = null;
			//Reading properties file in Java example
			  try{
						Properties props = new Properties();
						
						String propertyFileContents = readPropertyFileContents(preferencesTxtPath);

						props.load(new StringReader(propertyFileContents.replace("\\","\\\\")));						

						//loading properites from properties file						
//						FileInputStream fis = new FileInputStream(preferencesTxtPath);
//						props.load(fis);
						
						pathFromPreferences =	props.getProperty("sketchbook.path");			
				}
				catch(IOException ioex){
				  // some kind of error. fatal
				   throw new RuntimeException(ioex); 
				}
			
			dprintln("Received path from preferences[" + pathFromPreferences + "]");
			if ( pathFromPreferences == null ){
			   throw new RuntimeException("Fatal error: cannot find sketchbook path in preferences.txt");
			}
			
			return new File(pathFromPreferences);
		}
		
		
		/**
		* Returns path to prefernces txt or NULL .
		* Under the hood for Windows:
			fetches environment variable %APPROAMING%? or whatever it's called.
		*/
		private static String getPreferencesTxtPath(){
		    String APPDATA_DIR = System.getenv("APPDATA");
			if ( APPDATA_DIR == null ){
			   throw new RuntimeException("Error cannot get APPDATA environment variable. This script so far works on Windows." + 
							" for support on linux machine, modify getPreferencesTxtPath() so that it returns path to " + 
							" preferences.txt on Linux");
			}
			return APPDATA_DIR + File.separator + "Processing" +  File.separator + "preferences.txt";
		}
		
		
		/**
		* Copies contents of the directory srcDirWithContents into destDirWithContents.
		* Assumes that all directories exist. 
		* Will create file strcuture beneath destDirWithContents.
		* Will overwrite files beneath destDirWithContents.
		*/
		private static void copyDirectoryContentsFrom(File srcDirWithContents, File destDirWithContents, int outputOffset)
		{
			dprintln("Not implemented ... " , outputOffset);
		    String msg = String.format("Simulating copying contents of directory [%s] to directory [%s]", 
											srcDirWithContents.getAbsolutePath(),
											destDirWithContents
									   );
									
		    dprintln(msg, outputOffset);
		}
		
		private static void dprintln(String msg){
		   System.out.println(msg);
		}
		
		
		private static void dprintln(String msg, int offset){
			String paddedMsg  = getPaddedString(msg, offset, ' ');
			dprintln(paddedMsg);
		}
		
		/**
		* Just pads string with character.
		*/
		private static String getPaddedString(String msg, int offset, char paddingChar){
			if ( offset < 1 ){
			   return msg;
			}
			
			StringBuilder sb = new StringBuilder();
			for(int i = 0 ; i < offset ; i++){
			  sb.append(paddingChar);
			}
			sb.append(msg);
			return sb.toString();
		}
		
		static File getSketchBookFolder(){
		    return null;
		}
		
		
		/**
		* Our local exception
		*/
		static class WinMakeEx extends Exception
		{
		    WinMakeEx(String msg){
				super(msg);
			}
		}
		
		/**
		* Reads contents of the file into string
		*
		*/
		private static String readPropertyFileContents(String filePath) throws IOException
		{
					StringBuilder sb = new StringBuilder();
				
					// Open the file
					FileInputStream fstream = new FileInputStream(filePath);
					BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

					String strLine;

					//Read File Line By Line
					while ((strLine = br.readLine()) != null)   {
					  // Print the content on the console
					  //System.out.println (strLine);
					  sb.append(strLine);
					  sb.append("\n");
					}

					//Close the input stream
					br.close();
					
					return sb.toString();
		}
  
}
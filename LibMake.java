import java.nio.channels.FileChannel;
import java.io.File;
import java.io.StringReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.FileFilter;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
* This class attempts to compile your library from SIMPLICITY-J template.
* 
*/
class LibMake
{
		private static final int C_DEFAULT_TEXT_LINE_WIDTH = 80; // we assume screen is 80chars wide
		private static final int C_DEF_OFFSET = 8;


                /**
                 * Attempts to execute command, and make sure that full stream buffers do not stall
                 * the program execution.
                 * @param cmdArray stringar, command to execute
                 * @param environment stringar, can be null
                 * @param newWorkingDirectory working directory from which command will be executed
                 * @param iConLogger use this interface instead of sysout. As it will conserve the padding.
                 * @return 
                 */
                private static int safeExec(String[] cmdArray, String[] environment, File newWorkingDirectory, IConLogger iConLogger) throws IOException, InterruptedException {
//					 Process p = Runtime.getRuntime().exec(cmd, null, newWorkingDirectory);
					 Process p = Runtime.getRuntime().exec(cmdArray, null, newWorkingDirectory);
                                         // set up gobblers and start them
                                         StreamGobbler errGobbler = new StreamGobbler(p.getErrorStream(), "ERR", iConLogger);
                                         
                                         StreamGobbler inputGobbler = new StreamGobbler(p.getInputStream(), "OUT", iConLogger); // here
                                                                                                                    // we think from perspective of java
                                                                                                                    // so what for jave is InputStream
                                                                                                                    // is actually output-steam of the program
                                         errGobbler.start();
                                         inputGobbler.start();
                                      
					 
                                         int rez = p.waitFor(); 
                                         return rez;
                }
		
		/**
		* Attempts to make library.
		*/
		public void make() throws LibMakeEx
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
				   new  LibMake().make();
				}
				catch(LibMakeEx ex){
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
		* And throws LibMakeEx exception.
		*/
		void compileClasses(final int lineOffset) throws LibMakeEx
		{
				String wkd = System.getProperty("user.dir");
				dprintln("Working Directory: " + wkd, lineOffset);
                                dprintln("Compiling...", lineOffset);
                                String srcDir = wkd + File.separator  + "src"; 
				    Process p;
					//String cmd = "../java/bin/javac -verbose -d bin -target 1.6 -source 1.6 -sourcepath src -cp ../core/library/core.jar src/simplicity/*.java  -bootclasspath ../java/lib/rt.jar";
                                        ArrayList<String> cmdArlis = new ArrayList<String>();
					String[] arcmd = {
                                                            getJavacPath(),
							   // "-verbose",  // with -verbose on when running on Windows it never returns
							   "-d", "bin",
							   "-target" , "1.6",
							   "-source", "1.6",
							   "-sourcepath", srcDir ,  // TODO: should this be in dquotes? What if full path has spaces?
							   "-cp", "../core/library/core.jar",
							   "-bootclasspath",  "../java/lib/rt.jar"
							};
                                        Collections.addAll(cmdArlis, arcmd); // hitorically we were setting up parameters as string array
                                                                            // so it seems convenient to keep it like that
                                                        
                                        
                                        addFileListToArlis(cmdArlis, srcDir); // searches directory for java files
                                                                              // and adds their full path to arlis
                                        String[] newCmdArray  = new String[cmdArlis.size()];
                                        newCmdArray = cmdArlis.toArray(newCmdArray);
                                        //getFileListAsString(srcDir)
					try
					{
					 // p = Runtime.getRuntime().exec(cmd);
//					 p = Runtime.getRuntime().exec(arcmd);
                                         
                                         int rez = safeExec(newCmdArray, null, null, new IConLogger() {
                                             @Override
                                             public void conLogLn(String s) {
                                                 dprintln( s, lineOffset);
//                                                 dprintln("^[[5;34;42m" + s, lineOffset);
//                                                 dprintln("\033[22;31m " + s, lineOffset);  // looks like ansy.sys version. prob works in linux
                                             }
                                         });
                                         //emptyErrorStream(p, lineOffset); // this is kinda dirty hack
//					  int rez = p.waitFor();

					  if ( rez != 0 ){  // error
					     // output error buffer
						// printStream(p.getErrorStream(), lineOffset);
						 throw new LibMakeEx("Compile failed with code: " + rez);
					  }
					  dprintln("Compilation of library was successful.",  lineOffset);
                                          dprintln("Compiler exit code (" + rez + ")", lineOffset);
//					  printStream(p.getInputStream(), lineOffset);
					  // else success

					}
					catch(IOException ex){
					  ex.printStackTrace();
                                          throw new LibMakeEx(ex);
                                                  
					}		
					catch(InterruptedException e){
					  e.printStackTrace();
                                          throw new LibMakeEx(e);
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

		
		static void packClassesIntoJAR(final int outputPaddingOffset) throws LibMakeEx
		{
					Process p;
					File newWorkingDirectory = dirRelativeToCurrent("bin");
					File jarExePath = new File("../java/bin/jar");
					String jarExe = null;
					try{
					 jarExe = jarExePath.getCanonicalPath(); // how the hell this canonical "works" if the 
                                                                                  // file doesn't really exist
					}
					catch(IOException ioex){
					   // this is kinda fatal if we can't get canonical path.
					   throw new RuntimeException(ioex);
					}
					dprintln("JAR executable is: [" + jarExe + "]", outputPaddingOffset);
					
					
					//String jarPath = "c:\\Users\\Ernesto Guevara\\Desktop\\ProcessingBin\\processing-2.0.1\\java\\bin\\jar.exe";
					//String cmd = jarPath + "  " + " cfv ../dist/simplicity/library/simplicity.jar  * ";
					//String cmd = "jar cfv ../dist/simplicity/library/simplicity.jar  * ";
					//String cmd = jarExe +  " cfv ../dist/simplicity/library/simplicity.jar  * ";
					String[] cmdArray = { jarExe, "cfv", "../dist/simplicity/library/simplicity.jar", "*" };
					try
					{
					 //p = Runtime.getRuntime().exec(cmd, null, newWorkingDirectory);
//					 p = Runtime.getRuntime().exec(cmdArray, null, newWorkingDirectory);
//					  int rez = p.waitFor();
                                          
                                          int rez = safeExec(cmdArray, null, newWorkingDirectory, new IConLogger() { 
                                               public void conLogLn(String msg){
                                                   dprintln(msg, outputPaddingOffset);
                                               }
                                          });
                                          
					  dprintln("Exit result returned: " + rez, outputPaddingOffset);
					  
					  if ( rez != 0 ){  // error
					     // output error buffer
						 //printStream(p.getErrorStream(), outputPaddingOffset);
						 throw new LibMakeEx("JARring failed with code: " + rez);
					  }
					  
					 // printStream(p.getInputStream(), outputPaddingOffset);
					  // else success
					 

					}
					catch(IOException e){
					  e.printStackTrace();
                                          throw new LibMakeEx(e);
					}		
					catch(InterruptedException e){
					  e.printStackTrace();
                                          throw new LibMakeEx(e);
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
		static void putLibraryIntoSketchbookFolder(int outputOffset) throws LibMakeEx
		{
			try{
		     File sketchBookFolder = getSketchBookFolderPath();
			 File libraryFolder = new File(sketchBookFolder, "libraries");
			 File distFolder = new File("dist");
			 copyDirectoryContentsFrom(distFolder, libraryFolder, outputOffset); // this one throws IOException
			}
			catch(IOException ioex){
				throw new LibMakeEx(ioex);
			}
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
			
			//dprintln("Received path from preferences[" + pathFromPreferences + "]");
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
                    int os = OS.getOS();
                    switch ( os ){
                        case OS.LINUX:
                                return getPreferencesTxtPathFromLinux();
                        case OS.WINDOWS:
                                return getPreferencesTxtPathFromWindows();
                        case OS.MACOS:
                                throw new RuntimeException("getPreferencesTxtPath(): this only works on Linux/Windows currently.");
                    }
                    throw new RuntimeException("We shouldn't have got to this line");
		}

                /**
                 * Fetches filepath for preferences.txt on linux.
                 * (From user dir from subdirectory .processing
                 */
                private static String getPreferencesTxtPathFromLinux(){
		    String USER_DIR = System.getProperty("user.home");
			if ( USER_DIR == null ){
			   throw new RuntimeException("Error cannot get 'user.home' property. Are you sure you're running on Linux?");
			}
			return USER_DIR + File.separator + ".processing" +  File.separator + "preferences.txt";                    
                }                
                
                /**
                 * Fetches preferences txt file on Windows. 
                 * (From APPDATA environment variable)
                 */
                private static String getPreferencesTxtPathFromWindows(){
		    String APPDATA_DIR = System.getenv("APPDATA");
			if ( APPDATA_DIR == null ){
			   throw new RuntimeException("Error cannot get APPDATA environment variable are you running on Windows?");
			}
			return APPDATA_DIR + File.separator + "Processing" +  File.separator + "preferences.txt";                    
                }
		
		//*******************************************************************************************
		//*******************************************************************************************
		//***************** copying directories *****************************************************
		//*******************************************************************************************		
		//*******************************************************************************************
		/**
		* Copies contents of the directory srcDirWithContents into destDirWithContents.
		* Assumes that all directories exist. 
		* Will create file strcuture beneath destDirWithContents.
		* Will overwrite files beneath destDirWithContents.
		*/
		private static void copyDirectoryContentsFrom(File srcDirWithContents, File destDirWithContents, int outputOffset) throws IOException
		{
			
		    String msg = String.format("Exporting library from [%s] to directory [%s]", 
											srcDirWithContents.getAbsolutePath(),
											destDirWithContents
									   );
									
		    dprintln(msg, outputOffset);
			
			copyFolder(new File(srcDirWithContents, "simplicity"), destDirWithContents, outputOffset);
			
		}
		

		/**
		* TODO: this function should actually be properly tested. I just write it off top of my head,
		*  but it MAY bring surprises.
		* Copies file/directory into folder specified by 2nd parameter.
		* Partially tfaken from {@link http://www.mkyong.com/java/how-to-copy-directory-in-java/}
		* @param src file or directory to be copied.
		* @param dest EXISTING DIRECTORY (can't be file)  where the file/folder should be copied
		* @throws IOException in case there's error copying.
		* @throws IllegalArgumentException if the second parameter is NOT a directory
		*/
		public static void copyFolder(File src, File dest, int outputOffset) throws IOException{
	 
			if ( !dest.isDirectory() ){
				throw new IllegalArgumentException("copyFolder():: dest parameter MUST be directory, now pointing to : " + dest.getAbsolutePath());
			}
			
			if(src.isDirectory()){  // copying DIRECTORY to DIRECTORY
				// **** create "target directory" the directory with same name as src-directory but inside the destination

				File target = new File(dest, src.getName() );
				if(!target.exists()){
				   target.mkdir();
				   dprintln("Made directory: ["  + target.getAbsolutePath() + "]" );
				}
				
				// iterate over files (except but "." and ".." ) 
				// and copy them. via myself
	 
				//list all the directory contents
				String files[] = src.list();
	 
				for (String file : files) {
				   //construct the src and dest file structure
				   if ( 
							file.equals(".") ||
							file.equals("..") 
						){
							continue; // skip them files.
						}
						
				   File srcFile = new File(src, file);
				   //recursive copy
				   copyFolder(srcFile,target, outputOffset);
				}
	 
			}else{ // copying FILE to DIRECTORY
				copyFile(src, new File(dest, src.getName() ) , outputOffset);

			}
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

                

                /**
                 * Just returns path to javac (or javac.exe) relative to current path.
                 * OS dependent.
                 */
                private static String getJavacPath() {
                    if ( OS.getOS() == OS.LINUX ){
                        return "../java/bin/javac";
                    }
                    else if ( OS.getOS() == OS.WINDOWS ){
                        return "../java/bin/javac.exe";
                    }
                    throw new RuntimeException("only work with Linux and Windows");
                }

                
                /**
                 *  Gets listing of all files in the directory and it's subfolders,
                 * src/somefile.java
                 * src/simplicity/BouncingBall.java
                 * src/simplicity/BouncingBall1.java
                 * src/simplicity/BouncingBall2.java
                 * and returns the list as string 
                 * "src/somefile.java src/simplicity/BouncingBall.java AND_SO_ON"
                 * 
                 * We asssume that neither "src" dir neither subdirectories nor files
                 * have spaces in their names
                 */
                static String getFileListAsString(String srcDir) {
                    ArrayList<String> filePathes = new ArrayList<String>();
                    File root = new File(srcDir);
                    traverse(root, filePathes);
                    return arlisToString(filePathes, ' ');
                }
                
                static void addFileListToArlis(ArrayList<String> arlis, String srcDir){
                    //ArrayList<String> filePathes = new ArrayList<String>();
                    File root = new File(srcDir);
                    traverse(root, arlis); // traverses and adds each filepath to the arlis.
                }
                
                public static void traverse(File dir, ArrayList<String> fileCollection){
                    File[] files;
                    File[] dirs;
                    // get directory contents
                    files = dir.listFiles(C_FILE_FILTER);
                    dirs = dir.listFiles(C_DIR_FILTER);
                    
                    // files: add to list
                    for(File f: files){
                        fileCollection.add(f.getPath());
                    }
                    // directories: traverse
                    for(File d: dirs ){
                        traverse(d, fileCollection);
                    }
                }

                /**
                 * Concatenates arlis<String> into string.
                 * Also puts stirng in quotes if it has space in it.
                 * this is to specify pathes which have spaces in them. Dunno if it will work on linux...
                 * @return concatenated (with space) String.
                 */
                static String arlisToString(ArrayList<String> arlis, char separChar){
                     StringBuilder sb = new StringBuilder();
                     for(int i = 0 ; i < arlis.size() ; i++){
                         String s = arlis.get(i);
                         if ( i != 0 ){
                              sb.append(separChar);
                         }
                         
                         if ( s.contains(" ") ){
                               // adds double quotes on both sides of the line
                              
                                sb.append('"');
                                sb.append(s);
                                sb.append('"');
                         }
                         else{
                                sb.append(s);    
                         }
                                 
                         
                     }
                     return sb.toString();
                }

                /**
                 * This is just helper temporary solution, which 
                 * empties err stream  of the process (in our case javac)
                 * so that it doesn't hang the execution.
                 * This is bad hack as per this tutorial:
                 * {@link http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html?page=3}
                 */
                private static void emptyErrorStream(Process proc, int lineOffset) {
                    try {
                        InputStream stderr = proc.getErrorStream();
                        InputStreamReader isr = new InputStreamReader(stderr);
                        BufferedReader br = new BufferedReader(isr);
                        String line = null;
                        dprintln("<ERROR>", lineOffset);
                        while ( (line = br.readLine()) != null)
                            dprintln(line, lineOffset);
                        dprintln("</ERROR>", lineOffset);
                        
                    } catch (IOException ex) {
                        Logger.getLogger(LibMake.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
	
		/**
		* Our local exception
		*/
		static class LibMakeEx extends Exception
		{
			LibMakeEx(Exception ex){
				super(ex);
			}
		
		
		    LibMakeEx(String msg){
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
		

		/**
		* Just copies regular file using Java NIO (1.6jdk compatible);
		*/
		public static void copyFile(File sourceFile, File destFile, int outOffset) throws IOException {
			String msg = String.format("Copying file [%s] to [%s]", sourceFile.getName(), destFile.getName() );
			dprintln(msg, outOffset);
			if(!destFile.exists()) {
				destFile.createNewFile();
			}

			FileChannel source = null;
			FileChannel destination = null;

			try {
				source = new FileInputStream(sourceFile).getChannel();
				destination = new FileOutputStream(destFile).getChannel();
				destination.transferFrom(source, 0, source.size());
			}
			finally {
				if(source != null) {
					source.close();
				}
				if(destination != null) {
					destination.close();
				}
			}
		}	
                

                /**
                 * Just helper class providing static methods for detecting
                 * current OS.
                 */
                static class OS
                {
                     public static final int UNINITIALIZED = 0;
                     public static final int LINUX = 1;
                     public static final int WINDOWS = 2;
                     public static final int MACOS = 3;
                     
                     private static int smCurrentOs = UNINITIALIZED;
                     
                     public static int getOS(){
                          if ( smCurrentOs == UNINITIALIZED ){
                              detectOS();
                          }
                          return smCurrentOs;
                     }
                     
                     private static void detectOS(){
                          String osName = System.getProperty("os.name");
                          if ( osName.toLowerCase().contains("windows") ){
                              smCurrentOs = WINDOWS;
                              return;
                          }
                          
                          if ( osName.toLowerCase().contains("mac")){
                              smCurrentOs = MACOS;
                              return;
                          }
                          
                          if ( osName.toLowerCase().contains("linux") ){
                              smCurrentOs = LINUX;
                              return;
                          }
                          
                          System.out.println("Your os name is: [" + osName + "], doesn't seem to be windows or macos, we assume it's some type of Linux");
                          smCurrentOs = LINUX;
                          
                     }
                     
                }// class OS
                

                /**
                 * Checks and accepts *.java or *.JAVA files.
                 */
                private final static FileFilter C_FILE_FILTER = new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                               if ( pathname.isDirectory()){
                                   return false;
                               }
                               
                               return pathname.getName().toLowerCase().endsWith(".java");
                               //                        ^^^^^^^^^^^
                               //                keep in mind that we are using case insensitive
                               //                   verification here.
                    }
                };
                
                /**
                 * Returns true if it is directory (excluding "." and ".." dirs");
                 */
                private final static FileFilter C_DIR_FILTER = new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        String name = pathname.getName();
                        if ( name.equals(".") || name.equals("..") ){
                            return false;
                        }
                        return pathname.isDirectory();
                    }
                };
                        

        /**
         * This is helper class which "consumes" out and err streams
         * supplied by the process.
         * As per article
         * {@link http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html?page=4}
         */ 
        static class StreamGobbler extends Thread
        {
            InputStream is;
            String type;
            OutputStream os;
            
            /**
             * Through this interface we output strings.
             */
            IConLogger mConLogger;

            StreamGobbler(InputStream is, String type)
            {
                this.is = is;
                this.type = type;
                this.os = null;
                mConLogger = null;
            }
            
            StreamGobbler(InputStream is, String type, IConLogger conLogger){
                 this.is = is;
                this.type = type;
                this.os = null;
                mConLogger = conLogger;
            }
            
            StreamGobbler(InputStream is, String type, OutputStream redirect)
            {
                this.is = is;
                this.type = type;
                this.os = redirect;
            }

            public void run()
            {
                try
                {
                    PrintWriter pw = null;
                    if (os != null)
                        pw = new PrintWriter(os);

                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line=null;
                    while ( (line = br.readLine()) != null)
                    {
                        if (pw != null){
                            pw.println(line);
                        }
                     
                        if ( mConLogger != null){
                             mConLogger.conLogLn(line);
                        }
                        
                        //System.out.println(type + ">" + line);    
                    }
                    
                    if (pw != null){
                        pw.flush();
                    }
                } catch (IOException ioe)
                    {
                    ioe.printStackTrace();  
                    }
            }
        }//class StreamGobbler                
                
        
        
        interface IConLogger
        {
             void conLogLn(String s);
        }
  
}// class LibMake

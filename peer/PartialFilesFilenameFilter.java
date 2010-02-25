package peer;

import java.io.*;

public class PartialFilesFilenameFilter
	implements FilenameFilter
{
	private String baseFilename;
	
public PartialFilesFilenameFilter(String nameOfFullFile)
{
	baseFilename = nameOfFullFile;
}

public boolean accept(File parentDirectory, String filename)
{
	// Check if this filename begins with the base file name
	if (filename.startsWith(baseFilename))
	{
		// Construct a regex that matches when the filename ends with period-delimited start and end block indexes
		String regex = "\\A\\.\\d+\\.\\d+\\Z";
		
		return filename.substring(baseFilename.length()).matches(regex);
	}
	
	return false;
}

}

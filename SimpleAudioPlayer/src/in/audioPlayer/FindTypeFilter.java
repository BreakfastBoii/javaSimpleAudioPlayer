package in.audioPlayer;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class FindTypeFilter extends FileFilter {

	 	String extension = "";
	    String desc = "";
	    
	public FindTypeFilter(String _extension, String _desc) {
		extension = _extension;
		desc = _desc;
	}
	
	 @Override
	    public boolean accept(File _file) {
	        if (_file.isDirectory()) {
	            return true;
	        }
	        return _file.getName().toLowerCase().endsWith(extension);
	    }
	     
	    public String getDescription() {
	        return desc + String.format(" (*%s)", extension);
	    }
}

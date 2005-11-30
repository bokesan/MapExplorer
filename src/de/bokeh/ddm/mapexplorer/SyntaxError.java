package de.bokeh.ddm.mapexplorer;

public class SyntaxError extends Exception {

    private final String file;
    private final int line;
    private final String message;
    
    public SyntaxError(String file, int line, String message) {
	this.file = file;
	this.line = line;
	this.message = message;
    }

    /**
     * @return Returns the file.
     */
    public String getFile() {
        return file;
    }

    /**
     * @return Returns the line.
     */
    public int getLine() {
        return line;
    }

    /**
     * @return Returns the message.
     */
    public String getMessage() {
        return message;
    }
    
    
    
    
    
}

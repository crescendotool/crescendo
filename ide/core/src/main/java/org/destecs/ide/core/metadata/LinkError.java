package org.destecs.ide.core.metadata;

public class LinkError {
	
	private int line = -1;
	private String reason = null;
	
	public LinkError(int line, String reason) {
		this.line = line;
		this.reason = reason;
	}
	
	public int getLine() {
		return line;
	}
	
	public String getReason() {
		return reason;
	}
	
	@Override
	public String toString() {
		return "Error - line: " + line + " reason: " + reason;
	}
}

package com.trandonsystems.britebin.model;

public class UnitMessage {

	public int unitId;
	public String serialNo;

	public boolean replyMessage;
	
	// Message to be sent back to unit
	public int messageId;
	public byte[] message;
	
}

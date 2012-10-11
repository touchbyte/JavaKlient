package com.javanetics.turbotransfer;

public interface HttpPutTransferListener
{
	public void onTransferFinished();
	public void onTransfer(long transferredSize, long totalSize);
}

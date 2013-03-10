package com.javanetics.turbotransfer;

public interface HttpPutTransferListener
{
	public void onStartTransferFinished();
	public void onStopTransferFinished();
	public void onTransferFinished();
	public void onTransfer(long transferredSize, long totalSize);
}

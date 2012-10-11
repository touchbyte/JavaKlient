package com.javanetics.turbotransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.entity.AbstractHttpEntity;

/**
 * File entity which supports a progress bar.<br/>
 * Based on "org.apache.http.entity.FileEntity".
 * @author Benny Neugebauer (www.bennyn.de)
 */
public class FileEntityWithListener extends AbstractHttpEntity implements Cloneable
{

  protected final File file;
  private HttpPutTransferListener listener;
  private long transferredBytes;

  public FileEntityWithListener(final File file, final String contentType)
  {
    super();
    if (file == null)
    {
      throw new IllegalArgumentException("File may not be null");
    }
    this.file = file;
    this.transferredBytes = 0;
    setContentType(contentType);
  }

  public HttpPutTransferListener getListener()
	{
		return listener;
	}

	public void setListener(HttpPutTransferListener listener)
	{
		this.listener = listener;
	}

	public boolean isRepeatable()
  {
    return true;
  }

  public long getContentLength()
  {
    return this.file.length();
  }

  public InputStream getContent() throws IOException
  {
    return new FileInputStream(this.file);
  }

  public void writeTo(final OutputStream outstream) throws IOException
  {
    if (outstream == null)
    {
      throw new IllegalArgumentException("Output stream may not be null");
    }
    InputStream instream = new FileInputStream(this.file);
    try
    {
      byte[] tmp = new byte[1024*32];
      int l;
      while ((l = instream.read(tmp)) != -1)
      {
        outstream.write(tmp, 0, l);
        this.transferredBytes += l;
        if (null != this.listener) this.listener.onTransfer(this.transferredBytes, getContentLength());
      }
      outstream.flush();
    }
    finally
    {
      instream.close();
    }
  }

  public boolean isStreaming()
  {
    return false;
  }

  @Override
  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }
}
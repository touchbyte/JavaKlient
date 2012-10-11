package com.javanetics.turbotransfer;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class ProgressListener
{

  private JProgressBar progressBar = null;
  private JLabel fileProgress;
  private JLabel transferProgress;
  private int actualFile;
  private int totalFiles;
  private long fileSize;
  private long actualTransferred;

  public ProgressListener()
  {
    super();
  }

  public ProgressListener(int actualFile, int totalFiles, long fileSize, JProgressBar progressBar, JLabel fileProgress, JLabel transferProgress)
  {
    this();
    this.progressBar = progressBar;
    this.fileProgress = fileProgress;
    this.transferProgress = transferProgress;
    this.actualFile = actualFile;
    this.totalFiles = totalFiles;
    this.fileSize = fileSize;
    initProgress();
  }

  public void initProgress()
  {
  	SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        transferProgress.setText(String.format(Localizer.sharedLocalizer().localizedString("TransferStatus"), actualFile, totalFiles));
      }
  	});
  }
  
  public void updateTransferred(long transferedBytes)
  {
  	actualTransferred = transferedBytes;
  	SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        progressBar.setValue(new Float((actualFile / totalFiles)*100.0).intValue());
        progressBar.paint(progressBar.getGraphics());
        fileProgress.setText(String.format(Localizer.sharedLocalizer().localizedString("FileProgress"), (actualTransferred*1.0 / fileSize*1.0)*100.0));
      }
  	});
  }
}

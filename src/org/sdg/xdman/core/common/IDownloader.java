package org.sdg.xdman.core.common;

import java.util.*;

public interface IDownloader {
	public int getState();

	public UUID getID();

	public void setProgressListener(DownloadProgressListener pl);

	public void setDownloadListener(DownloadStateListner pl);

	public void setTempdir(String tempdir);

	public String getTempdir();

	public void setFileName(String file);

	public String getFileName();

	public void setDestdir(String destdir);

	public String getDestdir();

	public void setUrl(String url);

	public String getUrl();

	public void resume();

	public Credential getCreditential();

	public void setCredential(String user, String pass);

	public void start() throws UnsupportedProtocolException;

	public void stop();

	public void setOverwrite(boolean b);

	public int getType();

}

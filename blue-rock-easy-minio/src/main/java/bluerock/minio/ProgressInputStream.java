package bluerock.minio;

import dataworks.ExceptionInfoFormatter;
import lombok.extern.slf4j.Slf4j;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class ProgressInputStream extends FilterInputStream
{
    private final IProgressListener progressListener;
    private int readLength;
    private int totalLength;

    public ProgressInputStream(InputStream inputStream, IProgressListener progressListener)
    {
        super(inputStream);
        this.progressListener = progressListener;

        try
        {
            totalLength = inputStream.available();
        }
        catch (IOException e)
        {
            String exceptionInfo = ExceptionInfoFormatter.formatMessageAndStackTrace(e);
            log.error("Error when getting available bytes of input stream..., " + exceptionInfo);
            totalLength = 0;
        }
        readLength = 0;
    }

    public int getReadLength()
    {
        return readLength;
    }

    private void notifyProcess(long read)
    {
        if (progressListener != null)
        {
            if (totalLength > 0)
                progressListener.onProgress(((double) read * 100 / totalLength));
            else
                progressListener.onProgress(0);
        }
    }

    @Override
    public int read() throws IOException
    {
        int nextChar = in.read();
        if (nextChar >= 0)
            notifyProcess(++readLength);
        return nextChar;
    }

    @Override
    public int read(byte[] b) throws IOException
    {
        int readLength = in.read(b);
        if (readLength > 0)
            notifyProcess(this.readLength += readLength);

        return readLength;
    }

    @Override
    public int read(byte[] b, int offset, int maxLength) throws IOException
    {
        int readLength = in.read(b, offset, maxLength);
        if (readLength > 0)
            notifyProcess(this.readLength += readLength);
        return readLength;
    }

    @Override
    public long skip(long n) throws IOException
    {
        long readLength = in.skip(n);
        if (readLength > 0)
            notifyProcess(this.readLength += readLength);
        return readLength;
    }

    @Override
    public synchronized void reset() throws IOException
    {
        in.reset();
        readLength = 0;
    }
}

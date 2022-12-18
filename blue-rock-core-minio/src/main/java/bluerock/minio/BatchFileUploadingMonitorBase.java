package bluerock.minio;

import dataworks.minio.IProgressListenerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.Future;

public abstract class BatchFileUploadingMonitorBase
{
    private ThreadPoolTaskExecutor taskExecutor;
    private HashMap<String, InputStream> fileNameToInputStreamMap;
    private int maxConcurrency;
    private HashMap<String, Future<String>> uploadingResult;
    private IProgressListenerFactory progressListenerFactory;
}

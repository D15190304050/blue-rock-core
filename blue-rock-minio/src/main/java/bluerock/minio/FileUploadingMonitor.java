//package bluerock.minio;
//
//import bluerock.dao.FileChunkMapper;
//import bluerock.dao.FileUploadingTaskMapper;
//import bluerock.domain.FileMetadata;
//import bluerock.domain.FileChunk;
//import bluerock.domain.FileUploadingTask;
//import dataworks.ExceptionInfoFormatter;
//import dataworks.autoconfig.minio.EasyMinio;
//import dataworks.minio.IProgressListener;
//import dataworks.minio.ProgressInputStream;
//import dataworks.params.ArgumentValidator;
//import io.minio.errors.*;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.ValueOperations;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.ByteArrayInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;
//import java.util.List;
//
//// TODO: Check if uploadingTask sync with the record in database.
//
///**
// * The {@link FileUploadingMonitor} class represents a monitor and a manipulator for the progress of uploading of a single file.
// *
// * @apiNote Please make sure the file name is legal before set up the file uploading task.
// */
//@Slf4j
//public class FileUploadingMonitor
//{
//    /**
//     * 64 MB for the max batch size.
//     */
//    public static final int MAX_BUFFER_SIZE = 1024 * 1024 * 16;
//    public static final int BYTE_BUFFER_SIZE = 1024;
//
//    private InputStream inputStream;
//    private String objectName;
//    private String fileName;
//    private ProgressInputStream progressInputStream;
//    private IProgressListener progressListener;
//    private EasyMinio easyMinio;
//    private String bucketName;
//    private FileChunkMapper fileChunkMapper;
//    private FileUploadingTaskMapper fileUploadingTaskMapper;
//
//    private int bufferSize;
//    private byte[] partBuffer;
//    private FileUploadingTask fileUploadingTask;
//
//    private ValueOperations<String, String> valueOperations;
//
//
//    /**
//     * Call this method only when creating a new file uploading task.
//     * @param easyMinio
//     * @param progressListener
//     * @param fileChunkMapper
//     * @param fileUploadingTaskMapper
//     * @throws FileNotFoundException
//     */
//    public FileUploadingMonitor(EasyMinio easyMinio,
//                                ValueOperations<String, String> valueOperations,
//                                FileMetadata fileMetadata,
//                                IProgressListener progressListener,
//                                FileChunkMapper fileChunkMapper,
//                                FileUploadingTaskMapper fileUploadingTaskMapper)
//            throws IOException
//    {
//        initializeFields(easyMinio, valueOperations, fileMetadata, progressListener, fileChunkMapper, fileUploadingTaskMapper);
//
//        // taskId will be initialized in the initializeTask() method.
//        initializeTask(fileMetadata);
//    }
//
//    public FileUploadingMonitor(EasyMinio easyMinio,
//                                ValueOperations<String, String> valueOperations,
//                                FileMetadata fileMetadata,
//                                IProgressListener progressListener,
//                                FileChunkMapper fileChunkMapper,
//                                FileUploadingTaskMapper fileUploadingTaskMapper,
//                                long taskId)
//            throws IOException
//    {
//        initializeFields(easyMinio, valueOperations, fileMetadata, progressListener, fileChunkMapper, fileUploadingTaskMapper);
//        initializeTask(taskId);
//    }
//
//    private void initializeFields(EasyMinio easyMinio,
//                                  ValueOperations<String, String> valueOperations,
//                                  FileMetadata fileMetadata,
//                                  IProgressListener progressListener,
//                                  FileChunkMapper fileChunkMapper,
//                                  FileUploadingTaskMapper fileUploadingTaskMapper)
//            throws IOException
//    {
//        //region Non-null validation.
//        ArgumentValidator.requireNonNull(easyMinio, "easyMinio");
//        ArgumentValidator.requireNonNull(progressListener, "progressListener");
//        ArgumentValidator.requireNonNull(fileChunkMapper, "filePartMapper");
//        ArgumentValidator.requireNonNull(fileUploadingTaskMapper, "fileUploadingTaskMapper");
//        ArgumentValidator.requireNonNull(fileMetadata, "fileMetadata");
//        //endregion
//
//        //region Other validation.
//        //endregion
//
//        //region Member initialization.
//        this.easyMinio = easyMinio;
//        this.valueOperations = valueOperations;
//        this.bucketName = fileMetadata.getBucketName();
//        this.objectName = fileMetadata.getObjectName();
//        this.fileName = fileMetadata.getFileName();
//        this.progressListener = progressListener;
//        this.fileChunkMapper = fileChunkMapper;
//        this.fileUploadingTaskMapper = fileUploadingTaskMapper;
//        this.partBuffer = new byte[MAX_BUFFER_SIZE];
//        this.bufferSize = 0;
//        //endregion
//    }
//
//    private void initializeTask(FileMetadata fileMetadata) throws IOException
//    {
//        //region Insert fileUploadingTask to database.
//        FileUploadingTask fileUploadingTask = new FileUploadingTask();
//
//        fileUploadingTask.setUserId(fileMetadata.getUserId());
//        fileUploadingTask.setState(FileUploadingTaskState.NOT_STARTED.getCode());
//        fileUploadingTask.setMetadataId(fileMetadata.getId());
//        fileUploadingTask.setUploadedByteCount(0);
//        fileUploadingTask.setTotalByteCount(this.inputStream.available());
//
//        fileUploadingTaskMapper.insert(fileUploadingTask);
//        //endregion
//
//        this.fileUploadingTask = fileUploadingTask;
//    }
//
//    private void initializeTask(long taskId)
//    {
//        fileUploadingTask = fileUploadingTaskMapper.getTaskById(taskId);
//    }
//
//    public FileUploadingTaskState getState()
//    {
//        return FileUploadingTaskState.getStateByCode(fileUploadingTask.getState());
//    }
//
//    public FileUploadingTask getFileUploadingTask()
//    {
//        return fileUploadingTask;
//    }
//
//    public String getObjectName()
//    {
//        return objectName;
//    }
//
//    public String getFileName()
//    {
//        return fileName;
//    }
//
//    public InputStream getInputStream()
//    {
//        return inputStream;
//    }
//
//    public String getTaskKey()
//    {
//        return "fileUploadingTask_" + fileUploadingTask.getId();
//    }
//
//    private FileUploadingTaskState getTaskState()
//    {
//        String currentStateCodeString = valueOperations.get(getTaskKey());
//        int currentStateCode = Integer.parseInt(currentStateCodeString);
//        FileUploadingTaskState currentState = FileUploadingTaskState.getStateByCode(currentStateCode);
//
//        if (currentStateCode != fileUploadingTask.getState())
//            setTaskState(currentState);
//
//        return currentState;
//    }
//
//    public void run(MultipartFile file) throws IOException, InterruptedException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
//    {
//        setInputStreamForUploading(file);
//
//        fileUploadingTask.setState(FileUploadingTaskState.UPLOADING.getCode());
//
//        while (getTaskState() == FileUploadingTaskState.UPLOADING)
//        {
//            byte[] nextBatch = new byte[BYTE_BUFFER_SIZE];
//            int readLength = progressInputStream.read(nextBatch, this.bufferSize, BYTE_BUFFER_SIZE);
//
//            if (readLength == -1)
//            {
//                // Upload the last part if exists.
//                flushPartBuffer();
//
//                // Close the input stream since we meet EOF.
//                progressInputStream.close();
//
//                // Set task state, finish the task, compose object.
//                finishTask();
//                break;
//            }
//            else
//            {
//                // Copy data from nextBatch to partBuffer.
//                System.arraycopy(nextBatch, 0, partBuffer, this.bufferSize, readLength);
//
//                // Update offset.
//                this.bufferSize += readLength;
//
//                // Upload next part if partBuffer is full.
//                if (this.bufferSize >= MAX_BUFFER_SIZE)
//                    uploadNextPart();
//            }
//
////            Thread.sleep(1);
//        }
//
//        if (getTaskState() == FileUploadingTaskState.PAUSED)
//        {
//            // Upload bytes in partBuffer.
//            flushPartBuffer();
//        }
//
//        // Release unused resources.
//        progressInputStream.close();
//    }
//
//    public Thread start(MultipartFile file)
//    {
//        Thread thread = new Thread(() ->
//        {
//            try
//            {
//                run(file);
//            }
//            catch (IOException | InterruptedException | ServerException | InsufficientDataException | ErrorResponseException | NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException | InternalException e)
//            {
//                log.error(ExceptionInfoFormatter.formatMessageAndStackTrace(e));
//            }
//        });
//        thread.start();
//        return thread;
//    }
//
//    public Thread startOrResume(MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
//    {
//        // Restart a new input stream.
//        ArgumentValidator.requireNonNull(file, "file");
//        setInputStreamForUploading(file);
//
//        fileUploadingTask.setState(FileUploadingTaskState.UPLOADING.getCode());
//
//        return start(file);
//    }
//
//    public int getRemainingFileSize() throws IOException
//    {
//        return inputStream.available();
//    }
//
//    public double getUploadingPercentage() throws IOException
//    {
//        return (double) progressInputStream.getReadLength() / getRemainingFileSize() * 100;
//    }
//
//    private void setInputStreamForUploading(MultipartFile file) throws IOException
//    {
//        inputStream = file.getInputStream();
//        progressInputStream = new ProgressInputStream(inputStream, progressListener);
//        progressInputStream.skip(fileUploadingTask.getUploadedByteCount());
//    }
//
//    private void uploadBytes(String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
//    {
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(partBuffer, 0, bufferSize);
//        easyMinio.putObject(bucketName, objectName, inputStream);
//    }
//
//    private void initializeFilePart(String objectName, long byteCount)
//    {
//        FileChunk fileChunk = new FileChunk();
//        fileChunk.setByteCount(byteCount);
//        fileChunk.setObjectName(objectName);
//        fileChunk.setTaskId(fileUploadingTask.getId());
//        fileChunkMapper.insert(fileChunk);
////        return filePart;
//    }
//
//    private String generateNextPartName()
//    {
//        return objectName + "_part_" + fileChunkMapper.countFilePartsByTaskId(fileUploadingTask.getId());
//    }
//
//    private void updateTaskUpdatedBytes()
//    {
//        fileUploadingTask.setUploadedByteCount(bufferSize);
//        fileUploadingTaskMapper.updateUploadedBytesById(fileUploadingTask.getId(), bufferSize);
//    }
//
//    private void uploadNextPart() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
//    {
//        String nextPartName = generateNextPartName();
//        initializeFilePart(nextPartName, bufferSize);
//        uploadBytes(nextPartName);
//
//        // Update task state (uploaded bytes) if upload success.
//        updateTaskUpdatedBytes();
//
//        // Reset part buffer for the uploading of next file part.
//        bufferSize = 0;
//    }
//
//    private void flushPartBuffer() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
//    {
//        if (bufferSize > 0)
//            uploadNextPart();
//    }
//
//    private boolean setTaskSuccess()
//    {
//        if (fileUploadingTask.getTotalByteCount() == fileUploadingTask.getUploadedByteCount())
//            return setTaskState(FileUploadingTaskState.SUCCESS);
//        return false;
//    }
//
//    private boolean setTaskState(FileUploadingTaskState state)
//    {
//        int stateCode = state.getCode();
//        fileUploadingTask.setState(stateCode);
//        return fileUploadingTaskMapper.setTaskStateById(fileUploadingTask.getId(), stateCode) > 0;
//    }
//
//    private void finishTask() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
//    {
//        // Set state as success.
//        boolean setResult = setTaskSuccess();
//        if (!setResult)
//            throw new IllegalStateException("Try to set uploading task as success when uploading has not finished." +
//                    "Total bytes: " + fileUploadingTask.getTotalByteCount() + "; uploaded bytes: " +
//                    fileUploadingTask.getUploadedByteCount() + ".");
//
//        // Compose object.
//        // 1) Get names of objects to compose, from database.
//        // 2) Compose them as a new object.
//        List<String> partNames = fileChunkMapper.getChunkNamesByTaskId(fileUploadingTask.getId());
//        easyMinio.composeObjects(bucketName, objectName, partNames);
//    }
//}

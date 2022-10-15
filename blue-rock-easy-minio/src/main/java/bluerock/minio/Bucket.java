package bluerock.minio;

public class Bucket
{
    public Bucket() {}

    public static String getBucketName(long userId)
    {
        return "bucket-of-" + userId;
    }
}

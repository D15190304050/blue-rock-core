package bluerock.minio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("bluerock.easyminio")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EasyMinioProperties
{
    // Configurations for MinioClient.
    private String endpoint;
    private String accessKey;
    private String secretKey;
}

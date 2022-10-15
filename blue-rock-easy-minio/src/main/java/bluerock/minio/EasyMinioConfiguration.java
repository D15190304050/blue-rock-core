package bluerock.minio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EasyMinioConfiguration
{
    @Autowired
    private EasyMinioProperties easyMinioProperties;

    @Bean
    public EasyMinio easyMinio()
    {
        return new EasyMinio(easyMinioProperties);
    }
}

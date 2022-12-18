package bluerock;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan(basePackages = {"bluerock.dao"})
@ComponentScan(basePackages = {"bluerock", "dataworks.autoconfig"})
public class BlueRockMain
{
    public static void main(String[] args)
    {
        SpringApplication.run(BlueRockMain.class);
    }
}

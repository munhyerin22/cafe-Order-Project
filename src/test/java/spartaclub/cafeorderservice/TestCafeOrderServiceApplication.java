package spartaclub.cafeorderservice;

import org.springframework.boot.SpringApplication;

public class TestCafeOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(CafeOrderServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}

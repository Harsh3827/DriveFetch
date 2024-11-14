package org.harshpatel.drivefetch;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class HelloApplication implements CommandLineRunner
{
    public static void main( String[] args )
    {

        SpringApplication.run(HelloApplication.class,args);
        System.out.println( "Hello World!" );
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting Car Rental Scraper CLI...");

       Entry r=new Entry();
        r.main(args);
    }
}
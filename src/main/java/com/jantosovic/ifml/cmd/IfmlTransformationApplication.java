package com.jantosovic.ifml.cmd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class IfmlTransformationApplication{

	/**
	 * Main method used as command line entry point.
	 *
	 * @param args are arguments supplied to utility from command line
	 */
	public static void main(String... args) {
		System.exit(SpringApplication.exit(SpringApplication.run(IfmlTransformationApplication.class, args)));
	}

}

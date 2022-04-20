package com.jantosovic.ifml.cmd;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

@Component
public class MyApplicationRunner implements CommandLineRunner, ExitCodeGenerator {

  private final TransformCommand transformCommand;

  private final IFactory factory; // auto-configured to inject PicocliSpringFactory

  private int exitCode;

  public MyApplicationRunner(TransformCommand transformCommand, IFactory factory) {
    this.transformCommand = transformCommand;
    this.factory = factory;
  }

  @Override
  public void run(String... args) {
    exitCode = new CommandLine(transformCommand, factory).execute(args);
  }

  @Override
  public int getExitCode() {
    return exitCode;
  }

  @Override
  public String toString() {
    return "MyApplicationRunner{"
        + "transformCommand=" + transformCommand
        + ", factory=" + factory
        + ", exitCode=" + exitCode
        + '}';
  }
}
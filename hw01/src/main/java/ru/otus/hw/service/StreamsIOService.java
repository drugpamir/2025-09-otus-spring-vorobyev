package ru.otus.hw.service;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class StreamsIOService implements IOService {

    private final Scanner input;

    private final PrintStream printStream;

    public StreamsIOService(InputStream inputStream, PrintStream printStream) {
        this.input = new Scanner(inputStream);
        this.printStream = printStream;
    }

    @Override
    public String readLine() {
        return input.nextLine().trim();
    }

    @Override
    public void printLine(String s) {
        printStream.println(s);
    }

    @Override
    public void printFormattedLine(String s, Object... args) {
        printStream.printf(s + "%n", args);
    }
}

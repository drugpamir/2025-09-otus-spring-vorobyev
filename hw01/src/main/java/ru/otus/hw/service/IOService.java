package ru.otus.hw.service;

public interface IOService {

    String readLine();

    void printLine(String s);

    void printFormattedLine(String s, Object ...args);
}

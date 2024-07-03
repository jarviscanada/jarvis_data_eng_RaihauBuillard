package ca.jrvs.apps.practice;

import java.util.regex.Pattern;

public class RegexExcImp implements RegexExc{
    @Override
    public boolean matchJpeg(String fileName) {
        return Pattern.matches(".[jpeg|jpg]", fileName);
    }

    @Override
    public boolean matchIp(String ip) {
        return Pattern.matches("/^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/", ip);
    }

    @Override
    public boolean isEmptyLine(String line) {
        return line.isEmpty();
    }
}

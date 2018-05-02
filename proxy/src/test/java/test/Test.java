package test;

import org.joda.time.DateTime;

public class Test {
    public static void main(String[] args) {

        DateTime dt = new DateTime();

        System.out.println(new DateTime());
        System.out.println(new DateTime().withTimeAtStartOfDay());
        System.out.println( dt.minusDays(7));
    }
}

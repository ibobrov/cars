package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ScriptsTool {
    public static final Properties CONFIG = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (InputStream in = HibernateCarRepositoryTest.class.getClassLoader()
                .getResourceAsStream("scripts.properties")) {
            CONFIG.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void executeScript(SessionFactory sf, String script) {
        try (var session = sf.openSession()) {
            Transaction transaction;
            transaction = session.beginTransaction();
            var query = session.createNativeQuery(script);
            query.executeUpdate();
            transaction.commit();
        }
    }

    public static String getProp(String name) {
        return CONFIG.getProperty(name);
    }
}

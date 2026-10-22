package org.acme.bot.processors.entity;

import org.acme.bot.processors.annotations.GenerateEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * Test of the ProcessorEntity : we launch a real javac in memory (javax.tools API of the JDK)
 * on a record, and we check the generated Entity class.
 * The generated class is also compiled : the test fails if it is not valid Java.
 *
 */
class ProcessorEntityTest {

    private static final String ENTITY_DIR = "org/acme/bot/infrastructure/persistence/entity/";

    @TempDir
    Path generated;

    @TempDir
    Path classes;

    @Test
    void generatesEntityFromRecord() throws IOException {
        var result = compile("demo.Role", """
                package demo;
                import org.acme.bot.processors.annotations.*;
                @GenerateEntity("roles")
                public record Role(@Unique String name, int level) {}
                """);

        assertTrue(result.success(), result.messages());
        Path entity = generated.resolve(ENTITY_DIR + "RoleEntity.java");
        assertTrue(Files.exists(entity), "RoleEntity.java must be generated");

        String code = Files.readString(entity);
        assertTrue(code.contains("@Table(name=\"roles\")"), code);
        assertTrue(code.contains("@Column(unique = true, nullable = false)\n   private java.lang.String name;"), code);
        assertTrue(code.contains("private int level;"), code);
        assertTrue(code.contains("public static RoleEntity fromModel(demo.Role model)"), code);
        assertTrue(code.contains("return new demo.Role(name, level);"), code);
    }

    @Test
    void defaultTableIsLowercaseRecordName() throws IOException {
        var result = compile("demo.InvitationLink", """
                package demo;
                @org.acme.bot.processors.annotations.GenerateEntity
                public record InvitationLink(String code) {}
                """);

        assertTrue(result.success(), result.messages());
        String code = Files.readString(generated.resolve(ENTITY_DIR + "InvitationLinkEntity.java"));
        assertTrue(code.contains("@Table(name=\"invitationlink\")"), code);
    }

    @Test
    void rejectsAnnotationOnClass() {
        var result = compile("demo.Bad", """
                package demo;
                @org.acme.bot.processors.annotations.GenerateEntity
                public class Bad {}
                """);

        assertFalse(result.success());
        assertTrue(result.messages().contains("ne peut être placé que sur un record"), result.messages());
    }

    // --- tools for the tests ---------------------------------------------------

    private record Result(boolean success, String messages) {
    }

    /**
     *
     * Compile one source with the ProcessorEntity
     *
     */
    private Result compile(String className, String source) {
        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        JavaFileObject file = new SimpleJavaFileObject(
                URI.create("string:///" + className.replace('.', '/') + ".java"), JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return source;
            }
        };

        List<String> options = List.of(
                "-s", generated.toString(),
                "-d", classes.toString(),
                "-classpath", classpathOf(GenerateEntity.class, jakarta.persistence.Entity.class));

        var task = javac.getTask(null, null, diagnostics, options, null, List.of(file));
        task.setProcessors(List.of(new ProcessorEntity()));
        boolean success = task.call();

        StringBuilder messages = new StringBuilder();
        for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
            messages.append(d.getKind()).append(": ").append(d.getMessage(null)).append('\n');
        }
        return new Result(success, messages.toString());
    }

    /**
     *
     * Classpath = folders / jars containing these classes.
     * (System.getProperty("java.class.path") is almost empty with Maven : Surefire uses a "manifest-only jar")
     *
     */
    private static String classpathOf(Class<?>... classes) {
        return Stream.of(classes)
                .map(c -> {
                    try {
                        return Path.of(c.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                })
                .distinct()
                .collect(Collectors.joining(File.pathSeparator));
    }
}

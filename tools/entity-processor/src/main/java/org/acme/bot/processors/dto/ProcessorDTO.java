package org.acme.bot.processors.dto;

import com.google.auto.service.AutoService;
import org.acme.bot.processors.annotations.DTO;
import org.acme.bot.processors.annotations.DTOs;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A DTO Annotation processor to generate a DTO class per (class,@DTO(value))
 *
 * @see DTO
 * @see DTOs
 */
@AutoService(Processor.class)
public class ProcessorDTO extends AbstractProcessor {

    private static final String DTO_PACKAGE="org.acme.bot.application.dto";
    /**
     * The string format for the DTO class
     */
    private static final String DTO_CLASS_PATTERN = """
            %1$s
            public record %2$s (%3$s) {
                public static %2$s from(%4$s source) {
                    return new %2$s(%5$s);
                }
            }""";

    /**
     * A Messager to emit messages during annotation processing
     */
    private Messager messager;

    /**
     * @return The annotations to process
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(DTO.class.getName(), DTOs.class.getName());
    }

    /**
     * Supported language version
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * Initializes the processor with the processing environment
     *
     * @param processingEnv environment to access facilities the tool framework
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        messager = processingEnv.getMessager();
        super.init(processingEnv);
    }

    /**
     * Processes the annotations and generates DTO classes
     *
     * @param annotations the annotation interfaces requested to be processed
     * @param roundEnv  environment for information about the current and prior round
     * @return false
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // We query the types that are annotated with our annotations
        // to produce a Map of annotation grouped by @DTO(value) and by containing classes.
        Map<Element, Map<String, List<DTOAnnotation>>> annotatedFields = roundEnv.getElementsAnnotatedWithAny(Set.of(DTO.class, DTOs.class)).stream()
                .map(field -> Arrays.stream(field.getAnnotationsByType(DTO.class))
                        .map(annotation -> new DTOAnnotation(field, annotation)).toList())
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(d -> d.field.getEnclosingElement(), Collectors.groupingBy(d -> d.annotation.value())));
        annotatedFields.forEach(this::writeRecord);
        return false;
    }

    /**
     * Generates DTO classes for each unique value of @DTO parameter
     *
     * @param baseClass The class to generate DTO for.
     * @param dtoAnnotations the list of fields in every DTO (grouped by values).
     */
    private void writeRecord(Element baseClass, Map<String, List<DTOAnnotation>> dtoAnnotations) {
        // We compute the base class and package names.
        final String className = baseClass.asType().toString();
        final int lastDot = className.lastIndexOf('.');
        final String packageName = DTO_PACKAGE;
        final String simpleClassName = className.substring(lastDot + 1);

        dtoAnnotations.forEach((domain, fields) -> {
            final String dtoSimpleClassName = simpleClassName + domain;
            final String dtoClassName = packageName + "." + dtoSimpleClassName;
            final String fieldsDeclarations = fields.stream()
                    .map(e -> "%s %s".formatted(
                            e.field().asType().toString(),
                            e.field())).collect(Collectors.joining(", "));
            // a record has accessors name() and not getters getName()
            final String fieldList = fields.stream()
                    .map(f -> "source.%s()".formatted(f.field().toString())).collect(Collectors.joining(", "));
            messager.printMessage(Diagnostic.Kind.NOTE, "DTO generation for %s (%s)".formatted(baseClass, domain));

            try {
                JavaFileObject targetFile = processingEnv.getFiler().createSourceFile(dtoClassName);
                try (PrintWriter out = new PrintWriter(targetFile.openWriter())) {
                    out.print(DTO_CLASS_PATTERN.formatted(packageName.isEmpty() ? "" : "package " + packageName + ";",
                            dtoSimpleClassName,
                            fieldsDeclarations,
                            className,
                            fieldList));
                }
            } catch (IOException ex) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Unable to create target %s.".formatted(dtoClassName));
            }
        });
    }

    private record DTOAnnotation(Element field, DTO annotation) {
    }
}

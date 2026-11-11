package org.acme.bot.processors.entity;

import com.google.auto.service.AutoService;
import org.acme.bot.processors.annotations.GenerateEntity;
import org.acme.bot.processors.annotations.Unique;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

/**
 *
 * For each record with annotation @GenerateEntity(table="...") : generate an ...Entity class (JPA Entity)
 *
 * A Entity Annotation processor to generate a Entity class per (class,@Entity(value))
 *
 */
@SupportedAnnotationTypes("org.acme.bot.processors.annotations.GenerateEntity")
@AutoService(Processor.class)
public class ProcessorEntity extends AbstractProcessor {

    private static final String ENTITY_PACKAGE="org.acme.bot.infrastructure.persistence.entity";

    /**
     *The string format for the Entity class
     */
    private static final String ENTITY_CLASS_PATTERN= """
                    package %s;

                    import jakarta.persistence.*;

                    @Entity
                    @Table(name="%s")
                    public class %s {

                        @Id
                        @GeneratedValue(strategy=GenerationType.IDENTITY)
                        private Long id;

                    %s

                        public Long getId(){return id;}
                        public void setId(Long id){this.id=id;}

                    %s

                        public static %s fromModel(%s model){
                            %s entity=new %s();
                    %s
                            return entity;
                        }

                        public %s toModel(){
                            return new %s(%s);
                        }
                    }
                    """;

    /**
     * A Messager to emit messages during annotation processing
     */
    private Messager messager;

    /**
     *
     * @return the latest supported version of Java
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


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(GenerateEntity.class);

        for (Element element : roundEnv.getElementsAnnotatedWith(GenerateEntity.class)) {

            // the annotation can only be used on a record
            if (element.getKind() != ElementKind.RECORD) {
                messager.printMessage(Diagnostic.Kind.ERROR, "@GenerateEntity ne peut être placé que sur un record", element);
                continue;
            }

            TypeElement model = (TypeElement) element;

            // get the model class name
            String modelName=model.getSimpleName().toString();
            String modelClass=model.getQualifiedName().toString();
            String entityName=modelName+"Entity";

            // if no name is given, the table has the name of the record
            String tableName=model.getAnnotation(GenerateEntity.class).value();
            if (tableName.isBlank()) {
                tableName=modelName.toLowerCase();
            }

            // for each field in the record class : attribute + getter + setter
            String fields="";
            String methods="";
            String fromModel="";
            String toModel="";


            for (RecordComponentElement field : model.getRecordComponents()) {

                String fieldType = field.asType().toString();
                String fieldName = field.getSimpleName().toString();
                String Name = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                // @Unique on the field : the column must be unique in the database
                if (field.getAnnotation(Unique.class) != null) {
                    fields+="   @Column(unique = true, nullable = false)\n";
                }
                fields+="   private "+ fieldType + " " + fieldName+ ";\n";
                methods+="  public "+ fieldType + " get" + Name+ "(){ return "+fieldName+";}\n";
                methods+="  public void set"+Name+"("+fieldType+" "+fieldName+"){this."+fieldName+"="+fieldName+";}\n";

                // conversion model <-> entity
                fromModel+="        entity."+fieldName+"=model."+fieldName+"();\n";
                toModel+=(toModel.isEmpty() ? "" : ", ")+fieldName;
            }

            //the class patern (once all the fields are read)

            String entityClass=ENTITY_CLASS_PATTERN.formatted(
                    ENTITY_PACKAGE,tableName,entityName,fields,methods,
                    entityName,modelClass,entityName,entityName,fromModel,
                    modelClass,modelClass,toModel);

            // we write the .java file : javac will compile it

            try{
                var file=processingEnv.getFiler().createSourceFile(ENTITY_PACKAGE+"."+entityName,model);
                try(Writer writer=file.openWriter()){
                    writer.write(entityClass);
                }
                messager.printMessage(Diagnostic.Kind.NOTE, "Entity generation for %s (%s)".formatted(modelName, entityName));
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Impossible de générer "+entityName, model);
            }
        }
        return true;
    }
}

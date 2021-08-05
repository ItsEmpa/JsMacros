package xyz.wagyourtail.doclet.pydoclet.parsers;

import xyz.wagyourtail.doclet.pydoclet.Main;

import javax.lang.model.element.*;
import javax.lang.model.type.*;
import java.util.*;

public class ClassParser {
    public final TypeElement type;
    private final List<TypeMirror> imports = new LinkedList<>();
    private boolean importOverload = false, importList = false, importTypeVar = false, importAny = false, importMapping = false, importSet = false, importGeneric = false;
    HashMap<String, Map.Entry<String, Boolean>> typeVars = new HashMap<>();
    private final HashMap<String, String> types = new HashMap<>(){{
        put("java.lang.Object", "object");
        put("java.lang.String", "str");
        put("java.lang.Integer", "int");
        put("java.lang.Boolean", "bool");
        put("java.lang.Double", "float");
        put("java.lang.annotation.Annotation", "");
        put("java.lang.Enum", "");
        put("java.util.Iterator", "iter");
    }},
                                          withArg = new HashMap<>(){{
        put("java.util.Set", "Set");
        put("java.util.List", "List");
        put("java.util.Map", "Mapping");
        put("java.util.Collection", "List");
    }},
                                          unwantedClass = new HashMap<>(){{
        put("java.lang.Object", "");
        put("java.lang.annotation.Annotation", "");
        put("java.lang.Enum", "");
        put("java.util.Collection", "");
    }};



    public ClassParser(TypeElement type) {
        this.type = type;
    }


    public String parseClass(){
        StringBuilder sb = new StringBuilder();

        //ClassLine e.g class Test(AbstractTest, ):
        sb.append(getClassLine());

        //Fields
        sb.append(getFields()).append("\n");

        //Methods / Constructor
        sb.append(getMethods());

        //safety pass
        sb.append(getTabs(1)).append("pass\n\n");

        //imports + abstract types
        sb.insert(0, getImports()).append("\n");

        //Main.reporter.print(Diagnostic.Kind.NOTE, type + ", " + imports + "");

        return sb.toString();
    }

    private String getMethods() {
        StringBuilder sb = new StringBuilder();


        type.getEnclosedElements().stream().filter(e -> e.getKind() == ElementKind.METHOD || e.getKind() == ElementKind.CONSTRUCTOR).forEach(el ->{
            if (!el.getModifiers().contains(Modifier.PUBLIC)) return;
            ExecutableElement method = (ExecutableElement) el;
            importOverload = true;
            sb.append(getTabs(1)).append("@overload\n");
            sb.append(getTabs(1)).append("def ");
            if(el.getKind() == ElementKind.CONSTRUCTOR){
                sb.append("__init__");
            }else{
                sb.append(method.getSimpleName());
            }

            sb.append("(self");

            method.getParameters().forEach(parameter -> sb.append(", ").append(parameter.getSimpleName()).append(": ").append(getTypeMirrorName(parameter.asType(), false)));
            sb.append(") -> ");
            //Main.reporter.print(Diagnostic.Kind.NOTE, getTypeMirrorName(method.getReturnType(), false) + "");
            if (method.getReceiverType() != null) sb.append(getTypeMirrorName(method.getReturnType(), false));
            sb.append(":\n");
            sb.append(getTabs(2)).append("pass\n\n");
        });

        return sb.toString();
    }

    private String getImports(){
        StringBuilder sb = new StringBuilder();

        List<String> imp = new LinkedList<>();
        //Main.reporter.print(Diagnostic.Kind.NOTE, type + ": " + importList);

        imports.forEach(t -> {
            if(!types.containsKey(getClearedNameFromTypeMirror(t))){
                if(!withArg.containsKey(getClearedNameFromTypeMirror(t))) {
                    if ((t + "").startsWith("xyz")) {
                        if (Main.typeUtils.asElement(t) != null) {
                            if (!getClassName((TypeElement) Main.typeUtils.asElement(t)).equals(getClassName(type))) {
                                String path = getClassName((TypeElement) Main.typeUtils.asElement(t)); //getPackage((TypeElement) Main.typeUtils.asElement(t)) + "." +
                                if (!imp.contains(path)) imp.add(path);
                            }
                        }
                    } else if ((t + "").startsWith("net") || (t + "").startsWith("com") || (t + "").startsWith("io") || (t + "").startsWith("java.util") ||
                            (t + "").startsWith("java.lang.Runnable") || (t + "").startsWith("java.lang.Thread") || (t + "").startsWith("java.lang.Throwable") ||
                            (t + "").startsWith("java.util.function") || (t + "").startsWith("java.lang.ref") || (t + "").startsWith("java.io") || (t + "").startsWith("org") || (t + "").startsWith("java.lang.Iterable") ||
                            (t + "").startsWith("java.lang.StackTraceElement")) {
                        //Main.reporter.print(Diagnostic.Kind.NOTE, typeVars + "");
                        if (!importTypeVar) importTypeVar = true;
                        typeVars.put(
                                getClassName((TypeElement) Main.typeUtils.asElement(t)),
                                new AbstractMap.SimpleEntry<>(t + "", false)
                        );
                    }
                }
            }
        });

        if (importOverload)
            sb.append("from typing import overload\n");
        if (importList)
            sb.append("from typing import List\n");
        if (importTypeVar)
            sb.append("from typing import TypeVar\n");
        if (importAny)
            sb.append("from typing import Any\n");
        if (importMapping)
            sb.append("from typing import Mapping\n");
        if(importSet)
            sb.append("from typing import Set\n");
        if(importGeneric)
            sb.append("from typing import Generic\n");

        imp.forEach(s -> sb.append("from .").append(s).append(" import ").append(s).append("\n"));

        sb.append("\n");
        for(Map.Entry<String, Map.Entry<String, Boolean>> entry : typeVars.entrySet()){
            sb.append(entry.getKey()).append(" = TypeVar").append( entry.getValue().getValue() ? "(" : "[").append("\"").append(entry.getValue().getKey().replace("<", "_").replace(">", "_").replace("?", "")).append("\"").append( entry.getValue().getValue() ? ")" : "]").append("\n");
            //sb.append(entry.getKey()).append(" = TypeVar[\"").append(entry.getValue().replace("<", "_").replace(">", "_").replace("?", "")).append("\"]\n");
        }
        sb.append("\n");

        return sb.toString();
    }

    private String getClearedNameFromTypeMirror(TypeMirror type){
        StringBuilder s = new StringBuilder(type + "");
        if (s.indexOf("<") != -1){
            s.delete(s.indexOf("<"), s.length());
        }
        if (s.indexOf(" ") != -1){
            s.delete(0, s.indexOf(" ") + 1);
        }
        return s.toString();
    }

    private String getFields(){
        StringBuilder sb = new StringBuilder();

        type.getEnclosedElements().stream().filter(e -> e.getKind().equals(ElementKind.FIELD)|| e.getKind().equals(ElementKind.ENUM_CONSTANT)).forEach(el ->{
            if (!el.getModifiers().contains(Modifier.PUBLIC)) return;
            addImport(el.asType());
            sb.append(getTabs(1));
            sb.append(el.getSimpleName());
            sb.append(": ");
            sb.append(getTypeMirrorName(el.asType(), false));
            sb.append("\n");
        });

        return sb.toString();
    }


    private String getTabs(int amount){
        return new String(new char[amount]).replace("\0", "\t");
    }

    private String getClassLine(){
        StringBuilder sb = new StringBuilder("class ");
        sb.append(getClassName(type));

        List<? extends TypeMirror> implement = type.getInterfaces();
        TypeMirror extend = type.getSuperclass();

        if (!(implement.size() == 0 && extend.getKind().equals(TypeKind.NONE))){
            sb.append("(");
            implement.forEach(i -> {
                sb.append(getTypeMirrorName(i, true)).append(", ");
                addImport(i);
            });
            if(type.getTypeParameters().size() > 0){
                importGeneric = true;
                addImport(type.getTypeParameters().get(0).asType());
                sb.append("Generic[");
                sb.append(getTypeMirrorName(type.getTypeParameters().get(0).asType(), true));
                for(int i = 1; i < type.getTypeParameters().size(); i++){
                    sb.append(", ").append(getTypeMirrorName(type.getTypeParameters().get(i).asType(), true));
                    addImport(type.getTypeParameters().get(0).asType());
                }
                sb.append("], ");
            }
            if (!extend.getKind().equals(TypeKind.NONE)){
                sb.append(getTypeMirrorName(extend, true));
                addImport(extend);
            }
            //Main.reporter.print(Diagnostic.Kind.NOTE, sb.lastIndexOf(", ") + ", " + sb.length());
            if (sb.lastIndexOf(", ") == sb.length() - 2) sb.delete(sb.length() - 2, sb.length());
            sb.append(")");
            if (sb.lastIndexOf("()") != -1) sb.delete(sb.length() - 2, sb.length());
        }


        sb.append(":\n");

        return sb.toString();
    }

    private void addImport(TypeMirror type){
        if(type != null) {
            if (!imports.contains(type)) imports.add(type);
            if (type.getKind().equals(TypeKind.ARRAY))
                addImport(((ArrayType) type).getComponentType());
        }

    }

    private String getTypeMirrorName(TypeMirror type, boolean cls) {
        imports.add(type);
        //Main.reporter.print(Diagnostic.Kind.MANDATORY_WARNING, type + "");
        switch (type.getKind()) {
            case BOOLEAN -> {
                return "bool";
            }
            case BYTE, SHORT, LONG, FLOAT, DOUBLE -> {
                return "float";
            }
            case INT -> {
                return "int";
            }
            case CHAR -> {
                return "str";
            }
            case VOID, NONE, NULL -> {
                return "None";
            }
            case ARRAY -> {
                //List[NAME]
                importList = true;
                return "List[" + getTypeMirrorName(((ArrayType) type).getComponentType(), false) + "]";
            }
            case DECLARED -> {
                return parseDeclared(type, cls);
            }
            case TYPEVAR -> {
                typeVars.put(
                        ((TypeVariable) type).asElement().getSimpleName().toString(),
                        new AbstractMap.SimpleEntry<>(((TypeVariable) type).asElement().getSimpleName().toString(), true)
                );
                importTypeVar = true;
                return ((TypeVariable) type).asElement().getSimpleName().toString();
            }
            case WILDCARD -> {
                importAny = true;
                return "Any";
            }
            default -> throw new UnsupportedOperationException(String.valueOf(type.getKind()));
        }
    }

    private String parseDeclared(TypeMirror type, boolean cls){
        //Main.reporter.print(Diagnostic.Kind.NOTE, this.type + ": " + getClearedNameFromTypeMirror(type) + "");
        if (cls){
            if (unwantedClass.containsKey(getClearedNameFromTypeMirror(type))){
                return unwantedClass.get(getClearedNameFromTypeMirror(type));
            }
        }
        if (types.containsKey(getClearedNameFromTypeMirror(type))){

            if (types.get(getClearedNameFromTypeMirror(type)).contains("List")){
                importList = true;
            }
            return types.get(getClearedNameFromTypeMirror(type));
        }
        //Main.reporter.print(Diagnostic.Kind.NOTE, this.type + ": " + getClearedNameFromTypeMirror(type));

        if(getClearedNameFromTypeMirror(this.type.asType()).equals(getClearedNameFromTypeMirror(type))) return "\"" + getClassName((TypeElement) Main.typeUtils.asElement(type)) + "\"";

        if(withArg.containsKey(getClearedNameFromTypeMirror(type))){
            if (withArg.get(getClearedNameFromTypeMirror(type)).contains("List")) importList = true;
            else if(withArg.get(getClearedNameFromTypeMirror(type)).contains("Mapping")) importMapping = true;
            else if(withArg.get(getClearedNameFromTypeMirror(type)).contains("Set")) importSet = true;

            //Main.reporter.print(Diagnostic.Kind.NOTE, this.type + ", " + type + ": " + ((DeclaredType) type).getTypeArguments());
            StringBuilder sb = new StringBuilder(withArg.get(getClearedNameFromTypeMirror(type)) + "[" + getTypeMirrorName(((DeclaredType) type).getTypeArguments().get(0), false));
            for (int i = 1; i < ((DeclaredType) type).getTypeArguments().size(); i++){
                sb.append(", ").append(getTypeMirrorName(((DeclaredType) type).getTypeArguments().get(i), false));
            }
            sb.append("]");
            return sb.toString();
        }
        return getClassName((TypeElement) Main.typeUtils.asElement(type));
    }




    /**
     * @return class name with _ for inner class
     */
    public static String getClassName(TypeElement type) {
        StringBuilder s = new StringBuilder(type.getSimpleName());
        Element t2 = type.getEnclosingElement();
        while (t2.getKind() == ElementKind.INTERFACE || t2.getKind() == ElementKind.CLASS) {
            s.insert(0, t2.getSimpleName() + "_");
            t2 = t2.getEnclosingElement();
        }
        return s.toString();
    }

}
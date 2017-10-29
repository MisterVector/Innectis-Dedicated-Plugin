package net.innectis.innplugin.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import net.innectis.innplugin.items.IdpMaterial;
import net.innectis.innplugin.objects.owned.ChestFlagType;
import net.innectis.innplugin.objects.owned.LotFlagType;
import net.innectis.innplugin.player.PlayerSettings;

/**
 * @author Hret
 *
 * Formats enums so that they are readable when filled with information.
 * To apply the formatter to an enum the Formattags must be inserted as comments before
 * and after the enum values.
 *
 * Furthermore the Margins can be given to which the enum must comply.
 * The first 2 are the initial spacing (which will be 4 most of the times) and
 * the name of the constant itself.
 *
 * If the rest of the margins don't matter they can be left out.
 * The formatter will then use the default of 1 spacing.
 *
 */
final class EnumFormatter {

    public interface Formattags {

        public static final String FORMAT_START = "#FORMAT_START";
        public static final String FORMAT_END = "#FORMAT_END";
    }

    private EnumFormatter() {
    }

    public static void main(String[] args) throws IOException {
        // spc, name, id, data, name, group, group, alt. names
        formatClass(IdpMaterial.class, 4, 24, 4, 2, 24, 21);

        formatClass(PlayerSettings.class, 4, 24, 3, 30, 24, 5);
        formatClass(LotFlagType.class, 4, 24, 3, 24, 24);
        formatClass(ChestFlagType.class, 4, 24, 3, 24, 24);

        //formatClass(PlayerGroup.class, 4, 20, 2, 2, 20, 22, 4); Not needed
    }

    private static void formatClass(Class clazz, int... margins) throws FileNotFoundException, IOException {
        String classpath = clazz.getCanonicalName().replace(".", File.separator) + ".java";
        File classfile = new File("src" + File.separator + classpath);

        // Print out file
        System.out.println("Formatting: " + classfile.getAbsolutePath());

        if (!classfile.exists()) {
            System.out.println("File not found!");
            return;
        }

        // Read input
        ArrayList<String> lines = readFile(classfile);

        // Write
        formatAndWrite(classfile, lines, margins);
    }

    private static ArrayList<String> readFile(File classfile) throws IOException, FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(classfile));
        java.util.ArrayList<String> lines = new java.util.ArrayList<String>(2000);
        String inputline;
        while ((inputline = reader.readLine()) != null) {
            lines.add(inputline);
        }
        reader.close();
        return lines;
    }

    private static void formatAndWrite(File output, ArrayList<String> lines, int[] margins) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(output));

        boolean formatteractive = false;
        for (String outputline : lines) {
            // Check keywords
            if (!formatteractive) {
                if (outputline.contains(Formattags.FORMAT_START)) {
                    formatteractive = true;
                }
            } else {
                if (outputline.contains(Formattags.FORMAT_END)) {
                    formatteractive = false;
                }
            }

            // formatter
            if (formatteractive) {
                outputline = formatLine(outputline, margins);

            }


            writer.write(outputline);
            writer.newLine();
            writer.flush();
        }
        writer.close();
    }

    private static String formatLine(String line, int[] margins) {

        if (line.trim().startsWith("/") || line.trim().startsWith("*")) {
            return line;
        }

        StringBuilder output = new StringBuilder(line.length() * 2);

        int currentMargin = 0;
        output.append(getSpaces(margins.length > 0 ? margins[currentMargin++] : 1));

        int charcounter = 0;
        boolean instr = false;
        boolean initclosed = false;
        for (char ch : line.toCharArray()) {
            // Skip spaces
            if (ch == ' ') {
                if (instr) {
                    output.append(ch);
                }
                continue;
            }

            if (ch == '"') {
                instr = !instr;
            }

            if (!instr) {
                if (ch == ')') {
                    initclosed = true;
                }

                if (ch == '(' || (!initclosed && ch == ',')) {
                    int spaces = (margins.length > currentMargin ? margins[currentMargin++] : 1) - charcounter;

                    output.append(getSpaces(spaces));
                    charcounter = 0;

                    if (spaces < 0) {
                        charcounter -= spaces;
                    }

                    if (ch == '(') {
                        output.append(ch);
                    }

                    if (ch == ',') {
                        output.append(ch);
                        output.append(' ');
                    }

                    continue;
                }
            }

            charcounter++;
            output.append(ch);
        }

        return output.toString();
    }

    private static String getSpaces(int size) {
        if (size <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }
    
}

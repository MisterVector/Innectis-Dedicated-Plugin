package net.innectis.innplugin.documentation;

import java.io.BufferedReader;
import net.innectis.innplugin.system.command.commands.MiscCommands;
import net.innectis.innplugin.system.command.commands.ChatCommands;
import net.innectis.innplugin.system.command.commands.CheatCommands;
import net.innectis.innplugin.system.command.commands.AdminCommands;
import net.innectis.innplugin.system.command.commands.PlayerCommands;
import net.innectis.innplugin.system.command.commands.TinyWECommands;
import net.innectis.innplugin.system.command.commands.ModerationCommands;
import net.innectis.innplugin.system.command.commands.LocationCommands;
import net.innectis.innplugin.system.command.commands.InformationCommands;
import net.innectis.innplugin.system.command.commands.ShopCommands;
import net.innectis.innplugin.system.command.commands.GameCommands;
import net.innectis.innplugin.system.command.commands.LotCommands;
import net.innectis.innplugin.system.command.commands.RequestCommands;
import net.innectis.innplugin.system.command.commands.SpoofCommands;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.innectis.innplugin.Configuration;
import net.innectis.innplugin.InnPlugin;
import net.innectis.innplugin.player.PlayerGroup;
import net.innectis.innplugin.system.command.CommandMethod;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Hret
 */
public class DocumentGenerator {

    public static void main(String args[]) throws Exception {
        String name = Configuration.PLUGIN_NAME;
        String author = "Hret, The_Lynxy, AlphaBlend & Nosliw";
        String version = Configuration.PLUGIN_VERSION;
        String main = "net.innectis.innplugin.InnPlugin";
        String rootFolder = "extra" + File.separator +  "commands" + File.separator;

        PlayerGroup maxPlayer = PlayerGroup.DIAMOND;
        PlayerGroup minPlayer = PlayerGroup.GUEST;

        PlayerGroup maxStaff = PlayerGroup.ADMIN;
        PlayerGroup minStaff = PlayerGroup.MODERATOR;

        File cmdFolder = new File(rootFolder);

        if (!cmdFolder.isDirectory()) {
            cmdFolder.mkdirs();
        }

        // Write yaml
        File ymlSourceFile = new File("src" + File.separator + "plugin.yml");
        writePluginYmlFile(ymlSourceFile, name, author, version, main);

        List<Class<?>> classes = getCommandClasses();
        classes.remove(SpoofCommands.class);

        try {
            File textFile = new File(rootFolder + "commandList.txt");
            writeCommandList(textFile, classes);
            System.out.println("Succesfully written command list");
        } catch (IOException ex) {
            Logger.getLogger(DocumentGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Generate normal user commands
        writeHtmlCommandlist(new File(rootFolder + "commands.html"), "Innectis Command list", maxPlayer, minPlayer, version);
        // Generate separate staff list
        writeHtmlCommandlist(new File(rootFolder + "staffcommands.html"), "Innectis Staff Command list", maxStaff, minStaff, version);

        // Generate normal user commands
        writeBBCCode(new File(rootFolder + "bbcommands.txt"), maxPlayer, minPlayer, version);
        // Generate separate staff list
        writeBBCCode(new File(rootFolder + "staffbbcommands.txt"), maxStaff, minStaff, version);

        System.out.println("\nSuccesfully written files for version " + version + "!");

    }

    private static void writeCommandList(File file, List<Class<?>> commandClasses)
            throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            try {
                StringBuilder sb = null;
                for (Class<?> cls : commandClasses) {
                    for (Method method : cls.getDeclaredMethods()) {
                        CommandMethod info = method.getAnnotation(CommandMethod.class);
                        if (info != null) {
                            sb = new StringBuilder("[");
                            for (String alias : info.aliases()) {
                                sb.append("/").append(alias).append(",");
                            }
                            sb.delete(sb.length() - 1, sb.length());
                            sb.append("] | ").append(info.description()).append(" | ").append(info.usage());
                            writer.write(sb.toString());
                            writer.write("\n");
                        }
                    }
                }
//                writer.write(aContents);
            } finally {
                writer.close();
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private static List<Class<?>> getCommandClasses() {

        List<Class<?>> classes = new ArrayList<Class<?>>();
        classes.add(AdminCommands.class);
        classes.add(CheatCommands.class);
        classes.add(ChatCommands.class);
        classes.add(InformationCommands.class);
        classes.add(GameCommands.class);

        classes.add(LocationCommands.class);
        classes.add(LotCommands.class);
        classes.add(MiscCommands.class);
        classes.add(ModerationCommands.class);
        classes.add(PlayerCommands.class);

        classes.add(ShopCommands.class);
        classes.add(SpoofCommands.class);
        classes.add(TinyWECommands.class);
        classes.add(RequestCommands.class);

        return classes;
    }

    private static void writePluginYmlFile(File ymlSourceFile, String name, String author, String version, String main) throws SecurityException, IOException {
        System.out.println("Clearing old file");
        ymlSourceFile.delete();
        try {
            ymlSourceFile.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(DocumentGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

        YamlConfiguration yml = YamlConfiguration.loadConfiguration(ymlSourceFile);

        System.out.println("Printing plugin information");
        yml.set("name", name);
        yml.set("author", author);
        yml.set("version", version);
        yml.set("main", main);
        yml.set("loadbefore", Arrays.asList("dynmap"));

        String firstAlias;
        int commandCounter = 0;

        List<Method> methods = new ArrayList<Method>();
        for (Class<?> cls : getCommandClasses()) {
            methods.addAll(Arrays.asList(cls.getDeclaredMethods()));
            System.out.println("Finished loading commands of the " + cls.getSimpleName() + " class.");
        }
        for (int i = 0; i < 100; i++) {
            Collections.sort(methods, new Comparator<Method>() {
                public int compare(Method o1, Method o2) {
                    if (o1 == o2 || o1.equals(o2)) {
                        return 0;
                    }
                    CommandMethod info1 = o1.getAnnotation(CommandMethod.class);
                    CommandMethod info2 = o2.getAnnotation(CommandMethod.class);
                    if (o1 == o2 || o1.equals(o2) || info1 == info2) {
                        return 0;
                    }
                    if (info1 != null && info2 != null) {
                        if (info1.permission().getGroup().id == info2.permission().getGroup().id) {
                            return 0;
                        }
                        return (info1.permission().getGroup().id < info2.permission().getGroup().id) ? -1 : 1;
                    }
                    return (info1 == null) ? -1 : 1;
                }
            });
        }

        for (Method method : methods) {
            CommandMethod info = method.getAnnotation(CommandMethod.class);
            if (info != null) {
                commandCounter++;
                firstAlias = null;
                for (String alias : info.aliases()) {
                    if (firstAlias == null) {
                        firstAlias = alias;
                        yml.set("commands." + alias + ".description", ""); //info.description());
                        yml.set("commands." + alias + ".usage", "");//info.usage());
                        //yml.set("commands." + alias + ".permission.name", info.permission().name());
                        //yml.set("commands." + alias + ".permission.group", info.permission().getGroup().name());
                        //yml.set("commands." + alias + ".servercommand", info.serverCommand());
                    } else {
                        yml.set("commands." + alias + ".description", ""); // "This command is an alias of " + firstAlias);
                        yml.set("commands." + alias + ".usage", ""); // info.usage());
                    }
                }
            }
        }

        // These commands are dynamicly inserted, but need to be registered in the YML
        yml.set("commands.reschedulerestart.description", "");
        yml.set("commands.reschedulerestart.usage", "");
        yml.set("commands.restarttime.description", "");
        yml.set("commands.restarttime.usage", "");

        commandCounter += 2;

        System.out.println("Finished writing all commands.");
        System.out.println("Amount of commands: " + commandCounter);

        System.out.println("Plugin file done");
        yml.save(ymlSourceFile);
    }

    private static void writeHtmlCommandlist(File endfile, String title, PlayerGroup maxGroup, PlayerGroup minGroup, String idpversion) throws SecurityException, IOException {
        if (endfile.exists()) {
            System.out.println("Old file found, removing...");
            endfile.delete();
            System.out.println("...removed");
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(endfile));

        int commandCounter = 0;
        List<Method> methods = new ArrayList<Method>();
        for (Class<?> cls : getCommandClasses()) {
            methods.addAll(Arrays.asList(cls.getDeclaredMethods()));
            System.out.println("Finished loading commands of the " + cls.getSimpleName() + " class.");
        }

        System.out.println("Sorting...");
        Collections.sort(methods, new Comparator<Method>() {
            public int compare(Method o1, Method o2) {
                if (o1 == o2 || o1.equals(o2)) {
                    return 0;
                }
                CommandMethod info1 = o1.getAnnotation(CommandMethod.class);
                CommandMethod info2 = o2.getAnnotation(CommandMethod.class);
                if (o1 == o2 || o1.equals(o2) || info1 == info2) {
                    return 0;
                }
                if (info1 != null && info2 != null) {
                    if (info1.permission().getGroup().id == info2.permission().getGroup().id) {
                        return (info1.aliases()[0].compareTo(info2.aliases()[0]));
                    }
                    return (info1.permission().getGroup().id > info2.permission().getGroup().id) ? -1 : 1;
                }
                return (info1 == null) ? 1 : -1;
            }
        });
        System.out.println("...Sorted");

        String tableClass = "ct";

        System.out.println("Writing Headers...");
        writer.append("<html><head>");
        writer.append("<title>" + title + "</title>");
        writer.append("<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"http://site.innectis.net/favicon.ico\">");
        writer.append(makeStyle(tableClass));

        writer.append("</head><body>");
        writer.append("<center><h2>" + title + "</h2>");

        SimpleDateFormat dateformat = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss", Locale.US);
        dateformat.setTimeZone(TimeZone.getTimeZone("EST"));

        writer.append("<h4>Generated on ").append(dateformat.format(new Date())).append(" (EST) by IDP v" + idpversion + "</h4>");
        writer.append("<h5>Arguments in []s are optional, whereas <>s are required.</h5>");

        writer.flush();
        System.out.println("...headers writtten");
        System.out.println("Writing command table...");

        writer.append("<div class='tbd'><table class='" + tableClass + "'>");
        writer.append("<thead><tr>");
        writer.append("<td title='The group that is needed for this command to work.' style='width: 9%;'>Group</td>");
        writer.append("<td title='The command.' style='width: 9%;'>Command</td>");
        writer.append("<td title='An alias for this command.' style='width: 10%;'>Alias(es)</td>");
        // writer.append("<td title='N: normal, S: SmartArgs, P: Parameters, A: Actionargs.' style='width: 2%;'>Args</td>");
        writer.append("<td title='Description of what the command does.' style='width: 40%;'>Description</td>");
        writer.append("<td title='How to use the command.' style='width: 30%;'>Usage</td>");
        writer.append("</tr></thead>");

        writer.append("<tbody>");
        for (Method method : methods) {
            CommandMethod info = method.getAnnotation(CommandMethod.class);
            if (info != null) {
                PlayerGroup checkGroup = info.permission().getGroup();

                if (maxGroup.equalsOrInherits(checkGroup) && !minGroup.inherits(checkGroup)
                        && !info.hideinlists()) {
                    commandCounter++;
                    String[] aliasList = new String[info.aliases().length - 1];
                    System.arraycopy(info.aliases(), 1, aliasList, 0, info.aliases().length - 1);

                    HTMLCommandElement element = new HTMLCommandElement(info.aliases()[0], aliasList, info.permission().getGroup(), info.usage(), info.description());
                    writer.append(element.toHtml());
                    writer.flush();
                }
            }
        }
        writer.append("</tbody></table></div>");

        writer.flush();
        System.out.println("...command table written");

        System.out.println("Writing end tags...");
        writer.append("</center></body>");
        writer.append("</html>");
        writer.flush();
        System.out.println("... end tags written");

        System.out.println("Amount of commands: " + commandCounter);

    }

    private static String makeStyle(String tableClass) {
        StringBuilder style = new StringBuilder(1024);
        style.append("<style>");
        style.append("body { font-family: verdana arial; margin-bottom: 100px;} ");
        style.append(".sb { margin: 0px 0 22px 0; -webkit-border-radius: 2px; -moz-border-radius: 2px; border-radius: 2px; "
                + " padding: 2px 0px 2px 10px; background-color: orange; display: block; float: left; } ");
        style.append(".sb > .l { marging-left:50px; width: 100px; display: block; float: left; } ");
        style.append(".sb > #i { float: left; display: block; width: 200px; } ");
        style.append(".sb > .r { margin: auto 10px auto 10px; color: gray; font-size: 12px; font-style: italic; } ");
        style.append("a.h, a.h:hover { cursor: default; color: black; text-decoration: none; } ");
        style.append(" .hidden { display: none; } ");
        style.append("table.").append(tableClass).append(" { width: 1024px; clear:both; } ");
        style.append("table.").append(tableClass).append(" thead tr td{ font-weight: bold; font-size: 14px; } ");
        style.append("table.").append(tableClass).append(" tbody tr td { border: 1px solid lightgray; } ");
        style.append("label.f { font-size: 10px; color: gray !important; font-style: italic; display: block; } ");
        style.append("label.f a:visited { color: gray; } ");
        for (PlayerGroup group : PlayerGroup.values()) {
            style.append("table.").append(tableClass).append(" tbody tr[").append(HTMLCommandElement.cssGroupDataAttr).append("=g").append(group.id).append("]");
            style.append(" td.").append(HTMLCommandElement.cssClassGroup).append("{");
            style.append("background-color: #").append(group.getPrefix().getTextColor().getHTMLColor()).append("; ");
            style.append("}");
        }
        style.append("</style>");
        return style.toString();
    }

    private static void writeBBCCode(File endfile, PlayerGroup maxGroup, PlayerGroup minGroup, String idpversion) throws SecurityException, IOException {
        if (endfile.exists()) {
            System.out.println("Old file found, removing...");
            endfile.delete();
            System.out.println("...removed");
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(endfile));

        int commandCounter = 0;
        List<Method> methods = new ArrayList<Method>();
        for (Class<?> cls : getCommandClasses()) {
            methods.addAll(Arrays.asList(cls.getDeclaredMethods()));
            System.out.println("Finished loading commands of the " + cls.getSimpleName() + " class.");
        }

        System.out.println("Sorting...");
        Collections.sort(methods, new Comparator<Method>() {
            public int compare(Method o1, Method o2) {
                if (o1 == o2 || o1.equals(o2)) {
                    return 0;
                }
                CommandMethod info1 = o1.getAnnotation(CommandMethod.class);
                CommandMethod info2 = o2.getAnnotation(CommandMethod.class);
                if (o1 == o2 || o1.equals(o2) || info1 == info2) {
                    return 0;
                }
                if (info1 != null && info2 != null) {
                    if (info1.permission().getGroup().id == info2.permission().getGroup().id) {
                        return (info1.aliases()[0].compareTo(info2.aliases()[0]));
                    }
                    return (info1.permission().getGroup().id < info2.permission().getGroup().id) ? -1 : 1;
                }
                return (info1 == null) ? -1 : 1;
            }
        });
        System.out.println("...Sorted");

        System.out.println("Writing command table...");

        writer.append("[table]\n");

        writer.append("[tr]");
        writer.append("[td][b][u]Group[/u][/b][/td]");
        writer.append("[td][b][u]Command[/u][/b][/td]");
        writer.append("[td][b][u]Alias(es)[/u][/b][/td]");
        //writer.append("[td][b][u]Type[/u][/b][/td]");
        writer.append("[td][b][u]Description[/u][/b][/td]");
        writer.append("[td][b][u]Usage[/u][/b][/td]");
        writer.append("[/tr]\n");

        for (Method method : methods) {
            CommandMethod info = method.getAnnotation(CommandMethod.class);
            if (info != null) {
                PlayerGroup checkGroup = info.permission().getGroup();

                if (maxGroup.equalsOrInherits(checkGroup) && !minGroup.inherits(checkGroup)
                        && !info.hideinlists()) {
                    commandCounter++;
                    String[] aliasList = new String[info.aliases().length - 1];
                    System.arraycopy(info.aliases(), 1, aliasList, 0, info.aliases().length - 1);

                    HTMLCommandElement element = new HTMLCommandElement(info.aliases()[0], aliasList, info.permission().getGroup(), info.usage(), info.description());
                    writer.append(element.toBBC());
                    writer.newLine();
                    writer.flush();
                }
            }
        }
        writer.append("[/table]\n");

        writer.flush();
        System.out.println("...command table written");

        System.out.println("Writing generation date...");
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US);
        dateformat.setTimeZone(TimeZone.getTimeZone("GMT"));
        writer.append("[size=7pt][i]Generated on ").append(dateformat.format(new Date())).append(" (GMT) by IDP v" + idpversion + " [/i][/size]");
        writer.flush();
        System.out.println("...generation date written");

        System.out.println("Amount of commands: " + commandCounter);

    }

    public static String titleCase(String str) {
        if (str == null) {
            return str;
        }
        char[] arr = str.toCharArray();
        if (arr.length == 1) {
            return str.toUpperCase();
        } else if (arr.length > 1) {
            return Character.toUpperCase(arr[0]) + str.substring(1);
        } else {
            return str;
        }
    }
}

class HTMLCommandElement {

    private String command;
    private String[] aliases;
    private PlayerGroup group;
    private String usage;
    private String info;

    public HTMLCommandElement(String command, String[] aliases, PlayerGroup group, String usage, String info) {
        this.command = command;
        this.aliases = aliases;
        this.group = group;
        this.usage = usage;
        this.info = info;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getCommand() {
        return command;
    }

    public PlayerGroup getGroup() {
        return group;
    }

    public String getUsage() {
        return usage;
    }

    public String getInfo() {
        return info;
    }
    public static final String cssClassRow = "r";
    public static final String cssClassGroup = "g";
    public static final String cssClassCommand = "c";
    public static final String cssClassAlias = "a";
    public static final String cssClassInfo = "i";
    public static final String cssClassUsage = "u";
    public static final String cssGroupDataAttr = "d";

    public String toHtml() {
        StringBuilder sb = new StringBuilder(2048);

        sb.append("<tr class='").append(cssClassRow).append("' ").append(cssGroupDataAttr).append("='g").append(group.id).append("'>");
        // group
        sb.append("<td class='").append(cssClassGroup).append("'>").append(group.name).append("</td>");
        // command
        sb.append("<td class='").append(cssClassCommand).append("'>").append(command).append("</td>");

        // aliases
        sb.append("<td class='").append(cssClassAlias).append("'>");
        if (aliases.length > 0) {
            sb.append(aliases[0]);
            for (int i = 1; i < aliases.length; i++) {
                sb.append(", ").append(aliases[i]);
            }
        } else {
            sb.append("&nbsp;");
        }
        sb.append("</td>");


        // info
        sb.append("<td class='").append(cssClassInfo).append("'>").append(StringEscapeUtils.escapeHtml(info)).append("</td>");

        // usage
        sb.append("<td class='").append(cssClassUsage).append("'>").append(StringEscapeUtils.escapeHtml(usage)).append("</td>");

        // focuscell
        sb.append("<td style='border:none;' valign='top'><a class='h' href='#'>&nbsp;</a></td>");

        sb.append("</tr>");
        return sb.toString();
    }

    public String toBBC() {
        StringBuilder sb = new StringBuilder(2048);

        sb.append("[tr]");
        // group
        sb.append("[td]").append(group.name).append("[/td]");
        // command
        sb.append("[td]").append(command).append("[/td]");

        // aliases
        sb.append("[td]");
        if (aliases.length > 0) {
            sb.append(aliases[0]);
            for (int i = 1; i < aliases.length; i++) {
                sb.append(", ").append(aliases[i]);
            }
        } else {
            sb.append("[i] [/i]");
        }
        sb.append("[/td]");

        // info
        sb.append("[td]").append(info).append("[/td]");

        // usage
        sb.append("[td]").append(usage).append("[/td]");

        sb.append("[/tr]");
        return sb.toString();
    }

}

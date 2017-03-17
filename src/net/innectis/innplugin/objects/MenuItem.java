package net.innectis.innplugin.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.innectis.innplugin.IdpCommandSender;
import net.innectis.innplugin.player.chat.ChatColor;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Hret
 */
public class MenuItem {

    private int id;
    private String name;
    private List<Object> content;

    public MenuItem(int id, String name, List<Object> content) {
        this.id = id;
        this.name = name;
        if (content == null) {
            throw new NullPointerException("Missing information! ");
        }
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void printContent(IdpCommandSender<? extends CommandSender> sender) {
        sender.printRaw(ChatColor.AQUA + "Listing: " + name);
        for (Object obj : content) {
            if (obj instanceof String) {
                sender.print(ChatColor.GREEN, " " + obj);
            } else if (obj instanceof MenuItem) {
                MenuItem item = (MenuItem) obj;
                sender.print(ChatColor.DARK_GREEN, "(" + item.getId() + ") " + item.getName());
            }
        }
    }

    public MenuItem findItem(String name) {
        for (Object obj : content) {
            if (obj instanceof MenuItem) {
                MenuItem item = (MenuItem) obj;
                if (item.getName().equalsIgnoreCase(name)) {
                    return item;
                } else {
                    item = item.findItem(name);
                    if (item != null) {
                        return item;
                    }
                }
            }
        }
        return null;
    }

    public MenuItem findItem(int id) {
        for (Object obj : content) {
            if (obj instanceof MenuItem) {
                MenuItem item = (MenuItem) obj;
                if (item.getId() == id) {
                    return item;
                } else {
                    item = item.findItem(id);
                    if (item != null) {
                        return item;
                    }
                }
            }
        }
        return null;
    }

    public static MenuItem loadFile(YamlConfiguration config) {
        List<Object> obj = new ArrayList<Object>();

        Set<String> rootKeys = config.getKeys(false);
        for (String str : rootKeys) {
            obj.add(buildList(config, str));
        }

        return new MenuItem(0, ".", obj);
    }

    private static MenuItem buildList(YamlConfiguration config, String path) {
        MemorySection section = (MemorySection) config.get(path);
        Object obj;
        List<Object> contentList = new ArrayList<Object>();
        for (String key : section.getKeys(false)) {
            obj = config.get(path + "." + key);
            if (obj instanceof String) {
                contentList.add(obj.toString());
            } else if (obj instanceof MemorySection) {
                contentList.add(buildList(config, path + "." + key));
            }
        }

        String[] name = (path.contains(".") ? path.substring(path.lastIndexOf(".") + 1) : path).split(";");
        if (name.length != 2) {
            return null;
        }
        return new MenuItem(Integer.parseInt(name[0]), name[1], contentList);
    }

}
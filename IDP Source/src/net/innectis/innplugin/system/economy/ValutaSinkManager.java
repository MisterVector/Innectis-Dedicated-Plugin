package net.innectis.innplugin.system.economy;

import net.innectis.innplugin.handlers.ConfigValueHandler;

/**
 * A manager that manages the total sink value of valutas
 *
 * @author AlphaBlend
 */
public class ValutaSinkManager {

    /**
     * Adds the specified valutas to the valuta sink total
     * @param amount
     */
    public static void addToSink(int amount) {
        Integer sink = 0;

        String sinkString = ConfigValueHandler.getValue("valuta_sink");

        if (sinkString != null) {
            sink = Integer.parseInt(sinkString);
        }

        sink += amount;
        ConfigValueHandler.saveValue("valuta_sink", sink.toString());
    }

    /**
     * Gets the total of the valuta sink
     * @return
     */
    public static int getSink() {
        Integer sink = 0;
        String sinkString = ConfigValueHandler.getValue("valuta_sink");

        if (sinkString != null) {
            sink = Integer.parseInt(sinkString);
        }

        return sink;
    }

}

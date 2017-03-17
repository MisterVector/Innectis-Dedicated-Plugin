package net.innectis.innplugin.objects.owned.traits;

import java.util.List;
import java.util.Map;
import net.innectis.innplugin.objects.owned.InnectisLot;
import net.innectis.innplugin.objects.owned.LotTag;
import net.innectis.innplugin.player.PlayerCredentials;
import org.bukkit.Location;

/**
 * Traits for a lot
 *
 * @author AlphaBlend
 */
public class InnectisLotTraits extends InnectisOwnedObjectTraits {

    private String lotName;
    private Map<PlayerCredentials, Long> banned;
    private List<PlayerCredentials> safelist;
    private String enterMsg;
    private String exitMsg;
    private boolean hidden;
    private LotTag tag;

    public InnectisLotTraits (InnectisLot lot) {
        super(lot);

        this.lotName = lot.getLotName();
        this.banned = lot.getBanned();
        this.safelist = lot.getSafelist();
        this.enterMsg = lot.getEnterMsg();
        this.exitMsg = lot.getExitMsg();
        this.hidden = lot.getHidden();
        this.tag = lot.getTag().clone();
    }

    /**
     * Applies this lot's traits to the target
     * @param lot
     */
    public void applyTraits(InnectisLot lot) {
        super.applyTraits(lot);

        lot.setLotName(lotName);
        lot.setBanned(banned);
        lot.setSafelist(safelist);
        lot.setEnterMsg(enterMsg);
        lot.setExitMsg(exitMsg);
        lot.setHidden(hidden);
        lot.setTag(tag);
    }

}

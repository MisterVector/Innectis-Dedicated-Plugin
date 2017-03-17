package net.innectis.innplugin.objects.owned.traits;

import java.util.List;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.objects.owned.InnectisBookcase;
import net.innectis.innplugin.player.PlayerCredentials;

/**
 * Traits for innectis bookcases
 *
 * @author AlphaBlend
 */
public class InnectisBookcaseTraits extends InnectisOwnedObjectTraits {

    private IdpItemStack[] items = null;
    private String caseTitle;

    public InnectisBookcaseTraits(InnectisBookcase bookcase) {
        super(bookcase);

        this.items = bookcase.getItems();
        this.caseTitle = bookcase.getCaseTitle();
    }

    /**
     * Applies this bookcase's traits to the target
     * @param bookcase
     */
    public void applyTraits(InnectisBookcase bookcase) {
        super.applyTraits(bookcase);

        bookcase.setItems(items);
        bookcase.setCaseTitle(caseTitle);
    }

}

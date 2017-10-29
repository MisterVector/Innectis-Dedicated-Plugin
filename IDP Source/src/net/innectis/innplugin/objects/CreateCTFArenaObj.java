package net.innectis.innplugin.objects;

import net.innectis.innplugin.handlers.CTFHandler.CreateGameMode;
import net.innectis.innplugin.location.IdpWorldRegion;
import net.innectis.innplugin.objects.owned.InnectisLot;

/**
 * Object that handles CTF arena creation
 *
 * @author AlphaBlend
 */
public class CreateCTFArenaObj {

    private InnectisLot lot;
    private IdpWorldRegion startRegion;
    private CreateGameMode mode;

    public CreateCTFArenaObj(InnectisLot lot, IdpWorldRegion startRegion, CreateGameMode mode) {
        this.lot = lot;
        this.mode = mode;
        this.startRegion = startRegion;
    }

    public InnectisLot returnLot() {
        return lot;
    }

    public CreateGameMode returnGameMode() {
        return mode;
    }

    public IdpWorldRegion getStartRegion() {
        return startRegion;
    }

    public void setGameMode(CreateGameMode mode) {
        this.mode = mode;
    }

}

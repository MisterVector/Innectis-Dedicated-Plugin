package net.innectis.innplugin.objects;

import java.util.HashMap;
import java.util.Map;
import net.innectis.innplugin.inventory.IdpContainer;
import net.innectis.innplugin.items.IdpItemStack;
import net.innectis.innplugin.items.IdpMaterial;

/**
 * A class that handles the transfer of materials
 * from one container to another
 *
 * @author AlphaBlend
 */
public class IdpContainerTransfer {

    private Map<IdpMaterial, Integer> materialAdded = new HashMap<IdpMaterial, Integer>();
    private IdpContainer sourceContainer;
    private IdpContainer destinationContainer;

    public IdpContainerTransfer() {
        this(null, null);
    }

    public IdpContainerTransfer(IdpContainer sourceContainer, IdpContainer destinationContainer) {
        this.sourceContainer = sourceContainer;
        this.destinationContainer = destinationContainer;
    }

    /**
     * Sets the source container of this transfer
     * @param sourceContainer
     */
    public void setSourceContainer(IdpContainer sourceContainer) {
        this.sourceContainer = sourceContainer;
    }

    /**
     * Sets the destination container of this transfer
     * @param destinationContainer
     */
    public void setDestinationContainer(IdpContainer destinationContainer) {
        this.destinationContainer = destinationContainer;
    }

    /**
     * Process the transaction of containers and returns how many
     * items were transferred. Does not include a target
     * material or disabled minimum/maximum indexes
     * @return
     */
    public int process() {
        return process(null);
    }

    /**
     * Processes the transaction of containers and returns how many
     * items were transferred. Includes a target material and
     * does not include disabled minimum/maximum indexes
     * @param targetMaterial
     * @return
     */
    public int process(IdpMaterial targetMaterial) {
        return process(targetMaterial, -1, -1);
    }

    /**
     * Processes the transaction of containers and returns how many
     * that were added to the destination container
     * @return
     */
    public int process(IdpMaterial targetMaterial, int disabledMinIdx, int disabledMaxIdx) {
        int totalAdded = 0;

        for (int i = 0; i < sourceContainer.size(); i++) {
            IdpItemStack stack = sourceContainer.getItemAt(i);

            // Don't process null items or air material
            if (stack == null || stack.getMaterial() == IdpMaterial.AIR) {
                continue;
            }

            // Do not allow any potential disallowed index
            if (disabledMinIdx > -1 && disabledMaxIdx > -1) {
                if (i >= disabledMinIdx && i <= disabledMaxIdx) {
                    continue;
                }
            }

            IdpMaterial mat = stack.getMaterial();

            // If using a target material, don't allow anything else
            if (targetMaterial != null && targetMaterial != mat) {
                continue;
            }

            int remain = destinationContainer.addMaterialToStack(stack);
            int added = (stack.getAmount() - remain);

            if (remain > 0) {
                if (remain < stack.getAmount()) {
                    stack.setAmount(remain);
                }
            } else {
                stack = null;
            }

            if (added > 0) {
                sourceContainer.setItemAt(i, stack);

                if (!materialAdded.containsKey(mat)) {
                    materialAdded.put(mat, added);
                } else {
                    int previousAmount = materialAdded.get(mat);
                    materialAdded.put(mat, previousAmount + added);
                }

                totalAdded += added;
            }
        }

        return totalAdded;
    }

    /**
     * Gets all the material transferred between containers
     * @return
     */
    public Map<IdpMaterial, Integer> getMaterialAdded() {
        return materialAdded;
    }

}

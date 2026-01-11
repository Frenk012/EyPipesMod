package frenk.eypipes.client.renderer;

import frenk.eypipes.client.model.PipeModel;

/**
 * GeckoLib 4 item renderer for the original PipeItem.
 * Extends BasePipeRenderer which handles all first-person positioning logic.
 */
public class PipeItemRenderer extends BasePipeRenderer {
    public PipeItemRenderer() {
        super(new PipeModel());
    }
}

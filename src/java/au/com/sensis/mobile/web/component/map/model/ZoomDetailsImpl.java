package au.com.sensis.mobile.web.component.map.model;


/**
 * Default {@link ZoomDetails} implementation.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ZoomDetailsImpl implements ZoomDetails {

    /**
     * Zoom level that {@link au.com.sensis.wireless.manager.mapping.MapUrl#getZoom()}
     * corresponds to.
     * Required by AJAX maps that talk to EMS directly.
     */
    private final int emsZoom;

    private final boolean atMinimumZoom;
    private final boolean atMaximumZoom;

    /**
     * Default constructor.
     *
     * @param emsZoom See {@link #getEmsZoom()}.
     * @param atMinimumZoom See {@link #isAtMaximumZoom()}.
     * @param atMaximumZoom See {@link #isAtMaximumZoom()}.
     */
    public ZoomDetailsImpl(final int emsZoom, final boolean atMinimumZoom,
            final boolean atMaximumZoom) {
        super();
        this.emsZoom = emsZoom;
        this.atMinimumZoom = atMinimumZoom;
        this.atMaximumZoom = atMaximumZoom;
    }

    /**
     * {@inheritDoc}
     */
    public int getEmsZoom() {
        return emsZoom;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAtMaximumZoom() {
        return atMaximumZoom;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAtMinimumZoom() {
        return atMinimumZoom;
    }

}

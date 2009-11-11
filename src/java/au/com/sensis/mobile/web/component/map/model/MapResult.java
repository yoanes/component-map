package au.com.sensis.mobile.web.component.map.model;

public class MapResult {

    public static enum Status {

        /**
         * Map was retrieved. ie. {@link MapResult#getMapUrl()} is non-blank.
         */
        MAP_RETRIEVED,

        /**
         * The client is responsible for the actual map retrieval.
         * ie. {@link MapResult#getMapUrl()} is blank. Particularly intended
         * for high end devices and is governed by the device repository.
         */
        MAP_RETRIEVAL_CLIENT_RESPONSIBLE,

        /**
         * Map retrieval was attempted but failed due to an error.
         * TODO: do we need finer grain error reporting?
         */
        MAP_RETRIEVAL_FAILED
    }

    private MapState mapState;

    private String mapUrl;

    private Status status;

    public static MapResult createMapRetrievedResult(final MapState mapState, final String mapUrl) {
        final MapResult mapResult = new MapResult();
        mapResult.mapUrl = mapUrl;
        mapResult.mapState = mapState;
        mapResult.status = Status.MAP_RETRIEVED;
        return mapResult;
    }

    public static MapResult createMapRetrievalClientResponsible(final MapState mapState) {
        final MapResult mapResult = new MapResult();
        mapResult.mapState = mapState;
        mapResult.status = Status.MAP_RETRIEVAL_CLIENT_RESPONSIBLE;
        return mapResult;
    }

    public static MapResult createMapRetrievalFailedResult(final MapState mapState) {
        final MapResult mapResult = new MapResult();
        mapResult.mapState = mapState;
        mapResult.status = Status.MAP_RETRIEVAL_FAILED;
        return mapResult;
    }

    private MapResult() {

    }

    public boolean isMapRetrieved() {
        return Status.MAP_RETRIEVED.equals(getStatus());
    }

    public boolean isMapRetrievalFailed() {
        return Status.MAP_RETRIEVAL_FAILED.equals(getStatus());
    }

    public boolean isMapRetrievalClientResponsible() {
        return Status.MAP_RETRIEVAL_CLIENT_RESPONSIBLE.equals(getStatus());
    }

    /**
     * @return the mapUrl
     */
    public String getMapUrl() {
        return mapUrl;
    }

    /**
     * @return the mapState
     */
    public MapState getMapState() {
        return mapState;
    }

    /**
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

}

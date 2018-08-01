package Prover.Tableau;

enum ExtendResult {
    SUCCESS, FAILURE, STEPS_COMPLETE, CHECK_NEXT_HUE, START_NEW_FIRST_HUE;
    protected FirstHueUpLinkTree checkedUpLinks;
    protected UpLinkTree upLinks;
}

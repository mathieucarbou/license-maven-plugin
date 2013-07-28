AO_FOM_Cocoon.suicide = new Continuation();

AO_FOM_Cocoon.prototype.sendPageAndWait = function(uri, bizData) {
    this.sendPage(uri, bizData, new Continuation());
    AO_FOM_Cocoon.suicide();
}

AO_FOM_Cocoon.prototype.handleContinuation = function(k, wk) {
    k(wk);
}

package be.rubus.microstream.helidon.demo.service;

import be.rubus.microstream.helidon.demo.database.DB;
import be.rubus.microstream.helidon.demo.database.Locks;
import be.rubus.microstream.helidon.demo.database.Root;

public abstract class AbstractService {

    protected final Locks locks;

    protected final Root root;

    protected AbstractService() {
        root = DB.getRoot();
        locks = new Locks();
    }
}

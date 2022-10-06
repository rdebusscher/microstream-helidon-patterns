package be.rubus.microstream.helidon.demo.service;

import be.rubus.microstream.helidon.demo.database.Locks;
import be.rubus.microstream.helidon.demo.database.Root;
import io.helidon.integrations.microstream.cdi.MicrostreamStorage;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

import javax.inject.Inject;

public abstract class AbstractService {

    @Inject
    @MicrostreamStorage(configNode = "one.microstream.storage.bookstore")
    protected EmbeddedStorageManager storageManager;

    @Inject
    protected Locks locks;


    @Inject
    protected Root root;

}

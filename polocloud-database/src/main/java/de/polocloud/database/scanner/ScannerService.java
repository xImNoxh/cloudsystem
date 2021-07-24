package de.polocloud.database.scanner;

import de.polocloud.database.DatabaseService;

public class ScannerService {

    public ScannerService(ClassLoader classLoader, DatabaseService databaseService, String packing) {
        new EntityProcessor(classLoader, databaseService, packing);
        new RepositoryProcessor(classLoader, databaseService, packing);
    }

}
